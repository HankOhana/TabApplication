package com.henadz.sample.tabapplication.ui.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henadz.sample.tabapplication.domain.model.CellData
import com.henadz.sample.tabapplication.domain.usecase.ClearSessionUseCase
import com.henadz.sample.tabapplication.domain.usecase.GetTableUseCase
import com.henadz.sample.tabapplication.domain.usecase.ResetTableUseCase
import com.henadz.sample.tabapplication.domain.usecase.ToggleCellColorUseCase
import com.henadz.sample.tabapplication.domain.usecase.UpdateCellDataUseCase
import com.henadz.sample.tabapplication.ui.strings.UiStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TableViewModel(
    private val rows: Int,
    private val cols: Int,
    private val getTableUseCase: GetTableUseCase,
    private val toggleCellColorUseCase: ToggleCellColorUseCase,
    private val updateCellDataUseCase: UpdateCellDataUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val resetTableUseCase: ResetTableUseCase,
) : ViewModel() {
    private val _editingCell = MutableStateFlow<UiCell?>(null)

    private val cellsFlow: Flow<ImmutableList<UiCell>> = flow {
        emitAll(
            getTableUseCase(rows, cols)
                .runningFold<List<CellData>, ImmutableList<UiCell>>(persistentListOf()) { prev, domain ->
                    domain.toOptimizedUiCells(prev)
                },
        )
    }

    val state: StateFlow<TableUiState> =
        combine(cellsFlow, _editingCell) { cells, editingCell ->
            TableUiState(
                isLoading = cells.isEmpty(),
                rows = rows,
                columns = cols,
                cells = cells,
                editingCell = editingCell,
            )
        }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = TableUiState(rows = rows, columns = cols),
            )

    private val _effects = Channel<TableUiEffect>(Channel.BUFFERED)
    val effects: Flow<TableUiEffect> = _effects.receiveAsFlow()

    fun handleIntent(intent: TableUiIntent) {
        when (intent) {
            is TableUiIntent.CellClicked -> toggleColor(intent.id)
            is TableUiIntent.CellDoubleClicked -> openEditDialog(intent.id)
            is TableUiIntent.CellDataChanged -> updateCell(intent.id, intent.text)
            is TableUiIntent.CloseEditDialog -> closeEditDialog()
            is TableUiIntent.ExitSession -> exitSession()
            is TableUiIntent.ResetTable -> resetTable()
        }
    }

    private fun toggleColor(id: String) {
        viewModelScope.launch { toggleCellColorUseCase(id) }
    }

    private fun openEditDialog(id: String) {
        val index = id.toIntOrNull() ?: return
        val cell = state.value.cells.getOrNull(index) ?: return
        _editingCell.value = cell
    }

    private fun updateCell(
        id: String,
        text: String,
    ) {
        viewModelScope.launch {
            updateCellDataUseCase(id, text)
            if (_editingCell.value?.id == id) {
                _editingCell.value = null
            }
        }
    }

    private fun closeEditDialog() {
        _editingCell.value = null
    }

    private fun exitSession() {
        viewModelScope.launch {
            clearSessionUseCase()
            _effects.send(TableUiEffect.NavigateBack)
        }
    }

    private fun resetTable() {
        viewModelScope.launch {
            resetTableUseCase(rows, cols)
            _effects.send(TableUiEffect.ShowToast(UiStrings.TOAST_RESET_SUCCESS))
        }
    }
}

private fun List<CellData>.toOptimizedUiCells(current: ImmutableList<UiCell>): ImmutableList<UiCell> {
    val builder = persistentListOf<UiCell>().builder()
    for (i in indices) {
        val domain = this[i]
        val prev = current.getOrNull(i)
        builder.add(
            if (prev != null &&
                prev.id == domain.id &&
                prev.text == domain.text &&
                prev.isGreen == domain.isGreen
            ) {
                prev
            } else {
                domain.toUiCell()
            },
        )
    }
    return builder.build()
}

private fun CellData.toUiCell() =
    UiCell(
        id = id,
        text = text,
        isGreen = isGreen,
    )
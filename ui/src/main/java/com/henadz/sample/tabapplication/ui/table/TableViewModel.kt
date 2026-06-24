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
    private val _editingCellId = MutableStateFlow<String?>(null)

    private val cellsFlow: Flow<ImmutableList<UiCell>> = flow {
        emitAll(
            getTableUseCase(rows, cols)
                .runningFold<List<CellData>, ImmutableList<UiCell>>(persistentListOf()) { prev, domain ->
                    domain.toOptimizedUiCells(prev)
                },
        )
    }

    val state: StateFlow<TableUiState> =
        combine(cellsFlow, _editingCellId) { cells, editingCellId ->
            TableUiState(
                isLoading = cells.isEmpty(),
                rows = rows,
                columns = cols,
                cells = cells,
                editingCellId = editingCellId,
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
            is TableUiIntent.CellDoubleClicked -> startEdit(intent.id)
            is TableUiIntent.CommitCellEdit -> commitEdit(intent.id, intent.text)
            is TableUiIntent.CancelCellEdit -> cancelEdit()
            is TableUiIntent.ExitSession -> exitSession()
            is TableUiIntent.ResetTable -> resetTable()
        }
    }

    private fun toggleColor(id: String) {
        viewModelScope.launch { toggleCellColorUseCase(id) }
    }

    private fun startEdit(id: String) {
        _editingCellId.value = id
    }

    private fun cancelEdit() {
        _editingCellId.value = null
    }

    private fun commitEdit(
        id: String,
        text: String,
    ) {
        if (_editingCellId.value == null) return
        _editingCellId.value = null
        val index = id.toIntOrNull() ?: return
        val currentText = state.value.cells.getOrNull(index)?.text ?: return
        if (text != currentText) {
            viewModelScope.launch { updateCellDataUseCase(id, text) }
        }
    }

    private fun exitSession() {
        viewModelScope.launch {
            clearSessionUseCase()
            _effects.send(TableUiEffect.NavigateBack)
        }
    }

    private fun resetTable() {
        _editingCellId.value = null
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
package com.henadz.sample.tabapplication.ui.table

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TableUiState(
    val isLoading: Boolean = true,
    val rows: Int = 0,
    val columns: Int = 0,
    val cells: ImmutableList<UiCell> = persistentListOf(),
    val editingCell: UiCell? = null,
)

sealed interface TableUiIntent {
    data class CellClicked(
        val id: String,
    ) : TableUiIntent

    data class CellDoubleClicked(
        val id: String,
    ) : TableUiIntent

    data class CellDataChanged(
        val id: String,
        val text: String,
    ) : TableUiIntent

    data object CloseEditDialog : TableUiIntent

    data object ResetTable : TableUiIntent

    data object ExitSession : TableUiIntent
}

sealed interface TableUiEffect {
    data class ShowToast(
        val msg: String,
    ) : TableUiEffect

    data object NavigateBack : TableUiEffect
}
package com.henadz.sample.tabapplication.ui.table

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henadz.sample.tabapplication.ui.components.PosAlertDialog
import com.henadz.sample.tabapplication.ui.strings.UiStrings
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme

@Composable
fun TableScreen(
    viewModel: TableViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showExitDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    BackHandler { showExitDialog = true }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TableUiEffect.ShowToast ->
                    Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                is TableUiEffect.NavigateBack -> onBack()
            }
        }
    }

    val onCellClick =
        remember {
            { id: String -> viewModel.handleIntent(TableUiIntent.CellClicked(id)) }
        }
    val onCellDoubleClick =
        remember {
            { id: String -> viewModel.handleIntent(TableUiIntent.CellDoubleClicked(id)) }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TabAppTheme.colors.background)
                .statusBarsPadding(),
    ) {
        if (!state.isLoading) {
            ColumnHeaderRow(columnCount = state.columns)
        }

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = TabAppTheme.colors.onBackground)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(state.columns),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = state.cells,
                        key = { cell -> cell.id },
                        contentType = { "cell" },
                    ) { cell ->
                        TableCell(
                            cell = cell,
                            onClick =
                                remember(cell.id, onCellClick) {
                                    { onCellClick(cell.id) }
                                },
                            onDoubleClick =
                                remember(cell.id, onCellDoubleClick) {
                                    { onCellDoubleClick(cell.id) }
                                },
                        )
                    }
                }
            }
        }

        BottomActionBar(
            showReset = !state.isLoading,
            onExitClick = { showExitDialog = true },
            onResetClick = { showResetDialog = true },
        )
    }

    state.editingCell?.let { cell ->
        EditDialog(
            cell = cell,
            onConfirm = { text ->
                viewModel.handleIntent(TableUiIntent.CellDataChanged(cell.id, text))
            },
            onDismiss = { viewModel.handleIntent(TableUiIntent.CloseEditDialog) },
        )
    }

    if (showExitDialog) {
        PosAlertDialog(
            title = UiStrings.EXIT_TABLE_TITLE,
            message = UiStrings.EXIT_TABLE_MESSAGE,
            confirmLabel = UiStrings.LEAVE,
            dismissLabel = UiStrings.STAY,
            onConfirm = {
                showExitDialog = false
                viewModel.handleIntent(TableUiIntent.ExitSession)
            },
            onDismiss = { showExitDialog = false },
        )
    }

    if (showResetDialog) {
        PosAlertDialog(
            title = UiStrings.RESET_TABLE_TITLE,
            message = UiStrings.RESET_TABLE_MESSAGE,
            confirmLabel = UiStrings.CONFIRM,
            dismissLabel = UiStrings.CANCEL,
            onConfirm = {
                showResetDialog = false
                viewModel.handleIntent(TableUiIntent.ResetTable)
            },
            onDismiss = { showResetDialog = false },
        )
    }
}

@Composable
private fun ColumnHeaderRow(columnCount: Int) {
    val colors = TabAppTheme.colors
    val dimens = TabAppTheme.dimens
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(dimens.columnHeaderHeight)
                .background(colors.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(columnCount) { index ->
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(dimens.cellBorderWidth, colors.border, RectangleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = UiStrings.columnHeader(index),
                    color = colors.onBackground,
                    style = TabAppTheme.typography.inputLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    showReset: Boolean,
    onExitClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    val colors = TabAppTheme.colors
    val dimens = TabAppTheme.dimens
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(colors.surfaceVariant)
                .border(dimens.cellBorderWidth, colors.border, RectangleShape),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(dimens.touchTargetHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onExitClick,
                shape = RectangleShape,
                modifier = Modifier.fillMaxHeight(),
            ) {
                Text(
                    text = UiStrings.EXIT,
                    color = colors.onBackground,
                    style = TabAppTheme.typography.dialogButton,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (showReset) {
                TextButton(
                    onClick = onResetClick,
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Text(
                        text = UiStrings.RESET,
                        color = colors.error,
                        style = TabAppTheme.typography.dialogButton,
                    )
                }
            }
        }
    }
}

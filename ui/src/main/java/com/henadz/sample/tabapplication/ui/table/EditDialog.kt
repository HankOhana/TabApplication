package com.henadz.sample.tabapplication.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.henadz.sample.tabapplication.ui.components.TabAppTextField
import com.henadz.sample.tabapplication.ui.strings.UiStrings
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme
import com.henadz.sample.tabapplication.ui.util.rememberDebouncedClick

@Composable
internal fun EditDialog(
    cell: UiCell,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable(cell.id) { mutableStateOf(cell.text) }
    val colors = TabAppTheme.colors
    val dimens = TabAppTheme.dimens

    val onConfirmDebounced = rememberDebouncedClick { onConfirm(text) }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(min = dimens.dialogMinWidth, max = dimens.dialogMaxWidth)
                    .fillMaxWidth()
                    .imePadding()
                    .background(colors.surfaceVariant, RectangleShape)
                    .border(dimens.dialogBorderWidth, colors.border, RectangleShape)
                    .padding(dimens.dialogPadding),
        ) {
            Text(
                text = UiStrings.EDIT_CELL,
                color = colors.onBackground,
                style = TabAppTheme.typography.dialogLabel,
            )

            Spacer(Modifier.height(dimens.contentSpacing))

            TabAppTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(dimens.contentSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier =
                        Modifier.defaultMinSize(
                            minWidth = dimens.dialogButtonMinWidth,
                            minHeight = dimens.touchTargetHeight,
                        ),
                ) {
                    Text(
                        text = UiStrings.CANCEL,
                        color = colors.onBackground,
                        style = TabAppTheme.typography.dialogButton,
                    )
                }
                TextButton(
                    onClick = onConfirmDebounced,
                    modifier =
                        Modifier.defaultMinSize(
                            minWidth = dimens.dialogButtonMinWidth,
                            minHeight = dimens.touchTargetHeight,
                        ),
                ) {
                    Text(
                        text = UiStrings.SAVE,
                        color = colors.onBackground,
                        style = TabAppTheme.typography.dialogButton,
                    )
                }
            }
        }
    }
}
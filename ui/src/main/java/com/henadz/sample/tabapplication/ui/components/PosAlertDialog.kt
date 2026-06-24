package com.henadz.sample.tabapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme
import com.henadz.sample.tabapplication.ui.util.rememberDebouncedClick

@Composable
internal fun PosAlertDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = TabAppTheme.colors
    val dimens = TabAppTheme.dimens
    val typography = TabAppTheme.typography

    val onConfirmDebounced = rememberDebouncedClick(onClick = onConfirm)

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
                    .background(colors.surfaceVariant, RectangleShape)
                    .border(dimens.dialogBorderWidth, colors.border, RectangleShape)
                    .padding(dimens.dialogPadding),
        ) {
            Text(
                text = title,
                color = colors.onBackground,
                style = typography.dialogLabel,
            )

            Spacer(Modifier.height(dimens.contentSpacing))

            Text(
                text = message,
                color = colors.onBackground,
                style = typography.cellText,
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
                        text = dismissLabel,
                        color = colors.onBackground,
                        style = typography.dialogButton,
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
                        text = confirmLabel,
                        color = colors.error,
                        style = typography.dialogButton,
                    )
                }
            }
        }
    }
}

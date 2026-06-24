package com.henadz.sample.tabapplication.ui.table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TableCell(
    cell: UiCell,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
) {
    val colors = TabAppTheme.colors
    val dimens = TabAppTheme.dimens
    val bgColor: Color = if (cell.isGreen) colors.primary else colors.surface

    val haptic = LocalHapticFeedback.current
    val onClickWithHaptic =
        remember(haptic, onClick) {
            {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                onClick()
            }
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(dimens.cellHeight)
                .drawBehind { drawRect(bgColor) }
                .border(dimens.cellBorderWidth, colors.border, RectangleShape)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClickWithHaptic,
                    onDoubleClick = onDoubleClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = cell.text,
            color = if (cell.isGreen) colors.onPrimary else colors.onBackground,
            style = TabAppTheme.typography.cellText,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}
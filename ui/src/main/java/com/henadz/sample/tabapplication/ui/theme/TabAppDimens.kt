package com.henadz.sample.tabapplication.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class TabAppDimens(
    // Grid
    val cellHeight: Dp,
    val columnHeaderHeight: Dp,
    val cellBorderWidth: Dp,
    val dialogBorderWidth: Dp,
    // Layout
    val screenPadding: Dp,
    val contentSpacing: Dp,
    val sectionSpacing: Dp,
    val formMaxWidth: Dp,
    // Touch targets & dialogs
    val touchTargetHeight: Dp,
    val dialogMinWidth: Dp,
    val dialogMaxWidth: Dp,
    val dialogPadding: Dp,
    val dialogButtonMinWidth: Dp,
)

internal fun tabAppDimens() =
    TabAppDimens(
        cellHeight = 64.dp,
        columnHeaderHeight = 36.dp,
        cellBorderWidth = 0.5.dp,
        dialogBorderWidth = 1.dp,
        screenPadding = 32.dp,
        contentSpacing = 16.dp,
        sectionSpacing = 8.dp,
        formMaxWidth = 400.dp,
        touchTargetHeight = 56.dp,
        dialogMinWidth = 320.dp,
        dialogMaxWidth = 480.dp,
        dialogPadding = 20.dp,
        dialogButtonMinWidth = 96.dp,
    )

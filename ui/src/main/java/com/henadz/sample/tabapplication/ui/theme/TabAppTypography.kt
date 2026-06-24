package com.henadz.sample.tabapplication.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Immutable
data class TabAppTypography(
    val cellText: TextStyle, // 12sp mono — table cells
    val inputLabel: TextStyle, // 11sp mono + 1sp tracking — field labels
    val inputSupporting: TextStyle, // 10sp mono — error/helper text below fields
    val heading: TextStyle, // 18sp mono + 4sp tracking — screen titles
    val button: TextStyle, // mono + 4sp tracking — primary action buttons
    val dialogLabel: TextStyle, // 12sp mono + 3sp tracking — dialog titles
    val dialogButton: TextStyle, // mono + 2sp tracking — dialog action buttons
)

internal fun defaultTabAppTypography() =
    TabAppTypography(
        cellText =
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
            ),
        inputLabel =
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            ),
        inputSupporting =
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
            ),
        heading =
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                letterSpacing = 4.sp,
            ),
        button =
            TextStyle(
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp,
            ),
        dialogLabel =
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                letterSpacing = 3.sp,
            ),
        dialogButton =
            TextStyle(
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
            ),
    )

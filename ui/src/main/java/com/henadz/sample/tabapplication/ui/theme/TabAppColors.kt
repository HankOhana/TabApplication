package com.henadz.sample.tabapplication.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TabAppColors(
    val background: Color,
    val surface: Color, // default cell background
    val surfaceVariant: Color, // dialogs, elevated surfaces
    val onBackground: Color, // primary text
    val primary: Color, // active (green) cell background
    val onPrimary: Color, // text on active cells
    val border: Color,
    val error: Color,
    val isDark: Boolean,
)

internal fun darkTabAppColors() =
    TabAppColors(
        background = TabAppDarkPalette.background,
        surface = TabAppDarkPalette.surface,
        surfaceVariant = TabAppDarkPalette.surfaceVariant,
        onBackground = TabAppDarkPalette.onBackground,
        primary = TabAppDarkPalette.green,
        onPrimary = TabAppDarkPalette.onBackground,
        border = TabAppDarkPalette.border,
        error = TabAppDarkPalette.error,
        isDark = true,
    )

internal fun lightTabAppColors() =
    TabAppColors(
        background = TabAppLightPalette.background,
        surface = TabAppLightPalette.surface,
        surfaceVariant = TabAppLightPalette.surfaceVariant,
        onBackground = TabAppLightPalette.onBackground,
        primary = TabAppLightPalette.green,
        onPrimary = Color.White,
        border = TabAppLightPalette.border,
        error = TabAppLightPalette.error,
        isDark = false,
    )

/**
 * Maps semantic TabApp tokens to M3 color slots so that Material components
 * (OutlinedTextField, Button, Dialog) don't bleed their default purple/teal palette.
 *
 * Mapping rationale:
 *  primary       → onBackground  : cursor colour and focused-indicator of OutlinedTextField
 *  outline       → border        : unfocused OutlinedTextField border
 *  onSurfaceVariant → border     : unfocused hint/label text inside OutlinedTextField
 *  surface       → surfaceVariant: Dialog scrim surface
 */
internal fun TabAppColors.toMaterialColorScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = onBackground,
        onPrimary = background,
        background = background,
        onBackground = onBackground,
        surface = surfaceVariant,
        onSurface = onBackground,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = border,
        outline = border,
        error = error,
        onError = if (isDark) background else Color.White,
        errorContainer = error.copy(alpha = 0.15f),
        onErrorContainer = error,
    )
}

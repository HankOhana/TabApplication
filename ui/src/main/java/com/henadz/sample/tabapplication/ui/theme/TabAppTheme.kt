package com.henadz.sample.tabapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalTabAppColors = staticCompositionLocalOf { darkTabAppColors() }
val LocalTabAppTypography = staticCompositionLocalOf { defaultTabAppTypography() }
val LocalTabAppDimens = staticCompositionLocalOf { tabAppDimens() }

object TabAppTheme {
    val colors: TabAppColors
        @Composable get() = LocalTabAppColors.current
    val typography: TabAppTypography
        @Composable get() = LocalTabAppTypography.current
    val dimens: TabAppDimens
        @Composable get() = LocalTabAppDimens.current
}

@Composable
fun TabAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = remember(darkTheme) { if (darkTheme) darkTabAppColors() else lightTabAppColors() }
    val typography = remember { defaultTabAppTypography() }
    val dimens = remember { tabAppDimens() }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalTabAppColors provides colors,
        LocalTabAppTypography provides typography,
        LocalTabAppDimens provides dimens,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            content = content,
        )
    }
}
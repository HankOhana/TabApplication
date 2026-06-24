package com.henadz.sample.tabapplication.ui.table

import androidx.compose.runtime.Immutable

@Immutable
data class UiCell(
    val id: String,
    val text: String,
    val isGreen: Boolean,
)

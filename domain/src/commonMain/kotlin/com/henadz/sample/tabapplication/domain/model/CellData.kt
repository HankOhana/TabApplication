package com.henadz.sample.tabapplication.domain.model

data class CellData(
    val id: String,
    val rowIndex: Int,
    val columnIndex: Int,
    val text: String,
    val isGreen: Boolean,
)

package com.henadz.sample.tabapplication.domain.repository

import com.henadz.sample.tabapplication.domain.model.CellData
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    fun getTableData(
        rows: Int,
        cols: Int,
    ): Flow<List<CellData>>

    suspend fun updateCell(
        cellId: String,
        newText: String,
    )

    suspend fun toggleCellColor(cellId: String)

    suspend fun clearSession()

    suspend fun resetTable(
        rows: Int,
        cols: Int,
    )
}

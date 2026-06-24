package com.henadz.sample.tabapplication.domain.fake

import com.henadz.sample.tabapplication.domain.model.CellData
import com.henadz.sample.tabapplication.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTableRepository : TableRepository {

    val tableFlow = MutableStateFlow<List<CellData>>(emptyList())

    var lastGetRows: Int? = null
    var lastGetCols: Int? = null

    data class UpdateCall(val cellId: String, val newText: String)
    val updateCalls = mutableListOf<UpdateCall>()

    val toggleCalls = mutableListOf<String>()

    var clearCount = 0

    data class ResetCall(val rows: Int, val cols: Int)
    val resetCalls = mutableListOf<ResetCall>()

    override suspend fun getTableData(rows: Int, cols: Int): Flow<List<CellData>> {
        lastGetRows = rows
        lastGetCols = cols
        return tableFlow
    }

    override suspend fun updateCell(cellId: String, newText: String) {
        updateCalls.add(UpdateCall(cellId, newText))
    }

    override suspend fun toggleCellColor(cellId: String) {
        toggleCalls.add(cellId)
    }

    override suspend fun clearSession() {
        clearCount++
    }

    override suspend fun resetTable(rows: Int, cols: Int) {
        resetCalls.add(ResetCall(rows, cols))
    }
}

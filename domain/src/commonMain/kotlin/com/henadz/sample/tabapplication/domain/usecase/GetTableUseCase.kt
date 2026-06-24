package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.model.CellData
import com.henadz.sample.tabapplication.domain.model.GridConstraints
import com.henadz.sample.tabapplication.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow

class GetTableUseCase(
    private val repository: TableRepository,
) {
    suspend operator fun invoke(
        rows: Int,
        cols: Int,
    ): Flow<List<CellData>> {
        require(rows in GridConstraints.MIN_ROWS..GridConstraints.MAX_ROWS) {
            "rows must be in ${GridConstraints.MIN_ROWS}..${GridConstraints.MAX_ROWS}, got $rows"
        }
        require(cols in GridConstraints.MIN_COLS..GridConstraints.MAX_COLS) {
            "cols must be in ${GridConstraints.MIN_COLS}..${GridConstraints.MAX_COLS}, got $cols"
        }
        return repository.getTableData(rows, cols)
    }
}
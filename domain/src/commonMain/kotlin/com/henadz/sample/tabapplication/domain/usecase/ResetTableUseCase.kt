package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.model.GridConstraints
import com.henadz.sample.tabapplication.domain.repository.TableRepository

class ResetTableUseCase(
    private val repository: TableRepository,
) {
    suspend operator fun invoke(
        rows: Int,
        cols: Int,
    ) {
        require(rows in GridConstraints.MIN_ROWS..GridConstraints.MAX_ROWS) {
            "rows must be in ${GridConstraints.MIN_ROWS}..${GridConstraints.MAX_ROWS}, got $rows"
        }
        require(cols in GridConstraints.MIN_COLS..GridConstraints.MAX_COLS) {
            "cols must be in ${GridConstraints.MIN_COLS}..${GridConstraints.MAX_COLS}, got $cols"
        }
        repository.resetTable(rows, cols)
    }
}
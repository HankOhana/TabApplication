package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.repository.TableRepository

class ResetTableUseCase(
    private val repository: TableRepository,
) {
    suspend operator fun invoke(
        rows: Int,
        cols: Int,
    ) = repository.resetTable(rows, cols)
}

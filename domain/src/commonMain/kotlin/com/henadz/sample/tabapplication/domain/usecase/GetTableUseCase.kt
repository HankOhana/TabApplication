package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.model.CellData
import com.henadz.sample.tabapplication.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow

class GetTableUseCase(
    private val repository: TableRepository,
) {
    suspend operator fun invoke(
        rows: Int,
        cols: Int,
    ): Flow<List<CellData>> = repository.getTableData(rows, cols)
}

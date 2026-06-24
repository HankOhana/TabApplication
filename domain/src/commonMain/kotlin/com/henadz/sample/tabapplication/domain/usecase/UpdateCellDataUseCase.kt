package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.repository.TableRepository

class UpdateCellDataUseCase(
    private val repository: TableRepository,
) {
    suspend operator fun invoke(
        cellId: String,
        newText: String,
    ) = repository.updateCell(cellId, newText)
}

package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.repository.TableRepository

class ToggleCellColorUseCase(
    private val repository: TableRepository,
) {
    suspend operator fun invoke(cellId: String) = repository.toggleCellColor(cellId)
}

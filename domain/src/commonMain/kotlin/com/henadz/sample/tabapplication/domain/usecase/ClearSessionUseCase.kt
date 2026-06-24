package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.repository.TableRepository

class ClearSessionUseCase(
    private val repository: TableRepository,
) {
    suspend operator fun invoke() = repository.clearSession()
}

package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.fake.FakeTableRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClearSessionUseCaseTest {

    private val fake = FakeTableRepository()
    private val useCase = ClearSessionUseCase(fake)

    @Test
    fun `calls clearSession on repository`() = runTest {
        useCase()
        assertEquals(1, fake.clearCount)
    }

    @Test
    fun `each invocation is forwarded individually`() = runTest {
        useCase()
        useCase()
        assertEquals(2, fake.clearCount)
    }
}

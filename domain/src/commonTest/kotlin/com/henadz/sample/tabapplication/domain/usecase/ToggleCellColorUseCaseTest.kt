package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.fake.FakeTableRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ToggleCellColorUseCaseTest {

    private val fake = FakeTableRepository()
    private val useCase = ToggleCellColorUseCase(fake)

    @Test
    fun `passes cell id to repository`() = runTest {
        useCase("42")
        assertEquals("42", fake.toggleCalls.single())
    }

    @Test
    fun `each invocation is forwarded individually`() = runTest {
        useCase("1")
        useCase("2")
        useCase("3")
        assertEquals(listOf("1", "2", "3"), fake.toggleCalls)
    }
}

package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.fake.FakeTableRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateCellDataUseCaseTest {

    private val fake = FakeTableRepository()
    private val useCase = UpdateCellDataUseCase(fake)

    @Test
    fun `passes cell id and text to repository unchanged`() = runTest {
        useCase("7", "hello")
        assertEquals(FakeTableRepository.UpdateCall("7", "hello"), fake.updateCalls.single())
    }

    @Test
    fun `passes empty text unchanged`() = runTest {
        useCase("0", "")
        assertEquals("", fake.updateCalls.single().newText)
    }

    @Test
    fun `each invocation is forwarded individually`() = runTest {
        useCase("1", "foo")
        useCase("2", "bar")
        assertEquals(
            listOf(
                FakeTableRepository.UpdateCall("1", "foo"),
                FakeTableRepository.UpdateCall("2", "bar"),
            ),
            fake.updateCalls,
        )
    }
}

package com.henadz.sample.tabapplication.domain.usecase

import com.henadz.sample.tabapplication.domain.fake.FakeTableRepository
import com.henadz.sample.tabapplication.domain.model.GridConstraints
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ResetTableUseCaseTest {

    private val fake = FakeTableRepository()
    private val useCase = ResetTableUseCase(fake)

    @Test
    fun `passes rows and cols to repository unchanged`() = runTest {
        useCase(10, 4)
        assertEquals(FakeTableRepository.ResetCall(10, 4), fake.resetCalls.single())
    }

    @Test
    fun `accepts minimum boundary values`() = runTest {
        useCase(GridConstraints.MIN_ROWS, GridConstraints.MIN_COLS)
        assertEquals(
            FakeTableRepository.ResetCall(GridConstraints.MIN_ROWS, GridConstraints.MIN_COLS),
            fake.resetCalls.single(),
        )
    }

    @Test
    fun `accepts maximum boundary values`() = runTest {
        useCase(GridConstraints.MAX_ROWS, GridConstraints.MAX_COLS)
        assertEquals(
            FakeTableRepository.ResetCall(GridConstraints.MAX_ROWS, GridConstraints.MAX_COLS),
            fake.resetCalls.single(),
        )
    }

    @Test
    fun `throws when rows is below minimum`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase(GridConstraints.MIN_ROWS - 1, 3)
        }
    }

    @Test
    fun `throws when rows is above maximum`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase(GridConstraints.MAX_ROWS + 1, 3)
        }
    }

    @Test
    fun `throws when cols is below minimum`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase(5, GridConstraints.MIN_COLS - 1)
        }
    }

    @Test
    fun `throws when cols is above maximum`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase(5, GridConstraints.MAX_COLS + 1)
        }
    }

    @Test
    fun `does not call repository when validation fails`() = runTest {
        runCatching { useCase(0, 3) }
        assertTrue(fake.resetCalls.isEmpty())
    }
}

package com.henadz.sample.tabapplication.data.repository

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class InMemoryTableRepositoryTest {

    // ── 1. Grid generation ─────────────────────────────────────────────────

    @Test
    fun `generates correct total cell count`() = runTest {
        val cells = InMemoryTableRepository().getTableData(4, 3).first()
        assertEquals(12, cells.size)
    }

    @Test
    fun `cell ids are flat indices encoded as strings`() = runTest {
        val cells = InMemoryTableRepository().getTableData(2, 3).first()
        cells.forEachIndexed { index, cell ->
            assertEquals(index.toString(), cell.id)
        }
    }

    @Test
    fun `cell row and column indices are derived from flat index`() = runTest {
        val cols = 3
        val cells = InMemoryTableRepository().getTableData(2, cols).first()
        cells.forEachIndexed { index, cell ->
            assertEquals(index / cols, cell.rowIndex, "rowIndex mismatch at $index")
            assertEquals(index % cols, cell.columnIndex, "columnIndex mismatch at $index")
        }
    }

    @Test
    fun `all cells start with isGreen false`() = runTest {
        val cells = InMemoryTableRepository().getTableData(3, 3).first()
        assertTrue(cells.all { !it.isGreen })
    }

    @Test
    fun `generated cell text is non-empty`() = runTest {
        val cells = InMemoryTableRepository().getTableData(2, 2).first()
        assertTrue(cells.all { it.text.isNotEmpty() })
    }

    // ── 2. Cache behavior ──────────────────────────────────────────────────

    @Test
    fun `same dimensions on second call preserves modified cell data`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(2, 3)
        repo.updateCell("2", "CACHED")
        assertEquals("CACHED", repo.getTableData(2, 3).first()[2].text)
    }

    @Test
    fun `different total size on second call regenerates the grid`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(2, 3)
        // "MODIFIED_CACHE_DATA" is 18 chars; randomText() always generates 6 — can never collide
        repo.updateCell("0", "MODIFIED_CACHE_DATA")
        val cells = repo.getTableData(3, 3).first()
        assertEquals(9, cells.size)
        assertNotEquals("MODIFIED_CACHE_DATA", cells[0].text)
    }

    // ── 3. getTableData — flow emission ────────────────────────────────────

    @Test
    fun `emits the generated grid immediately on collection`() = runTest {
        InMemoryTableRepository().getTableData(3, 2).test {
            assertEquals(6, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── 4. updateCell ──────────────────────────────────────────────────────

    @Test
    fun `updateCell changes text of the target cell`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(2, 3)
        repo.updateCell("4", "UPDATED")
        assertEquals("UPDATED", flow.first()[4].text)
    }

    @Test
    fun `updateCell does not affect other cells`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(1, 3)
        val before = flow.first()
        repo.updateCell("1", "CHANGED")
        val after = flow.first()
        assertEquals(before[0].text, after[0].text)
        assertEquals(before[2].text, after[2].text)
    }

    @Test
    fun `updateCell emits the updated list via flow`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            awaitItem()
            repo.updateCell("0", "NEW")
            assertEquals("NEW", awaitItem()[0].text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCell ignores non-integer cell id without emitting`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            awaitItem()
            repo.updateCell("not-a-number", "X")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCell ignores out-of-bounds index without emitting`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            awaitItem() // 3 cells: valid indices are 0-2
            repo.updateCell("99", "X")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── 5. toggleCellColor ─────────────────────────────────────────────────

    @Test
    fun `toggleCellColor sets isGreen from false to true`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(1, 3)
        assertFalse(flow.first()[1].isGreen)
        repo.toggleCellColor("1")
        assertTrue(flow.first()[1].isGreen)
    }

    @Test
    fun `toggleCellColor sets isGreen back to false on second call`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(1, 3)
        repo.toggleCellColor("1")
        repo.toggleCellColor("1")
        assertFalse(flow.first()[1].isGreen)
    }

    @Test
    fun `toggleCellColor does not affect other cells`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(1, 3)
        val before = flow.first()
        repo.toggleCellColor("1")
        val after = flow.first()
        assertEquals(before[0].isGreen, after[0].isGreen)
        assertEquals(before[2].isGreen, after[2].isGreen)
    }

    @Test
    fun `toggleCellColor emits the updated list via flow`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            awaitItem()
            repo.toggleCellColor("0")
            assertTrue(awaitItem()[0].isGreen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleCellColor ignores non-integer cell id without emitting`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            awaitItem()
            repo.toggleCellColor("oops")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── 6. clearSession ────────────────────────────────────────────────────

    @Test
    fun `clearSession emits empty list via flow`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(2, 3).test {
            awaitItem()
            repo.clearSession()
            assertEquals(emptyList(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearSession followed by getTableData generates a fresh grid`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(2, 3)
        repo.updateCell("0", "MODIFIED_BEFORE_CLEAR")
        repo.clearSession()
        val cells = repo.getTableData(2, 3).first()
        assertEquals(6, cells.size)
        assertNotEquals("MODIFIED_BEFORE_CLEAR", cells[0].text)
    }

    // ── 7. resetTable ──────────────────────────────────────────────────────

    @Test
    fun `resetTable generates a new grid of the same size`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(2, 3)
        repo.resetTable(2, 3)
        assertEquals(6, flow.first().size)
    }

    @Test
    fun `resetTable emits the new grid via flow`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(2, 3).test {
            awaitItem()
            repo.resetTable(2, 3)
            assertEquals(6, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resetTable can change grid dimensions`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(2, 3) // 6 cells
        repo.resetTable(5, 4)              // 20 cells — same StateFlow, new value
        assertEquals(20, flow.first().size)
    }

    @Test
    fun `resetTable discards previous cell modifications`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(2, 3)
        repo.updateCell("0", "SHOULD_BE_GONE")
        repo.resetTable(2, 3)
        assertNotEquals("SHOULD_BE_GONE", repo.getTableData(2, 3).first()[0].text)
    }

    // ── 8. Concurrency / Stress ────────────────────────────────────────────

    @Test
    fun `100 concurrent toggles complete without errors and yield deterministic result`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(1, 1)
        assertFalse(flow.first()[0].isGreen) // starts false

        List(100) { launch(Dispatchers.Default) { repo.toggleCellColor("0") } }.joinAll()

        // Mutex serialises all 100 operations; 100 even flips → back to false
        assertFalse(flow.first()[0].isGreen)
    }

    @Test
    fun `100 concurrent updateCell calls complete without data corruption`() = runTest {
        val repo = InMemoryTableRepository()
        val flow = repo.getTableData(1, 1)
        val expectedTexts = (0 until 100).map { "update-$it" }.toSet()

        List(100) { i -> launch(Dispatchers.Default) { repo.updateCell("0", "update-$i") } }.joinAll()

        val finalState = flow.first()
        assertEquals(1, finalState.size)              // list structure intact
        assertTrue(finalState[0].text in expectedTexts) // one of the 100 valid writes won
    }

    // ── 9. Referential integrity & diff stability ──────────────────────────

    @Test
    fun `updateCell emits a new list reference`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            val before = awaitItem()
            repo.updateCell("1", "NEW")
            assertNotSame(before, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCell creates a new CellData instance for the modified cell`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            val before = awaitItem()
            repo.updateCell("1", "NEW")
            assertNotSame(before[1], awaitItem()[1])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCell preserves referential equality for unchanged cells`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            val before = awaitItem()
            repo.updateCell("1", "NEW")
            val after = awaitItem()
            assertSame(before[0], after[0])
            assertSame(before[2], after[2])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleCellColor preserves referential equality for unchanged cells`() = runTest {
        val repo = InMemoryTableRepository()
        repo.getTableData(1, 3).test {
            val before = awaitItem()
            repo.toggleCellColor("1")
            val after = awaitItem()
            assertSame(before[0], after[0])
            assertSame(before[2], after[2])
            cancelAndIgnoreRemainingEvents()
        }
    }
}

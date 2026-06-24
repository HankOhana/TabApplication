package com.henadz.sample.tabapplication.ui.table

import app.cash.turbine.test
import com.henadz.sample.tabapplication.domain.model.CellData
import com.henadz.sample.tabapplication.domain.repository.TableRepository
import com.henadz.sample.tabapplication.domain.usecase.ClearSessionUseCase
import com.henadz.sample.tabapplication.domain.usecase.GetTableUseCase
import com.henadz.sample.tabapplication.domain.usecase.ResetTableUseCase
import com.henadz.sample.tabapplication.domain.usecase.ToggleCellColorUseCase
import com.henadz.sample.tabapplication.domain.usecase.UpdateCellDataUseCase
import com.henadz.sample.tabapplication.ui.strings.UiStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// MutableSharedFlow(replay=1) instead of MutableStateFlow: SharedFlow has no equality check,
// so emitting structurally equal lists twice still produces two downstream emissions —
// which is required to test runningFold instance reuse.
private class FakeTableRepository : TableRepository {

    val tableFlow = MutableSharedFlow<List<CellData>>(replay = 1)

    var lastGetRows: Int? = null
    var lastGetCols: Int? = null

    data class UpdateCall(val cellId: String, val newText: String)
    val updateCalls = mutableListOf<UpdateCall>()

    val toggleCalls = mutableListOf<String>()
    var clearCount = 0

    data class ResetCall(val rows: Int, val cols: Int)
    val resetCalls = mutableListOf<ResetCall>()

    suspend fun emit(cells: List<CellData>) = tableFlow.emit(cells)

    override suspend fun getTableData(rows: Int, cols: Int): Flow<List<CellData>> {
        lastGetRows = rows
        lastGetCols = cols
        return tableFlow
    }

    override suspend fun updateCell(cellId: String, newText: String) {
        updateCalls.add(UpdateCall(cellId, newText))
    }

    override suspend fun toggleCellColor(cellId: String) {
        toggleCalls.add(cellId)
    }

    override suspend fun clearSession() {
        clearCount++
    }

    override suspend fun resetTable(rows: Int, cols: Int) {
        resetCalls.add(ResetCall(rows, cols))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TableViewModelTest {

    // UnconfinedTestDispatcher: coroutines run eagerly inline.
    // This means backgroundScope.launch { vm.state.collect { } } runs immediately,
    // activating SharingStarted.Lazily's upstream without needing advanceUntilIdle().
    // It also collapses flowOn(dispatcher) channel hops so state updates are visible
    // on vm.state.value right after handleIntent() or fakeRepo.emit().
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepo: FakeTableRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeTableRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(rows: Int = ROWS, cols: Int = COLS) =
        TableViewModel(
            rows = rows,
            cols = cols,
            getTableUseCase = GetTableUseCase(fakeRepo),
            toggleCellColorUseCase = ToggleCellColorUseCase(fakeRepo),
            updateCellDataUseCase = UpdateCellDataUseCase(fakeRepo),
            clearSessionUseCase = ClearSessionUseCase(fakeRepo),
            resetTableUseCase = ResetTableUseCase(fakeRepo),
            defaultDispatcher = testDispatcher,
        )

    // Subscribes to vm.state so SharingStarted.Lazily activates the upstream.
    // With UnconfinedTestDispatcher the launch runs inline — by the time this
    // function returns the combine + cellsFlow + fakeRepo.tableFlow subscriber
    // chain is live and fakeRepo.emit() will deliver immediately.
    private fun TestScope.activateState(vm: TableViewModel) {
        backgroundScope.launch { vm.state.collect { } }
    }

    @Test
    fun `initial state is loading with correct dimensions`() {
        val vm = buildViewModel()
        with(vm.state.value) {
            assertTrue(isLoading)
            assertEquals(ROWS, rows)
            assertEquals(COLS, columns)
            assertTrue(cells.isEmpty())
            assertNull(editingCellId)
        }
    }

    @Test
    fun `cells emitted by use case appear in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.state.test {
            awaitItem() // initial loading state

            fakeRepo.emit(buildGrid())

            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertEquals(ROWS * COLS, loaded.cells.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unchanged cells reuse same UiCell instances on second emit`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        activateState(vm)

        fakeRepo.emit(buildGrid())
        val first = vm.state.value.cells.toList()
        assertTrue(first.isNotEmpty())

        // Same content — SharedFlow always re-delivers; runningFold reuses existing
        // UiCell instances. The resulting state is structurally equal to the previous
        // one, so StateFlow does NOT emit. vm.state.value still refers to the same
        // ImmutableList built from the same cell objects.
        fakeRepo.emit(buildGrid())
        val second = vm.state.value.cells.toList()

        first.forEachIndexed { i, cell -> assertSame(cell, second[i]) }
    }

    @Test
    fun `only the changed cell gets a new UiCell instance`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.state.test {
            awaitItem() // initial

            val grid = buildGrid()
            fakeRepo.emit(grid)
            val first = awaitItem().cells.toList()

            val modified = grid.toMutableList().also { it[2] = it[2].copy(isGreen = true) }
            fakeRepo.emit(modified)
            val second = awaitItem().cells.toList()

            assertNotSame(first[2], second[2])
            first.forEachIndexed { i, cell -> if (i != 2) assertSame(cell, second[i]) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `double click sets editingCellId`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        activateState(vm)
        fakeRepo.emit(buildGrid())

        vm.handleIntent(TableUiIntent.CellDoubleClicked("2"))

        assertEquals("2", vm.state.value.editingCellId)
    }

    @Test
    fun `cancel edit clears editingCellId`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        activateState(vm)
        fakeRepo.emit(buildGrid())

        vm.handleIntent(TableUiIntent.CellDoubleClicked("2"))
        vm.handleIntent(TableUiIntent.CancelCellEdit)

        assertNull(vm.state.value.editingCellId)
    }

    @Test
    fun `commit with changed text calls UpdateCellDataUseCase`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        activateState(vm)
        fakeRepo.emit(buildGrid()) // cells have text = ""

        vm.handleIntent(TableUiIntent.CellDoubleClicked("2"))
        vm.handleIntent(TableUiIntent.CommitCellEdit("2", "new text"))
        advanceUntilIdle()

        assertEquals(1, fakeRepo.updateCalls.size)
        assertEquals(FakeTableRepository.UpdateCall("2", "new text"), fakeRepo.updateCalls[0])
    }

    @Test
    fun `commit with unchanged text skips UpdateCellDataUseCase`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        activateState(vm)
        fakeRepo.emit(buildGrid()) // cells have text = ""

        vm.handleIntent(TableUiIntent.CellDoubleClicked("2"))
        vm.handleIntent(TableUiIntent.CommitCellEdit("2", "")) // same as current text
        advanceUntilIdle()

        assertTrue(fakeRepo.updateCalls.isEmpty())
    }

    @Test
    fun `commit when not editing is a no-op`() = runTest(testDispatcher) {
        val vm = buildViewModel()

        vm.handleIntent(TableUiIntent.CommitCellEdit("2", "text"))
        advanceUntilIdle()

        assertTrue(fakeRepo.updateCalls.isEmpty())
    }

    @Test
    fun `second commit is rejected by the null guard`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        activateState(vm)
        fakeRepo.emit(buildGrid())

        vm.handleIntent(TableUiIntent.CellDoubleClicked("2"))

        // commitEdit() sets _editingCellId = null synchronously before launching the
        // update coroutine. With UnconfinedTestDispatcher the launch runs inline, so
        // by the time the second call arrives _editingCellId is already null.
        vm.handleIntent(TableUiIntent.CommitCellEdit("2", "first"))
        vm.handleIntent(TableUiIntent.CommitCellEdit("2", "first"))
        advanceUntilIdle()

        assertEquals(1, fakeRepo.updateCalls.size)
    }

    @Test
    fun `cell click invokes ToggleCellColorUseCase with correct id`() = runTest(testDispatcher) {
        val vm = buildViewModel()

        vm.handleIntent(TableUiIntent.CellClicked("5"))
        advanceUntilIdle()

        assertEquals(listOf("5"), fakeRepo.toggleCalls)
    }

    @Test
    fun `exit session calls ClearSessionUseCase`() = runTest(testDispatcher) {
        val vm = buildViewModel()

        vm.handleIntent(TableUiIntent.ExitSession)
        advanceUntilIdle()

        assertEquals(1, fakeRepo.clearCount)
    }

    @Test
    fun `exit session emits NavigateBack effect`() = runTest(testDispatcher) {
        val vm = buildViewModel()

        vm.effects.test {
            vm.handleIntent(TableUiIntent.ExitSession)
            assertEquals(TableUiEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reset clears editingCellId synchronously before coroutine runs`() = runTest(testDispatcher) {
        // Switch to StandardTestDispatcher so that viewModelScope.launch { } is
        // SCHEDULED but NOT run yet. This lets us verify that _editingCellId = null
        // is set synchronously on the calling thread BEFORE the reset coroutine
        // executes, making a subsequent CommitCellEdit hit the null guard.
        val standardDispatcher = StandardTestDispatcher(testDispatcher.scheduler)
        Dispatchers.setMain(standardDispatcher)
        val vm = TableViewModel(
            rows = ROWS,
            cols = COLS,
            getTableUseCase = GetTableUseCase(fakeRepo),
            toggleCellColorUseCase = ToggleCellColorUseCase(fakeRepo),
            updateCellDataUseCase = UpdateCellDataUseCase(fakeRepo),
            clearSessionUseCase = ClearSessionUseCase(fakeRepo),
            resetTableUseCase = ResetTableUseCase(fakeRepo),
            defaultDispatcher = standardDispatcher,
        )

        backgroundScope.launch { vm.state.collect { } }
        fakeRepo.emit(buildGrid())
        advanceUntilIdle()

        vm.handleIntent(TableUiIntent.CellDoubleClicked("2"))
        advanceUntilIdle()
        assertEquals("2", vm.state.value.editingCellId)

        // ResetTable sets _editingCellId = null synchronously on the main thread,
        // then schedules the reset coroutine. CommitCellEdit arrives before
        // advanceUntilIdle(), so the null guard rejects it.
        vm.handleIntent(TableUiIntent.ResetTable)
        vm.handleIntent(TableUiIntent.CommitCellEdit("2", "stale text"))
        advanceUntilIdle()

        assertTrue(fakeRepo.updateCalls.isEmpty())
        assertEquals(1, fakeRepo.resetCalls.size)
    }

    @Test
    fun `reset calls ResetTableUseCase with correct dimensions`() = runTest(testDispatcher) {
        val vm = buildViewModel()

        vm.handleIntent(TableUiIntent.ResetTable)
        advanceUntilIdle()

        assertEquals(1, fakeRepo.resetCalls.size)
        assertEquals(FakeTableRepository.ResetCall(ROWS, COLS), fakeRepo.resetCalls[0])
    }

    @Test
    fun `reset emits ShowToast effect with success message`() = runTest(testDispatcher) {
        val vm = buildViewModel()

        vm.effects.test {
            vm.handleIntent(TableUiIntent.ResetTable)
            assertEquals(TableUiEffect.ShowToast(UiStrings.TOAST_RESET_SUCCESS), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        private const val ROWS = 3
        private const val COLS = 2

        private fun buildGrid(rows: Int = ROWS, cols: Int = COLS): List<CellData> =
            List(rows * cols) { i ->
                CellData(
                    id = "$i",
                    rowIndex = i / cols,
                    columnIndex = i % cols,
                    text = "",
                    isGreen = false,
                )
            }
    }
}
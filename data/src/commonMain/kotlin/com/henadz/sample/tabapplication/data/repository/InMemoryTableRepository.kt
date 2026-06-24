package com.henadz.sample.tabapplication.data.repository

import com.henadz.sample.tabapplication.domain.model.CellData
import com.henadz.sample.tabapplication.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

internal class InMemoryTableRepository : TableRepository {
    private val _state = MutableStateFlow<List<CellData>>(emptyList())
    private val mutex = Mutex()
    private var cachedGrid: MutableList<CellData>? = null

    override suspend fun getTableData(
        rows: Int,
        cols: Int,
    ): Flow<List<CellData>> {
        mutex.withLock {
            val targetSize = rows * cols
            val grid =
                cachedGrid?.takeIf { it.size == targetSize }
                    ?: generateGrid(rows, cols).toMutableList().also { cachedGrid = it }
            _state.value = grid.toList()
        }
        return _state.asStateFlow()
    }

    override suspend fun updateCell(
        cellId: String,
        newText: String,
    ) {
        val index = cellId.toIntOrNull() ?: return
        mutex.withLock {
            val cache = cachedGrid ?: return@withLock
            if (index !in cache.indices) return@withLock
            cache[index] = cache[index].copy(text = newText)
            _state.value = cache.toList()
        }
    }

    override suspend fun toggleCellColor(cellId: String) {
        val index = cellId.toIntOrNull() ?: return
        mutex.withLock {
            val cache = cachedGrid ?: return@withLock
            if (index !in cache.indices) return@withLock
            cache[index] = cache[index].copy(isGreen = !cache[index].isGreen)
            _state.value = cache.toList()
        }
    }

    override suspend fun clearSession() {
        mutex.withLock {
            cachedGrid = null
            _state.value = emptyList()
        }
    }

    override suspend fun resetTable(
        rows: Int,
        cols: Int,
    ) {
        mutex.withLock {
            val newGrid = generateGrid(rows, cols).toMutableList()
            cachedGrid = newGrid
            _state.value = newGrid.toList()
        }
    }

    private fun generateGrid(
        rows: Int,
        cols: Int,
    ): List<CellData> =
        List(rows * cols) { index ->
            CellData(
                id = index.toString(),
                rowIndex = index / cols,
                columnIndex = index % cols,
                text = randomText(),
                isGreen = false,
            )
        }

    private fun randomText(length: Int = 6): String {
        val charArray =
            CharArray(length) {
                CHAR_POOL[Random.nextInt(CHAR_POOL.length)]
            }
        return String(charArray)
    }
}

private const val CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
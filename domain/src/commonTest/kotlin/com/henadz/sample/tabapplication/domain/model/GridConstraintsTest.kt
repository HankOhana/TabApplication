package com.henadz.sample.tabapplication.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class GridConstraintsTest {

    @Test
    fun `rows range is 1 to 1000`() {
        assertEquals(1, GridConstraints.MIN_ROWS)
        assertEquals(1000, GridConstraints.MAX_ROWS)
    }

    @Test
    fun `cols range is 1 to 6`() {
        assertEquals(1, GridConstraints.MIN_COLS)
        assertEquals(6, GridConstraints.MAX_COLS)
    }

    @Test
    fun `max rows input length equals digit count of MAX_ROWS`() {
        assertEquals(GridConstraints.MAX_ROWS.toString().length, GridConstraints.MAX_ROWS_INPUT_LENGTH)
    }

    @Test
    fun `max cols input length equals digit count of MAX_COLS`() {
        assertEquals(GridConstraints.MAX_COLS.toString().length, GridConstraints.MAX_COLS_INPUT_LENGTH)
    }
}

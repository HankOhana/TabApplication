package com.henadz.sample.tabapplication.ui.strings

import com.henadz.sample.tabapplication.domain.model.GridConstraints

internal object UiStrings {
    // SetupScreen
    const val SETUP_TITLE = "POS TABLE SETUP"
    val ROWS_LABEL = "ROWS  (${GridConstraints.MIN_ROWS} – ${GridConstraints.MAX_ROWS})"
    val ROWS_ERROR = "Enter a number between ${GridConstraints.MIN_ROWS} and ${GridConstraints.MAX_ROWS}"
    val COLS_LABEL = "COLUMNS  (${GridConstraints.MIN_COLS} – ${GridConstraints.MAX_COLS})"
    val COLS_ERROR = "Enter a number between ${GridConstraints.MIN_COLS} and ${GridConstraints.MAX_COLS}"
    const val SUBMIT = "SUBMIT"

    // TableScreen
    fun columnHeader(index: Int) = "COL ${index + 1}"

    // Dialogs (shared dismiss label)
    const val CANCEL = "CANCEL"

    // Bottom action bar
    const val EXIT = "EXIT"

    // Exit confirmation dialog
    const val EXIT_TABLE_TITLE = "EXIT TABLE?"
    const val EXIT_TABLE_MESSAGE = "All unsaved changes will be lost."
    const val STAY = "STAY"
    const val LEAVE = "LEAVE"

    // Reset confirmation dialog
    const val RESET = "RESET"
    const val RESET_TABLE_TITLE = "CLEAR ALL DATA?"
    const val RESET_TABLE_MESSAGE = "This cannot be undone."
    const val CONFIRM = "CONFIRM"

    // Toasts
    const val TOAST_RESET_SUCCESS = "Table reset successfully"
}

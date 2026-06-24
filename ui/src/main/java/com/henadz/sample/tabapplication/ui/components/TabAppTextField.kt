package com.henadz.sample.tabapplication.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme

@Composable
internal fun TabAppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = TabAppTheme.colors
    val typography = TabAppTheme.typography
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let { { Text(it, style = typography.inputLabel) } },
        isError = isError,
        supportingText =
            supportingText?.let { msg ->
                { Text(msg, style = typography.inputSupporting) }
            },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        shape = RectangleShape,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.onBackground,
                unfocusedTextColor = colors.onBackground,
                focusedBorderColor = colors.onBackground,
                unfocusedBorderColor = colors.border,
                focusedLabelColor = colors.onBackground,
                unfocusedLabelColor = colors.onBackground,
                cursorColor = colors.onBackground,
                errorBorderColor = colors.error,
                errorLabelColor = colors.error,
                errorCursorColor = colors.error,
                errorTextColor = colors.onBackground,
                errorSupportingTextColor = colors.error,
            ),
        modifier = modifier,
    )
}

package com.henadz.sample.tabapplication.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.henadz.sample.tabapplication.domain.model.GridConstraints
import com.henadz.sample.tabapplication.ui.components.TabAppTextField
import com.henadz.sample.tabapplication.ui.strings.UiStrings
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme
import com.henadz.sample.tabapplication.ui.util.rememberDebouncedClick

@Composable
fun SetupScreen(onSubmit: (rows: Int, cols: Int) -> Unit) {
    var rowsInput by remember { mutableStateOf("") }
    var colsInput by remember { mutableStateOf("") }

    val rowsValue by remember { derivedStateOf { rowsInput.toIntOrNull() } }
    val colsValue by remember { derivedStateOf { colsInput.toIntOrNull() } }

    val rowsError = rowsInput.isNotEmpty() && (rowsValue == null || rowsValue !in GridConstraints.MIN_ROWS..GridConstraints.MAX_ROWS)
    val colsError = colsInput.isNotEmpty() && (colsValue == null || colsValue !in GridConstraints.MIN_COLS..GridConstraints.MAX_COLS)
    val submitEnabled =
        rowsValue != null &&
            rowsValue in GridConstraints.MIN_ROWS..GridConstraints.MAX_ROWS &&
            colsValue != null &&
            colsValue in GridConstraints.MIN_COLS..GridConstraints.MAX_COLS

    val dimens = TabAppTheme.dimens
    val colsFocus = remember { FocusRequester() }
    val onSubmitDebounced = rememberDebouncedClick { onSubmit(rowsValue!!, colsValue!!) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TabAppTheme.colors.background),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = dimens.formMaxWidth)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = UiStrings.SETUP_TITLE,
                color = TabAppTheme.colors.onBackground,
                style = TabAppTheme.typography.heading,
            )

            Spacer(Modifier.height(dimens.sectionSpacing))

            TabAppTextField(
                value = rowsInput,
                onValueChange = { rowsInput = it.take(GridConstraints.MAX_ROWS_INPUT_LENGTH) },
                label = UiStrings.ROWS_LABEL,
                isError = rowsError,
                supportingText = if (rowsError) UiStrings.ROWS_ERROR else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { colsFocus.requestFocus() }),
                modifier = Modifier.fillMaxWidth(),
            )

            TabAppTextField(
                value = colsInput,
                onValueChange = { colsInput = it.take(GridConstraints.MAX_COLS_INPUT_LENGTH) },
                label = UiStrings.COLS_LABEL,
                isError = colsError,
                supportingText = if (colsError) UiStrings.COLS_ERROR else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().focusRequester(colsFocus),
            )

            Spacer(Modifier.height(dimens.sectionSpacing))

            Button(
                onClick = onSubmitDebounced,
                enabled = submitEnabled,
                shape = RectangleShape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = TabAppTheme.colors.onBackground,
                        contentColor = TabAppTheme.colors.background,
                        disabledContainerColor = TabAppTheme.colors.border,
                        disabledContentColor = TabAppTheme.colors.surfaceVariant,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(dimens.touchTargetHeight),
            ) {
                Text(
                    text = UiStrings.SUBMIT,
                    style = TabAppTheme.typography.button,
                )
            }
        }
    }
}

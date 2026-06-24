package com.henadz.sample.tabapplication.ui.table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TableCell(
    cell: UiCell,
    isEditing: Boolean,
    isAnyEditingProvider: () -> String?,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onCommit: (String) -> Unit,
) {
    val colors = TabAppTheme.colors
    val typography = TabAppTheme.typography
    val dimens = TabAppTheme.dimens
    val bgColor: Color = if (cell.isGreen) colors.primary else colors.surface
    val textColor: Color = if (cell.isGreen) colors.onPrimary else colors.onBackground

    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val onClickWithHaptic =
        remember(haptic, onClick) {
            {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                onClick()
            }
        }

    val interactionSource = remember { MutableInteractionSource() }

    val contentModifier =
        if (isEditing) {
            Modifier.border(dimens.dialogBorderWidth, colors.primary, RectangleShape)
        } else {
            Modifier
                .border(dimens.cellBorderWidth, colors.border, RectangleShape)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (isAnyEditingProvider() != null) focusManager.clearFocus()
                        else onClickWithHaptic()
                    },
                    onDoubleClick = {
                        if (isAnyEditingProvider() != null) focusManager.clearFocus()
                        else onDoubleClick()
                    },
                )
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(dimens.cellHeight)
                .drawBehind { drawRect(bgColor) }
                .then(contentModifier),
        contentAlignment = Alignment.Center,
    ) {
        if (isEditing) {
            val focusRequester = remember { FocusRequester() }
            val textFieldStyle =
                remember(textColor, typography.cellText) {
                    typography.cellText.copy(
                        color = textColor,
                        textAlign = TextAlign.Center,
                    )
                }
            var localValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(cell.text, TextRange(0, cell.text.length)))
            }

            var hasFocused by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) { focusRequester.requestFocus() }

            BasicTextField(
                value = localValue,
                onValueChange = { localValue = it },
                textStyle = textFieldStyle,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(onDone = {
                        onCommit(localValue.text)
                        focusManager.clearFocus()
                    }),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.sectionSpacing)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                hasFocused = true
                            } else if (hasFocused) {
                                onCommit(localValue.text)
                            }
                        },
            )
        } else {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.sectionSpacing),
                text = cell.text,
                color = textColor,
                style = typography.cellText,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        }
    }
}
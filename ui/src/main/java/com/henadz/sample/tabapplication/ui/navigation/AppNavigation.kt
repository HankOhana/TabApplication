package com.henadz.sample.tabapplication.ui.navigation

import android.os.Parcelable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.henadz.sample.tabapplication.ui.setup.SetupScreen
import com.henadz.sample.tabapplication.ui.table.TableScreen
import com.henadz.sample.tabapplication.ui.table.TableViewModel
import com.henadz.sample.tabapplication.ui.theme.TabAppTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TRANSITION_DURATION_MS = 180

@Composable
fun AppNavigation() {
    TabAppTheme {
        val backStack =
            rememberSaveable(saver = backStackSaver()) {
                mutableStateListOf(Setup)
            }

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            transitionSpec = {
                fadeIn(tween(TRANSITION_DURATION_MS)) togetherWith
                    fadeOut(tween(TRANSITION_DURATION_MS))
            },
            popTransitionSpec = {
                fadeIn(tween(TRANSITION_DURATION_MS)) togetherWith
                    fadeOut(tween(TRANSITION_DURATION_MS))
            },
            predictivePopTransitionSpec = {
                fadeIn(tween(TRANSITION_DURATION_MS)) togetherWith
                    fadeOut(tween(TRANSITION_DURATION_MS))
            },
            entryProvider =
                entryProvider {
                    entry<Setup> {
                        SetupScreen(
                            onSubmit = { rows, cols -> backStack.add(Table(rows, cols)) },
                        )
                    }
                    entry<Table> { key ->
                        val vm = koinViewModel<TableViewModel> { parametersOf(key.rows, key.cols) }
                        TableScreen(
                            viewModel = vm,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                },
        )
    }
}

private fun backStackSaver(): Saver<SnapshotStateList<Any>, ArrayList<Parcelable>> =
    Saver(
        save = { stack -> ArrayList(stack.filterIsInstance<Parcelable>()) },
        restore = { list -> mutableStateListOf(*list.toTypedArray<Any>()) },
    )
package com.henadz.sample.tabapplication.ui.util

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/**
 * Returns a stable lambda that forwards clicks to [onClick] but suppresses
 * rapid repeat taps within [debounceMs]. Use on all POS confirmation buttons
 * (SUBMIT, SAVE) where double-fire before UI teardown causes duplicate intents.
 *
 * [rememberUpdatedState] keeps the reference to the latest [onClick] without
 * invalidating the outer [remember], so stale closures are not an issue.
 */
@Composable
internal fun rememberDebouncedClick(
    debounceMs: Long = 300L,
    onClick: () -> Unit,
): () -> Unit {
    val lastClickTime = remember { mutableLongStateOf(0L) }
    val latestOnClick by rememberUpdatedState(onClick)
    return remember {
        {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime.longValue >= debounceMs) {
                lastClickTime.longValue = now
                latestOnClick()
            }
        }
    }
}

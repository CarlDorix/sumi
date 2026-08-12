package eu.kanade.presentation.more.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import eu.kanade.presentation.components.PreferenceCard
import eu.kanade.presentation.more.settings.screen.SearchableSettings
import eu.kanade.presentation.more.settings.widget.PreferenceGroupHeader
import kotlinx.coroutines.delay
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import kotlin.time.Duration.Companion.seconds

/**
 * Preference Screen composable which contains a list of [Preference] items
 * @param items [Preference] items which should be displayed on the preference screen. An item can be a single [PreferenceItem] or a group ([Preference.PreferenceGroup])
 * @param modifier [Modifier] to be applied to the preferenceScreen layout
 */
@Composable
fun PreferenceScreen(
    items: List<Preference>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val state = rememberLazyListState()
    val highlightKey = SearchableSettings.highlightKey
    if (highlightKey != null) {
        LaunchedEffect(Unit) {
            val i = items.findHighlightedIndex(highlightKey)
            if (i >= 0) {
                delay(0.5.seconds)
                state.animateScrollToItem(i)
            }
            SearchableSettings.highlightKey = null
        }
    }

    ScrollbarLazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
    ) {
        items.fastForEachIndexed { i, preference ->
            when (preference) {
                // Create Preference Group
                is Preference.PreferenceGroup -> {
                    if (!preference.enabled) return@fastForEachIndexed

                    item {
                        PreferenceGroupHeader(title = preference.title)
                    }
                    // Tachiyomi: one card per group, so a long run of identical rows reads as a
                    // handful of scannable blocks instead.
                    item {
                        PreferenceCard {
                            preference.preferenceItems.forEach { groupItem ->
                                PreferenceItem(
                                    item = groupItem,
                                    highlightKey = highlightKey,
                                )
                            }
                        }
                    }
                    item {
                        Spacer(
                            modifier = Modifier.height(if (i < items.lastIndex) 12.dp else 0.dp),
                        )
                    }
                }

                // Create Preference Item
                is Preference.PreferenceItem<*, *> -> item {
                    PreferenceItem(
                        item = preference,
                        highlightKey = highlightKey,
                    )
                }
            }
        }
    }
}

/**
 * Index of the lazy item to scroll to when settings search highlights a key.
 *
 * A group now emits exactly three lazy items — header, card, spacer — regardless of how many
 * preferences it holds, so this counts emitted items rather than individual preferences. Disabled
 * groups emit nothing and are skipped.
 */
private fun List<Preference>.findHighlightedIndex(highlightKey: String): Int {
    var lazyIndex = 0
    forEach { preference ->
        when (preference) {
            is Preference.PreferenceGroup -> {
                if (!preference.enabled) return@forEach
                if (preference.preferenceItems.any { it.title == highlightKey }) {
                    // The card holding the highlighted preference.
                    return lazyIndex + 1
                }
                lazyIndex += 3
            }
            is Preference.PreferenceItem<*, *> -> {
                if (preference.title == highlightKey) return lazyIndex
                lazyIndex++
            }
        }
    }
    return -1
}

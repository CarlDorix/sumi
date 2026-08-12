package eu.kanade.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource

/**
 * One browse section: its title, toolbar actions and content.
 *
 * The pager-backed `TabbedScreen` that used to render these is gone — Sources, Extensions and
 * Migrate are now separate destinations rather than permanent tabs — but the sections themselves
 * are still described this way and hosted by
 * [eu.kanade.tachiyomi.ui.browse.BrowseSectionScreen].
 */
data class TabContent(
    val titleRes: StringResource,
    val badgeNumber: Int? = null,
    val searchEnabled: Boolean = false,
    val actions: List<AppBar.AppBarAction> = listOf(),
    val content: @Composable (contentPadding: PaddingValues, snackbarHostState: SnackbarHostState) -> Unit,
)

package eu.kanade.tachiyomi.ui.browse

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.components.AppBarTitle
import eu.kanade.presentation.components.SearchToolbar
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.ui.browse.extension.ExtensionsViewModel
import eu.kanade.tachiyomi.ui.browse.extension.extensionsTab
import eu.kanade.tachiyomi.ui.browse.migration.sources.migrateSourceTab
import eu.kanade.tachiyomi.ui.browse.source.sourcesTab
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource

enum class BrowseSection { Sources, Extensions, Migrate }

/**
 * Standalone screen for one of the browse sections.
 *
 * These used to be permanent tabs competing with search for attention. They are destinations you
 * visit deliberately now, so each gets a real screen with a back arrow instead.
 */
data class BrowseSectionScreen(private val section: BrowseSection) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }

        val extensionsViewModel = viewModel<ExtensionsViewModel>()
        val extensionsSearchQuery by extensionsViewModel.searchQuery.collectAsStateWithLifecycle()

        val tab = when (section) {
            BrowseSection.Sources -> sourcesTab()
            BrowseSection.Extensions -> extensionsTab(extensionsViewModel)
            BrowseSection.Migrate -> migrateSourceTab()
        }

        Scaffold(
            topBar = { scrollBehavior ->
                SearchToolbar(
                    titleContent = { AppBarTitle(stringResource(tab.titleRes)) },
                    searchEnabled = tab.searchEnabled,
                    searchQuery = if (tab.searchEnabled) extensionsSearchQuery else null,
                    onChangeSearchQuery = extensionsViewModel::search,
                    navigateUp = navigator::pop,
                    actions = { AppBarActions(tab.actions) },
                    scrollBehavior = scrollBehavior,
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { contentPadding ->
            tab.content(contentPadding, snackbarHostState)
        }
    }
}

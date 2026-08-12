package eu.kanade.tachiyomi.ui.browse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.presentation.browse.PopularSourcePickerDialog
import eu.kanade.presentation.browse.SearchHomeScreen
import eu.kanade.presentation.browse.SourceUiModel
import eu.kanade.presentation.browse.components.BrowseSearchField
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.components.SearchToolbar
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.ui.browse.source.SourcesViewModel
import eu.kanade.tachiyomi.ui.browse.source.browse.BrowseSourceScreen
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.GlobalSearchScreen
import eu.kanade.tachiyomi.ui.home.GenreScreen
import eu.kanade.tachiyomi.ui.home.LibraryFilterViewModel
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

private const val MAX_RECENT_SEARCHES = 8
private const val MAX_SOURCE_CHIPS = 15
private const val MAX_GENRE_CHIPS = 12

data object BrowseTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = stringResource(MR.strings.action_search),
            icon = rememberVectorPainter(Icons.Outlined.Search),
        )

    override suspend fun onReselect(navigator: Navigator) {
        navigator.push(GlobalSearchScreen())
    }

    private val switchToExtensionTabChannel = Channel<Unit>(1, BufferOverflow.DROP_OLDEST)

    fun showExtension() {
        switchToExtensionTabChannel.trySend(Unit)
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val sourcePreferences = remember { Injekt.get<SourcePreferences>() }

        val sourcesViewModel = viewModel<SourcesViewModel>()
        val sourcesState by sourcesViewModel.state.collectAsStateWithLifecycle()

        val libraryFilterViewModel = viewModel<LibraryFilterViewModel>()
        val library by libraryFilterViewModel.library.collectAsStateWithLifecycle()
        val genres = remember(library) {
            libraryFilterViewModel.genreIndex(library).take(MAX_GENRE_CHIPS)
        }

        val popularShelfViewModel = viewModel<PopularShelfViewModel>()
        val popularState by popularShelfViewModel.state.collectAsStateWithLifecycle()
        val popularSuccess = popularState as? PopularShelfState.Success

        var query by rememberSaveable { mutableStateOf("") }
        var recents by remember {
            mutableStateOf(sourcePreferences.recentSearches.get().lines().filter { it.isNotBlank() })
        }

        fun persistRecents(updated: List<String>) {
            recents = updated
            sourcePreferences.recentSearches.set(updated.joinToString("\n"))
        }

        fun runSearch(raw: String) {
            val trimmed = raw.trim()
            if (trimmed.isEmpty()) return
            val deduped = listOf(trimmed) + recents.filterNot { it.equals(trimmed, ignoreCase = true) }
            persistRecents(deduped.take(MAX_RECENT_SEARCHES))
            navigator.push(GlobalSearchScreen(trimmed))
        }

        val allSources = remember(sourcesState.items) {
            sourcesState.items
                .filterIsInstance<SourceUiModel.Item>()
                .map { it.source }
                .distinctBy { it.id }
        }
        val sources = remember(allSources) { allSources.take(MAX_SOURCE_CHIPS) }
        var showSourcePicker by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                SearchToolbar(
                    searchQuery = null,
                    onChangeSearchQuery = {},
                    searchEnabled = false,
                    titleContent = {
                        BrowseSearchField(
                            query = query,
                            placeholder = stringResource(MR.strings.browse_search_all_sources),
                            onQueryChange = { query = it },
                            onSubmit = { runSearch(it) },
                        )
                    },
                    // Sources, extensions and migration are destinations, not scenery. They live
                    // behind the overflow so the screen itself stays about searching.
                    actions = {
                        AppBarActions(
                            listOf(
                                AppBar.OverflowAction(
                                    title = stringResource(MR.strings.label_sources),
                                    onClick = {
                                        navigator.push(BrowseSectionScreen(BrowseSection.Sources))
                                    },
                                ),
                                AppBar.OverflowAction(
                                    title = stringResource(MR.strings.label_extensions),
                                    onClick = {
                                        navigator.push(BrowseSectionScreen(BrowseSection.Extensions))
                                    },
                                ),
                                AppBar.OverflowAction(
                                    title = stringResource(MR.strings.browse_migrate),
                                    onClick = {
                                        navigator.push(BrowseSectionScreen(BrowseSection.Migrate))
                                    },
                                ),
                            ),
                        )
                    },
                )
            },
        ) { contentPadding ->
            SearchHomeScreen(
                recentSearches = recents,
                sources = sources,
                contentPadding = contentPadding,
                onSearch = { runSearch(it) },
                onRemoveRecent = { removed -> persistRecents(recents.filterNot { it == removed }) },
                onClearRecents = { persistRecents(emptyList()) },
                onClickSource = { navigator.push(BrowseSourceScreen(it.id, null)) },
                popularSourceName = popularSuccess?.sourceName,
                popularManga = popularSuccess?.manga.orEmpty(),
                onClickManga = { navigator.push(MangaScreen(it.id)) },
                onChangePopularSource = { showSourcePicker = true },
                genres = genres,
                onClickGenre = { navigator.push(GenreScreen(it)) },
            )
        }

        if (showSourcePicker) {
            PopularSourcePickerDialog(
                sources = allSources,
                onDismissRequest = { showSourcePicker = false },
                onSelect = { popularShelfViewModel.setSource(it.id) },
            )
        }

        LaunchedEffect(Unit) {
            switchToExtensionTabChannel.receiveAsFlow().collectLatest {
                navigator.push(BrowseSectionScreen(BrowseSection.Extensions))
            }
        }

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}

package eu.kanade.tachiyomi.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.presentation.components.CompactTab
import eu.kanade.presentation.components.CompactTabRow
import eu.kanade.presentation.home.GenreIndexScreen
import eu.kanade.presentation.home.LibraryFilterScreen
import eu.kanade.presentation.home.RecentScreen
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.ui.history.HistoryTab
import eu.kanade.tachiyomi.ui.history.HistoryViewModel
import eu.kanade.tachiyomi.ui.library.LibraryTab
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import androidx.compose.runtime.LaunchedEffect as ComposeLaunchedEffect

/**
 * Single destination merging the reading feed, the library and history.
 *
 * The Library and History pages delegate to the existing tabs, which each bring their own toolbar.
 * That means a page's toolbar sits below this tab row rather than above it — the tradeoff for not
 * refactoring both screens to split toolbar from content.
 */
data object HomeTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = stringResource(MR.strings.label_home),
            icon = rememberVectorPainter(Icons.Outlined.Home),
        )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val filterViewModel = viewModel<LibraryFilterViewModel>()
        val library by filterViewModel.library.collectAsStateWithLifecycle()

        // One collector for the whole tab: every filter page shares the view model.
        ComposeLaunchedEffect(Unit) {
            filterViewModel.events.collectLatest { event ->
                if (event is LibraryFilterViewModel.Event.OpenChapter) {
                    val chapter = event.chapter ?: return@collectLatest
                    context.startActivity(
                        ReaderActivity.newIntent(context, chapter.mangaId, chapter.id),
                    )
                }
            }
        }

        val tabs = listOf(
            CompactTab(stringResource(MR.strings.home_tab_recent)),
            CompactTab(stringResource(MR.strings.label_library)),
            CompactTab(stringResource(MR.strings.home_tab_unread)),
            CompactTab(stringResource(MR.strings.home_tab_finished)),
            CompactTab(stringResource(MR.strings.home_tab_downloaded)),
            CompactTab(stringResource(MR.strings.home_tab_genres)),
            CompactTab(stringResource(MR.strings.label_recent_manga)),
        )

        val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            // Chips rather than a PrimaryTabRow: six destinations do not fit a fixed-width row,
            // and this matches the quiet tab styling used elsewhere.
            CompactTabRow(
                tabs = tabs,
                selectedIndex = pagerState.currentPage,
                onSelect = { scope.launch { pagerState.animateScrollToPage(it) } },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (page) {
                    0 -> RecentPage()
                    1 -> LibraryTab.Content()
                    2 -> FilterPage(filterViewModel.unread(library), navigator, filterViewModel)
                    3 -> FilterPage(filterViewModel.finished(library), navigator, filterViewModel)
                    4 -> FilterPage(filterViewModel.downloaded(library), navigator, filterViewModel)
                    5 -> GenreIndexScreen(
                        genres = remember(library) { filterViewModel.genreIndex(library) },
                        contentPadding = PaddingValues(bottom = 16.dp),
                        onClickGenre = { navigator.push(GenreScreen(it)) },
                    )
                    else -> HistoryTab.Content()
                }
            }
        }
    }
}

@Composable
private fun FilterPage(
    entries: List<LibraryManga>,
    navigator: Navigator,
    viewModel: LibraryFilterViewModel,
) {
    LibraryFilterScreen(
        entries = entries,
        contentPadding = PaddingValues(bottom = 16.dp),
        onClick = { navigator.push(MangaScreen(it.manga.id)) },
        onClickContinue = { viewModel.openNextChapter(it.manga.id) },
    )
}

@Composable
private fun RecentPage() {
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    val viewModel = viewModel<HistoryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecentScreen(
        history = state.list,
        contentPadding = PaddingValues(bottom = 16.dp),
        onClickCover = { navigator.push(MangaScreen(it.mangaId)) },
        onClickResume = { viewModel.getNextChapterForManga(it.mangaId, it.chapterId) },
    )

    // The splash screen waits on this; Library and History set it too, but only the composed page
    // runs, so Home has to signal readiness itself.
    ComposeLaunchedEffect(state.list) {
        if (state.list != null) {
            (context as? MainActivity)?.ready = true
        }
    }

    ComposeLaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is HistoryViewModel.Event.OpenChapter) {
                val chapter = event.chapter ?: return@collectLatest
                context.startActivity(
                    ReaderActivity.newIntent(context, chapter.mangaId, chapter.id),
                )
            }
        }
    }
}

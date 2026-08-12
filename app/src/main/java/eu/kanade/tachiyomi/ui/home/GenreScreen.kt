package eu.kanade.tachiyomi.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBarTitle
import eu.kanade.presentation.components.SearchToolbar
import eu.kanade.presentation.home.LibraryFilterScreen
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import kotlinx.coroutines.flow.collectLatest
import tachiyomi.presentation.core.components.material.Scaffold

/**
 * Your library entries carrying one genre.
 *
 * A single destination with three ways in — the Genres tab, the genre chips on Search, and the
 * tag menu on a manga screen — so genre browsing behaves the same however you reach it.
 */
data class GenreScreen(private val genre: String) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = viewModel<LibraryFilterViewModel>()
        val library by viewModel.library.collectAsStateWithLifecycle()

        val entries = remember(library, genre) { viewModel.withGenre(library, genre) }

        Scaffold(
            topBar = { scrollBehavior ->
                SearchToolbar(
                    titleContent = { AppBarTitle(genre) },
                    searchEnabled = false,
                    searchQuery = null,
                    onChangeSearchQuery = {},
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { contentPadding ->
            LibraryFilterScreen(
                entries = entries,
                contentPadding = contentPadding,
                onClick = { navigator.push(MangaScreen(it.manga.id)) },
                onClickContinue = { viewModel.openNextChapter(it.manga.id) },
            )
        }

        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.events.collectLatest { event ->
                if (event is LibraryFilterViewModel.Event.OpenChapter) {
                    val chapter = event.chapter ?: return@collectLatest
                    context.startActivity(
                        ReaderActivity.newIntent(context, chapter.mangaId, chapter.id),
                    )
                }
            }
        }
    }
}

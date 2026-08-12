package eu.kanade.tachiyomi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kanade.tachiyomi.data.download.DownloadManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.history.interactor.GetNextChapters
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.domain.manga.interactor.GetLibraryManga
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

private const val STOP_TIMEOUT_MS = 5_000L

/**
 * Backs the Unread / Read / Downloaded pages on Home.
 *
 * Reads the library directly rather than going through the full library screen model, because
 * these pages only need a filtered list — no categories, sorting, selection or filter state.
 */
class LibraryFilterViewModel(
    getLibraryManga: GetLibraryManga = Injekt.get(),
    private val downloadManager: DownloadManager = Injekt.get(),
    private val getNextChapters: GetNextChapters = Injekt.get(),
) : ViewModel() {

    val library: StateFlow<List<LibraryManga>> = getLibraryManga.subscribe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    private val _events = Channel<Event>()
    val events: Flow<Event> = _events.receiveAsFlow()

    /** Resolves the next unread chapter and hands it back for the caller to open. */
    fun openNextChapter(mangaId: Long) {
        viewModelScope.launchIO {
            _events.send(Event.OpenChapter(getNextChapters.await(mangaId, onlyUnread = true).firstOrNull()))
        }
    }

    sealed interface Event {
        data class OpenChapter(val chapter: Chapter?) : Event
    }

    fun unread(entries: List<LibraryManga>): List<LibraryManga> =
        entries.filter { it.unreadCount > 0 }.sortedByDescending { it.lastRead }

    /**
     * Started and fully caught up. Deliberately not "everything you've read at all" — a series
     * you're partway through would then sit in both Unread and here, and the tabs would stop
     * being mutually exclusive.
     */
    fun finished(entries: List<LibraryManga>): List<LibraryManga> =
        entries.filter { it.readCount > 0 && it.unreadCount == 0L }.sortedByDescending { it.lastRead }

    fun downloaded(entries: List<LibraryManga>): List<LibraryManga> =
        entries.filter { downloadManager.getDownloadCount(it.manga) > 0 }
            .sortedByDescending { it.lastRead }

    /**
     * Every genre across the library with how many entries carry it, most common first.
     *
     * Genres arrive as free text from sources, so they're matched case-insensitively and the
     * first-seen spelling is kept as the display label.
     */
    fun genreIndex(entries: List<LibraryManga>): List<GenreCount> {
        val counts = LinkedHashMap<String, GenreCount>()
        entries.forEach { entry ->
            entry.manga.genre.orEmpty().forEach { raw ->
                val genre = raw.trim()
                if (genre.isEmpty()) return@forEach
                val key = genre.lowercase()
                val existing = counts[key]
                counts[key] = existing?.copy(count = existing.count + 1)
                    ?: GenreCount(name = genre, count = 1)
            }
        }
        return counts.values.sortedWith(compareByDescending<GenreCount> { it.count }.thenBy { it.name })
    }

    fun withGenre(entries: List<LibraryManga>, genre: String): List<LibraryManga> =
        entries.filter { entry ->
            entry.manga.genre.orEmpty().any { it.trim().equals(genre, ignoreCase = true) }
        }.sortedByDescending { it.lastRead }
}

data class GenreCount(val name: String, val count: Int)

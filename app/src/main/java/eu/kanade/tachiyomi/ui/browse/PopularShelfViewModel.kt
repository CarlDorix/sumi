package eu.kanade.tachiyomi.ui.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.tachiyomi.source.CatalogueSource
import tachiyomi.domain.source.service.SourceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mihon.domain.manga.model.toDomainManga
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.manga.interactor.NetworkToLocalManga
import tachiyomi.domain.manga.model.Manga
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

private const val MAX_SHELF_ITEMS = 20

/**
 * Loads one page of popular titles from a single source to give the search screen something to
 * look at before you've typed anything.
 *
 * Failure is silent by design: this is a decorative shelf on a screen whose actual job is search,
 * so a dead source or a dropped connection should make it disappear, not throw an error block in
 * front of the search field.
 */
class PopularShelfViewModel(
    private val sourceManager: SourceManager = Injekt.get(),
    private val sourcePreferences: SourcePreferences = Injekt.get(),
    private val networkToLocalManga: NetworkToLocalManga = Injekt.get(),
) : ViewModel() {

    private val _state = MutableStateFlow<PopularShelfState>(PopularShelfState.Loading)
    val state: StateFlow<PopularShelfState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launchIO {
            val source = pickSource()
            if (source == null) {
                _state.value = PopularShelfState.Unavailable
                return@launchIO
            }

            try {
                val page = source.getPopularManga(1)
                val manga = networkToLocalManga(
                    page.mangas.map { it.toDomainManga(source.id) },
                )
                _state.value = if (manga.isEmpty()) {
                    PopularShelfState.Unavailable
                } else {
                    PopularShelfState.Success(source.name, manga.take(MAX_SHELF_ITEMS))
                }
            } catch (e: Throwable) {
                logcat(throwable = e) { "Popular shelf failed to load" }
                _state.value = PopularShelfState.Unavailable
            }
        }
    }

    /** Switches the shelf to a specific source and remembers the choice. */
    fun setSource(sourceId: Long) {
        sourcePreferences.popularShelfSource.set(sourceId)
        _state.value = PopularShelfState.Loading
        load()
    }

    /**
     * Your explicit choice if you've made one, otherwise the last source you browsed, otherwise
     * any pinned source. Each candidate is skipped if its extension isn't currently installed.
     */
    private fun pickSource(): CatalogueSource? {
        val chosen = sourcePreferences.popularShelfSource.get().takeIf { it != -1L }
        val lastUsed = sourcePreferences.lastUsedSource.get().takeIf { it != -1L }
        val candidates = listOfNotNull(chosen, lastUsed) +
            sourcePreferences.pinnedSources.get().mapNotNull { it.toLongOrNull() }

        return candidates.firstNotNullOfOrNull { sourceManager.get(it) as? CatalogueSource }
    }
}

sealed interface PopularShelfState {
    data object Loading : PopularShelfState
    data object Unavailable : PopularShelfState
    data class Success(val sourceName: String, val manga: List<Manga>) : PopularShelfState
}

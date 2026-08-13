package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.tachiyomi.ui.library.LibraryItem
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.domain.manga.model.MangaCover

/** Comfortable trades density for presence: fewer, larger covers with room for context below. */
private const val COMFORTABLE_COLUMNS = 3

@Composable
internal fun LibraryComfortableGrid(
    items: List<LibraryItem>,
    columns: Int,
    contentPadding: PaddingValues,
    selection: Set<Long>,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickContinueReading: ((LibraryManga) -> Unit)?,
    searchQuery: String?,
    onGlobalSearchClicked: () -> Unit,
) {
    val heroItem = if (searchQuery.isNullOrEmpty() && selection.isEmpty()) items.heroCandidate() else null
    val onResume: ((LibraryItem) -> Unit)? = onClickContinueReading?.let { resume ->
        { item: LibraryItem -> resume(item.libraryManga) }
    }

    // An explicit column preference still wins; this only sets the default.
    val gridColumns = if (columns == 0) COMFORTABLE_COLUMNS else columns

    LazyLibraryGrid(
        modifier = Modifier.fillMaxSize(),
        columns = gridColumns,
        contentPadding = contentPadding,
    ) {
        globalSearchItem(searchQuery, onGlobalSearchClicked)
        libraryHeroItem(
            item = heroItem,
            onClick = { onClick(it.libraryManga) },
            onClickResume = onResume,
        )

        // No double-width featured tiles here. Comfortable is about an even, unhurried grid;
        // tiles spanning two cells break the rhythm and cost density this mode can least spare.
        items(
            items = items,
            contentType = { "library_comfortable_grid_item" },
        ) { libraryItem ->
            val manga = libraryItem.libraryManga.manga

            MangaComfortableGridItem(
                isSelected = manga.id in selection,
                title = manga.title,
                subtitle = libraryItem.libraryManga.let { lm ->
                    if (lm.totalChapters > 0) "${lm.readCount} / ${lm.totalChapters}" else null
                },
                readProgress = libraryItem.readProgress,
                coverData = MangaCover(
                    mangaId = manga.id,
                    sourceId = manga.source,
                    isMangaFavorite = manga.favorite,
                    url = manga.thumbnailUrl,
                    lastModified = manga.coverLastModified,
                ),
                coverBadgeStart = {
                    DownloadsBadge(count = libraryItem.badges.downloadCount)
                    UnreadBadge(count = libraryItem.badges.unreadCount)
                },
                coverBadgeEnd = {
                    LanguageBadge(
                        isLocal = libraryItem.badges.isLocal,
                        sourceLanguage = libraryItem.badges.sourceLanguage,
                    )
                },
                onLongClick = { onLongClick(libraryItem.libraryManga) },
                onClick = { onClick(libraryItem.libraryManga) },
                onClickContinueReading = if (onClickContinueReading != null && libraryItem.unreadCount > 0) {
                    { onClickContinueReading(libraryItem.libraryManga) }
                } else {
                    null
                },
            )
        }
    }
}

package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.tachiyomi.ui.library.LibraryItem
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.domain.manga.model.MangaCover

/** Compact fits more per row than the other grids: its titles sit on the artwork, not beside it. */
private const val COMPACT_COLUMNS = 4

@Composable
internal fun LibraryCompactGrid(
    items: List<LibraryItem>,
    showTitle: Boolean,
    columns: Int,
    contentPadding: PaddingValues,
    selection: Set<Long>,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickContinueReading: ((LibraryManga) -> Unit)?,
    searchQuery: String?,
    onGlobalSearchClicked: () -> Unit,
) {
    // showTitle is false only in Cover-only mode, which shows no text anywhere — including the
    // hero card.
    val heroItem = if (showTitle && searchQuery.isNullOrEmpty() && selection.isEmpty()) {
        items.heroCandidate()
    } else {
        null
    }
    val onResume: ((LibraryItem) -> Unit)? = onClickContinueReading?.let { resume ->
        { item: LibraryItem -> resume(item.libraryManga) }
    }

    // An explicit column preference still wins; this only sets the default.
    val gridColumns = if (columns == 0) COMPACT_COLUMNS else columns

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

        // Cover-only means exactly that: no titles, and no featured tiles either, since those
        // carry text. It leaves a uniform wall of artwork, which is the point of the mode and
        // what makes it read differently from Compact.
        fun featured(index: Int) = showTitle && isFeaturedIndex(index, gridColumns)

        items(
            count = items.size,
            span = { index -> GridItemSpan(if (featured(index)) 2 else 1) },
            contentType = { index ->
                if (featured(index)) "library_featured_tile" else "library_compact_grid_item"
            },
        ) { index ->
            val libraryItem = items[index]
            val manga = libraryItem.libraryManga.manga

            if (featured(index)) {
                LibraryFeaturedTile(
                    item = libraryItem,
                    isSelected = manga.id in selection,
                    onClick = { onClick(libraryItem.libraryManga) },
                    onLongClick = { onLongClick(libraryItem.libraryManga) },
                )
            } else {
                MangaCompactGridItem(
                    isSelected = manga.id in selection,
                    title = manga.title.takeIf { showTitle },
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
}

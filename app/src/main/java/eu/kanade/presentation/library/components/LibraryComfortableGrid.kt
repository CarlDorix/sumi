package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.tachiyomi.ui.library.LibraryItem
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.domain.manga.model.MangaCover

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

    LazyLibraryGrid(
        modifier = Modifier.fillMaxSize(),
        columns = columns,
        contentPadding = contentPadding,
    ) {
        globalSearchItem(searchQuery, onGlobalSearchClicked)
        libraryHeroItem(
            item = heroItem,
            onClick = { onClick(it.libraryManga) },
            onClickResume = onResume,
        )

        items(
            count = items.size,
            span = { index -> GridItemSpan(if (isFeaturedIndex(index, columns)) 2 else 1) },
            contentType = { index ->
                if (isFeaturedIndex(index, columns)) "library_featured_tile" else "library_comfortable_grid_item"
            },
        ) { index ->
            val libraryItem = items[index]
            val manga = libraryItem.libraryManga.manga

            if (isFeaturedIndex(index, columns)) {
                LibraryFeaturedTile(
                    item = libraryItem,
                    isSelected = manga.id in selection,
                    onClick = { onClick(libraryItem.libraryManga) },
                    onLongClick = { onLongClick(libraryItem.libraryManga) },
                )
            } else {
                MangaComfortableGridItem(
                    isSelected = manga.id in selection,
                    title = manga.title,
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

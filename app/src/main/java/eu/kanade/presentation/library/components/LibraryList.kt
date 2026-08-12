package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.components.SeriesListRow
import eu.kanade.presentation.components.Stat
import eu.kanade.presentation.components.StatsRow
import eu.kanade.presentation.components.relativeDateText
import eu.kanade.tachiyomi.ui.library.LibraryItem
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.FastScrollLazyColumn
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

@Composable
internal fun LibraryList(
    items: List<LibraryItem>,
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

    FastScrollLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item {
            if (!searchQuery.isNullOrEmpty()) {
                GlobalSearchItem(
                    modifier = Modifier.fillMaxWidth(),
                    searchQuery = searchQuery,
                    onClick = onGlobalSearchClicked,
                )
            }
        }

        libraryHeroItem(
            item = heroItem,
            onClick = { onClick(it.libraryManga) },
            onClickResume = onResume,
        )

        item(key = "library_stats", contentType = "library_stats") {
            StatsRow(
                stats = listOf(
                    Stat(items.size, stringResource(MR.strings.library_series_total)),
                    Stat(items.count { it.unreadCount > 0 }, stringResource(MR.strings.library_unread_series)),
                ),
            )
        }

        items(
            items = items,
            contentType = { "library_list_item" },
        ) { libraryItem ->
            val libraryManga = libraryItem.libraryManga
            val manga = libraryManga.manga

            SeriesListRow(
                coverData = MangaCoverModel(
                    mangaId = manga.id,
                    sourceId = manga.source,
                    isMangaFavorite = manga.favorite,
                    url = manga.thumbnailUrl,
                    lastModified = manga.coverLastModified,
                ),
                title = manga.title,
                subtitle = if (libraryManga.readCount > 0) {
                    stringResource(MR.strings.library_chapter_number, libraryManga.readCount.toString())
                } else {
                    null
                },
                caption = if (libraryManga.lastRead > 0) {
                    stringResource(MR.strings.library_last_read, relativeDateText(libraryManga.lastRead))
                } else {
                    null
                },
                readProgress = libraryItem.readProgress,
                isSelected = manga.id in selection,
                onClick = { onClick(libraryManga) },
                onLongClick = { onLongClick(libraryManga) },
                onClickContinue = if (onClickContinueReading != null && libraryItem.unreadCount > 0) {
                    { onClickContinueReading(libraryManga) }
                } else {
                    null
                },
                badges = {
                    DownloadsBadge(count = libraryItem.badges.downloadCount)
                    UnreadBadge(count = libraryItem.badges.unreadCount)
                },
            )
        }
    }
}

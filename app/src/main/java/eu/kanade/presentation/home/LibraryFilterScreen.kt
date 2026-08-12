package eu.kanade.presentation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.presentation.components.SeriesListRow
import eu.kanade.presentation.components.relativeDateText
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

/**
 * A filtered slice of the library rendered with the shared [SeriesListRow], so Unread, Read and
 * Downloaded look identical to the library list itself rather than like a separate feature.
 */
@Composable
fun LibraryFilterScreen(
    entries: List<LibraryManga>,
    contentPadding: PaddingValues,
    onClick: (LibraryManga) -> Unit,
    onClickContinue: ((LibraryManga) -> Unit)?,
) {
    if (entries.isEmpty()) {
        EmptyScreen(
            stringRes = MR.strings.information_no_manga_category,
            modifier = Modifier.padding(contentPadding),
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(
            items = entries,
            key = { "filtered-${it.id}" },
            contentType = { "filtered_row" },
        ) { entry ->
            val manga = entry.manga
            SeriesListRow(
                coverData = MangaCoverModel(
                    mangaId = manga.id,
                    sourceId = manga.source,
                    isMangaFavorite = manga.favorite,
                    url = manga.thumbnailUrl,
                    lastModified = manga.coverLastModified,
                ),
                title = manga.title,
                subtitle = if (entry.readCount > 0) {
                    stringResource(MR.strings.library_chapter_number, entry.readCount.toString())
                } else {
                    null
                },
                caption = if (entry.lastRead > 0) {
                    stringResource(MR.strings.library_last_read, relativeDateText(entry.lastRead))
                } else {
                    null
                },
                readProgress = if (entry.readCount > 0 && entry.totalChapters > 0) {
                    (entry.readCount.toFloat() / entry.totalChapters).coerceIn(0f, 1f)
                } else {
                    null
                },
                onClick = { onClick(entry) },
                onClickContinue = if (onClickContinue != null && entry.unreadCount > 0) {
                    { onClickContinue(entry) }
                } else {
                    null
                },
            )
        }
    }
}

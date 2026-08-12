package eu.kanade.presentation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import eu.kanade.presentation.components.CoverShelf
import eu.kanade.presentation.components.SectionHeader
import eu.kanade.presentation.components.SeriesListRow
import eu.kanade.presentation.components.ShelfItem
import eu.kanade.presentation.components.Stat
import eu.kanade.presentation.components.StatsRow
import eu.kanade.presentation.history.HistoryUiModel
import eu.kanade.presentation.util.formatChapterNumber
import tachiyomi.domain.history.model.HistoryWithRelations
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen

private const val SHELF_LIMIT = 12

/**
 * The home feed: what you were last reading, surfaced in one screen.
 *
 * Built entirely from reading history, deduplicated to one entry per series, so it answers
 * "what do I open next" rather than "what do I own".
 */
@Composable
fun RecentScreen(
    history: List<HistoryUiModel>?,
    contentPadding: PaddingValues,
    onClickCover: (HistoryWithRelations) -> Unit,
    onClickResume: (HistoryWithRelations) -> Unit,
) {
    if (history == null) {
        LoadingScreen(Modifier.padding(contentPadding))
        return
    }

    val allItems = remember(history) {
        history.filterIsInstance<HistoryUiModel.Item>().map { it.item }
    }
    val series = remember(allItems) { allItems.distinctBy { it.mangaId } }

    if (series.isEmpty()) {
        EmptyScreen(
            stringRes = MR.strings.information_no_recent_manga,
            modifier = Modifier.padding(contentPadding),
        )
        return
    }

    val shelf = remember(series) {
        series.take(SHELF_LIMIT).map {
            ShelfItem(key = "shelf-${it.mangaId}", coverData = it.coverData, title = it.title)
        }
    }
    val byKey = remember(series) { series.associateBy { "shelf-${it.mangaId}" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item(key = "home_stats") {
            StatsRow(
                stats = listOf(
                    Stat(series.size, stringResource(MR.strings.home_stat_series)),
                    Stat(allItems.size, stringResource(MR.strings.home_stat_chapters)),
                ),
            )
        }

        item(key = "home_shelf") {
            SectionHeader(stringResource(MR.strings.home_jump_back_in))
            CoverShelf(
                items = shelf,
                onClick = { item -> byKey[item.key]?.let(onClickResume) },
            )
        }

        item(key = "home_recent_header") {
            SectionHeader(stringResource(MR.strings.home_recently_read))
        }

        items(
            items = series,
            key = { "recent-${it.mangaId}" },
            contentType = { "recent_row" },
        ) { entry ->
            SeriesListRow(
                coverData = entry.coverData,
                title = entry.title,
                subtitle = if (entry.chapterNumber > -1) {
                    stringResource(
                        MR.strings.history_chapter_single,
                        formatChapterNumber(entry.chapterNumber),
                    )
                } else {
                    null
                },
                onClick = { onClickResume(entry) },
                onClickCover = { onClickCover(entry) },
                onClickContinue = { onClickResume(entry) },
            )
        }
    }
}

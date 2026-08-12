package eu.kanade.presentation.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import eu.kanade.presentation.components.SeriesListRow
import eu.kanade.presentation.history.HistoryRun
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.util.formatChapterNumber
import eu.kanade.tachiyomi.util.lang.toTimestampString
import tachiyomi.domain.history.model.HistoryWithRelations
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource

/**
 * A history row, representing either a single chapter or a whole run read back to back.
 *
 * Uses the same [SeriesListRow] as the library so both lists read as one surface. Row actions are
 * swipes rather than buttons: swiping left asks to remove the run, swiping right adds the series to
 * the library. Neither swipe actually dismisses the row — removal is confirmed in a dialog and the
 * list rebuilds from the database, so the row springs back either way.
 */
@Composable
fun HistoryItem(
    run: HistoryRun,
    onClickCover: () -> Unit,
    onClickResume: () -> Unit,
    onClickDelete: () -> Unit,
    onClickFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canFavorite = !run.latest.coverData.isMangaFavorite
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> onClickDelete()
                SwipeToDismissBoxValue.StartToEnd -> onClickFavorite()
                SwipeToDismissBoxValue.Settled -> Unit
            }
            false
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = canFavorite,
        backgroundContent = { HistorySwipeBackground(dismissState.dismissDirection) },
    ) {
        SeriesListRow(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            coverData = run.latest.coverData,
            title = run.latest.title,
            subtitle = chapterLabel(run),
            caption = captionLabel(run),
            onClick = onClickResume,
            onClickCover = onClickCover,
            onClickContinue = onClickResume,
        )
    }
}

@Composable
private fun chapterLabel(run: HistoryRun): String = when {
    run.latest.chapterNumber < 0 -> ""
    run.isGrouped -> stringResource(
        MR.strings.history_chapter_range,
        formatChapterNumber(run.lowestChapterNumber),
        formatChapterNumber(run.highestChapterNumber),
    )
    else -> stringResource(
        MR.strings.history_chapter_single,
        formatChapterNumber(run.latest.chapterNumber),
    )
}

@Composable
private fun captionLabel(run: HistoryRun): String {
    val time = run.endedAt?.toTimestampString().orEmpty()
    if (!run.isGrouped) return time
    val count = pluralStringResource(MR.plurals.manga_num_chapters, run.size, run.size)
    return if (time.isEmpty()) count else "$time  ·  $count"
}

@Composable
private fun HistorySwipeBackground(direction: SwipeToDismissBoxValue) {
    val isDelete = direction == SwipeToDismissBoxValue.EndToStart
    val color = when (direction) {
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = MaterialTheme.padding.large),
        contentAlignment = if (isDelete) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        if (direction != SwipeToDismissBoxValue.Settled) {
            Icon(
                imageVector = if (isDelete) Icons.Outlined.Delete else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(
                    if (isDelete) MR.strings.action_delete else MR.strings.add_to_library,
                ),
                tint = if (isDelete) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HistoryItemPreviews(
    @PreviewParameter(HistoryWithRelationsProvider::class)
    historyWithRelations: HistoryWithRelations,
) {
    TachiyomiPreviewTheme {
        Surface {
            HistoryItem(
                run = HistoryRun(listOf(historyWithRelations)),
                onClickCover = {},
                onClickResume = {},
                onClickDelete = {},
                onClickFavorite = {},
            )
        }
    }
}

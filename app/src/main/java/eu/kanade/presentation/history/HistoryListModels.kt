package eu.kanade.presentation.history

import kotlinx.datetime.LocalDate
import tachiyomi.domain.history.model.HistoryWithRelations
import java.util.Date

/**
 * A run of chapters from the same series read back to back.
 *
 * History arrives newest-first, so a reading session is always contiguous in the list and can be
 * folded into one row instead of repeating the same cover and title several times.
 */
data class HistoryRun(
    val chapters: List<HistoryWithRelations>,
) {
    /** Most recent entry in the run. Supplies the cover, title and resume target. */
    val latest: HistoryWithRelations = chapters.first()

    val size: Int = chapters.size

    val isGrouped: Boolean = size > 1

    val lowestChapterNumber: Double = chapters.minOf { it.chapterNumber }
    val highestChapterNumber: Double = chapters.maxOf { it.chapterNumber }

    val endedAt: Date? = chapters.mapNotNull { it.readAt }.maxOrNull()
}

/** One date's worth of history, already folded into runs. */
data class HistoryDay(
    val date: LocalDate?,
    val runs: List<HistoryRun>,
) {
    val chapterCount: Int = runs.sumOf { it.size }

    /** Stable across list rebuilds, so collapsed state survives new history arriving. */
    val key: String = date?.toString() ?: "undated"
}

/**
 * Folds the view model's flat header/item list into days of runs.
 *
 * Entries that arrive before any header (which happens when `readAt` is null and the view model
 * emits no separator for them) are kept under a null date rather than dropped.
 */
fun List<HistoryUiModel>.toHistoryDays(): List<HistoryDay> {
    val days = mutableListOf<HistoryDay>()
    val pending = mutableListOf<HistoryWithRelations>()
    var date: LocalDate? = null

    for (model in this) {
        when (model) {
            is HistoryUiModel.Header -> {
                if (pending.isNotEmpty()) {
                    days += HistoryDay(date, pending.toRuns())
                    pending.clear()
                }
                date = model.date
            }
            is HistoryUiModel.Item -> pending += model.item
        }
    }
    if (pending.isNotEmpty()) {
        days += HistoryDay(date, pending.toRuns())
    }
    return days
}

private fun List<HistoryWithRelations>.toRuns(): List<HistoryRun> {
    val runs = mutableListOf<HistoryRun>()
    val bucket = mutableListOf<HistoryWithRelations>()

    for (entry in this) {
        if (bucket.isNotEmpty() && bucket.first().mangaId != entry.mangaId) {
            runs += HistoryRun(bucket.toList())
            bucket.clear()
        }
        bucket += entry
    }
    if (bucket.isNotEmpty()) {
        runs += HistoryRun(bucket.toList())
    }
    return runs
}

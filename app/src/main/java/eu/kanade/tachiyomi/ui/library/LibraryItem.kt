package eu.kanade.tachiyomi.ui.library

import tachiyomi.domain.library.model.LibraryManga

data class LibraryItem(
    val libraryManga: LibraryManga,
    val downloadCount: Int,
    val unreadCount: Long,
    val isLocal: Boolean,
    val sourceName: String,
    val sourceLanguage: String,
    val badges: Badges,
) {
    val id: Long = libraryManga.id

    /**
     * Fraction of chapters read, or null for entries that haven't been started. Null means
     * "draw nothing", which keeps untouched entries visually quiet.
     */
    val readProgress: Float? = libraryManga
        .takeIf { it.readCount > 0 && it.totalChapters > 0 }
        ?.let { (it.readCount.toFloat() / it.totalChapters).coerceIn(0f, 1f) }

    data class Badges(
        val downloadCount: Int,
        val unreadCount: Long,
        val isLocal: Boolean,
        val sourceLanguage: String,
    )
}

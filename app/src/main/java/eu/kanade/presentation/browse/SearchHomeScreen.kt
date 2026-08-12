package eu.kanade.presentation.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.CoverShelf
import eu.kanade.presentation.components.LabelChip
import eu.kanade.presentation.components.SectionHeader
import eu.kanade.presentation.components.ShelfItem
import eu.kanade.tachiyomi.ui.home.GenreCount
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.source.model.Source
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

/**
 * Landing content for the search tab.
 *
 * There is no source/extension/migrate furniture here on purpose — those are destinations reached
 * from the overflow menu when you actually need them. What stays on screen is what helps you
 * search: what you searched before, what's popular, and where to jump straight in.
 */
@Composable
fun SearchHomeScreen(
    recentSearches: List<String>,
    sources: List<Source>,
    contentPadding: PaddingValues,
    onSearch: (String) -> Unit,
    onRemoveRecent: (String) -> Unit,
    onClearRecents: () -> Unit,
    onClickSource: (Source) -> Unit,
    popularSourceName: String? = null,
    popularManga: List<Manga> = emptyList(),
    onClickManga: (Manga) -> Unit = {},
    onChangePopularSource: () -> Unit = {},
    genres: List<GenreCount> = emptyList(),
    onClickGenre: (String) -> Unit = {},
) {
    val popularShelf = remember(popularManga) {
        popularManga.map { manga ->
            ShelfItem(
                key = "popular-${manga.id}",
                coverData = MangaCoverModel(
                    mangaId = manga.id,
                    sourceId = manga.source,
                    isMangaFavorite = manga.favorite,
                    url = manga.thumbnailUrl,
                    lastModified = manga.coverLastModified,
                ),
                title = manga.title,
            )
        }
    }
    val popularByKey = remember(popularManga) { popularManga.associateBy { "popular-${it.id}" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        if (popularSourceName != null && popularShelf.isNotEmpty()) {
            item(key = "popular_header") {
                SectionHeader(
                    text = stringResource(MR.strings.browse_popular_on, popularSourceName),
                    action = {
                        TextButton(onClick = onChangePopularSource) {
                            Text(stringResource(MR.strings.browse_popular_change_source))
                        }
                    },
                )
            }
            item(key = "popular_shelf") {
                CoverShelf(
                    items = popularShelf,
                    onClick = { item -> popularByKey[item.key]?.let(onClickManga) },
                )
            }
        }

        if (recentSearches.isNotEmpty()) {
            item(key = "recent_header") {
                SectionHeader(
                    text = stringResource(MR.strings.browse_recent_searches),
                    action = {
                        TextButton(onClick = onClearRecents) {
                            Text(stringResource(MR.strings.browse_clear_recent))
                        }
                    },
                )
            }
            items(
                items = recentSearches,
                key = { "recent-$it" },
            ) { query ->
                RecentSearchRow(
                    query = query,
                    onClick = { onSearch(query) },
                    onRemove = { onRemoveRecent(query) },
                )
            }
        }

        if (genres.isNotEmpty()) {
            item(key = "genres_header") {
                SectionHeader(text = stringResource(MR.strings.genre_browse))
            }
            item(key = "genres_row") {
                ChipRow {
                    items(items = genres, key = { "genre-chip-${it.name.lowercase()}" }) { genre ->
                        LabelChip(
                            text = "${genre.name}  ${genre.count}",
                            onClick = { onClickGenre(genre.name) },
                        )
                    }
                }
            }
        }

        if (sources.isNotEmpty()) {
            item(key = "sources_header") {
                SectionHeader(text = stringResource(MR.strings.browse_your_sources))
            }
            item(key = "sources_row") {
                ChipRow {
                    items(items = sources, key = { "source-${it.id}" }) { source ->
                        LabelChip(text = source.name, onClick = { onClickSource(source) })
                    }
                }
            }
        }

        if (recentSearches.isEmpty()) {
            item(key = "hint") {
                Text(
                    text = stringResource(MR.strings.browse_search_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun ChipRow(content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

@Composable
private fun RecentSearchRow(
    query: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = query,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(MR.strings.action_close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/**
 * Picks which source feeds the popular shelf. Any catalogue source you have enabled is fair game,
 * not just pinned ones.
 */
@Composable
fun PopularSourcePickerDialog(
    sources: List<Source>,
    onDismissRequest: () -> Unit,
    onSelect: (Source) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(MR.strings.browse_popular_pick_source)) },
        text = {
            LazyColumn {
                items(items = sources, key = { "picker-${it.id}" }) { source ->
                    Text(
                        text = source.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(source)
                                onDismissRequest()
                            }
                            .padding(vertical = 14.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.action_cancel))
            }
        },
    )
}

package eu.kanade.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import eu.kanade.presentation.components.ReadProgressBar
import eu.kanade.tachiyomi.ui.library.LibraryItem
import tachiyomi.presentation.core.components.BadgeGroup
import eu.kanade.presentation.manga.components.MangaCover as MangaCoverComposable
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

/**
 * Aspect ratio of a featured tile.
 *
 * A tile spans two grid cells, so at 4:3 its height matches the height of a single
 * portrait cover in the same row. That keeps rows flush instead of leaving gaps.
 */
private const val FEATURED_TILE_ASPECT = 4f / 3f

private val FeaturedTileBlurRadius = 16.dp
private val FeaturedProgressBarHeight = 4.dp
private val FeaturedTileSelectedBorder = 3.dp

/**
 * How often a featured tile appears, in items.
 *
 * A featured tile takes two cells, so a cycle of `2 * columns - 1` items lands every featured
 * tile at the start of a row. Any other interval drifts and leaves ragged holes in the grid.
 * Raise the multiplier to make featured tiles rarer (and the grid shorter).
 */
internal fun featuredTileInterval(columns: Int): Int {
    val effectiveColumns = if (columns <= 0) 3 else columns
    return (2 * effectiveColumns - 1).coerceAtLeast(3)
}

internal fun isFeaturedIndex(index: Int, columns: Int): Boolean =
    index % featuredTileInterval(columns) == 0

/**
 * Wide grid tile, styled as a compact echo of the library hero card: the entry's own cover
 * blurred as a backdrop, the sharp cover alongside it, then title and read progress.
 */
@Composable
internal fun LibraryFeaturedTile(
    item: LibraryItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val manga = item.libraryManga.manga
    val coverData = MangaCoverModel(
        mangaId = manga.id,
        sourceId = manga.source,
        isMangaFavorite = manga.favorite,
        url = manga.thumbnailUrl,
        lastModified = manga.coverLastModified,
    )
    val shape = MaterialTheme.shapes.small

    Box(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(FEATURED_TILE_ASPECT)
            .clip(shape)
            .then(
                if (isSelected) {
                    Modifier.border(FeaturedTileSelectedBorder, MaterialTheme.colorScheme.secondary, shape)
                } else {
                    Modifier
                },
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        AsyncImage(
            model = coverData,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .blur(FeaturedTileBlurRadius),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x80000000),
                        1f to Color(0xE0000000),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MangaCoverComposable.Book(
                data = coverData,
                modifier = Modifier.fillMaxHeight(),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
            ) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                val progress = item.readProgress
                if (progress != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${item.libraryManga.readCount} / ${item.libraryManga.totalChapters}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                    )
                    Spacer(Modifier.height(4.dp))
                    ReadProgressBar(
                        progress = progress,
                        trackColor = Color.White.copy(alpha = 0.25f),
                    )
                }
            }
        }

        BadgeGroup(
            modifier = Modifier
                .padding(6.dp)
                .align(Alignment.TopStart),
        ) {
            DownloadsBadge(count = item.badges.downloadCount)
            UnreadBadge(count = item.badges.unreadCount)
        }
    }
}

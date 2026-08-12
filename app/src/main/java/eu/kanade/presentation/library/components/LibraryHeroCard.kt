package eu.kanade.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import eu.kanade.presentation.components.ReadProgressBar
import eu.kanade.tachiyomi.ui.library.LibraryItem
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import eu.kanade.presentation.manga.components.MangaCover as MangaCoverComposable
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

private val HeroCardHeight = 168.dp
private val HeroCardBlurRadius = 24.dp
private val HeroProgressBarHeight = 4.dp
private val HeroResumeButtonSize = 44.dp

/**
 * Picks the entry the hero card should feature: the most recently read entry that still has
 * something left to read. Returns null when nothing qualifies, in which case no card is shown.
 */
fun List<LibraryItem>.heroCandidate(): LibraryItem? = this
    .filter { it.libraryManga.lastRead > 0 && it.unreadCount > 0 }
    .maxByOrNull { it.libraryManga.lastRead }

/**
 * Large "jump back in" card shown above the grid.
 *
 * The entry's own cover is reused twice: blurred and cropped as the card's backdrop, and sharp
 * at reading size on the left. Scrolls away with the grid rather than being pinned, so it costs
 * screen space only once.
 */
@Composable
fun LibraryHeroCard(
    item: LibraryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickResume: (() -> Unit)? = null,
) {
    val manga = item.libraryManga.manga
    val coverData = MangaCoverModel(
        mangaId = manga.id,
        sourceId = manga.source,
        isMangaFavorite = manga.favorite,
        url = manga.thumbnailUrl,
        lastModified = manga.coverLastModified,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HeroCardHeight)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = coverData,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .blur(HeroCardBlurRadius),
        )

        // Keeps the text legible whatever the cover art happens to be.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x99000000),
                        1f to Color(0xE6000000),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MangaCoverComposable.Book(
                data = coverData,
                modifier = Modifier.fillMaxHeight(),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(MR.strings.library_continue_reading).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                HeroProgress(item = item)
            }

            if (onClickResume != null) {
                FilledIconButton(
                    onClick = onClickResume,
                    shape = MaterialTheme.shapes.medium,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier.size(HeroResumeButtonSize),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(MR.strings.action_resume),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroProgress(item: LibraryItem) {
    val libraryManga = item.libraryManga
    val progress = item.readProgress ?: 0f

    Text(
        text = "${libraryManga.readCount} / ${libraryManga.totalChapters}",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.8f),
    )
    Spacer(Modifier.height(6.dp))
    ReadProgressBar(
        progress = progress,
        trackColor = Color.White.copy(alpha = 0.25f),
    )
}

internal fun LazyGridScope.libraryHeroItem(
    item: LibraryItem?,
    onClick: (LibraryItem) -> Unit,
    onClickResume: ((LibraryItem) -> Unit)?,
) {
    if (item == null) return
    item(
        key = "library_hero_card",
        span = { GridItemSpan(maxLineSpan) },
        contentType = { "library_hero_card" },
    ) {
        LibraryHeroCard(
            item = item,
            onClick = { onClick(item) },
            onClickResume = onClickResume?.let { resume -> { resume(item) } },
            modifier = Modifier.padding(bottom = 8.dp),
        )
    }
}

internal fun LazyListScope.libraryHeroItem(
    item: LibraryItem?,
    onClick: (LibraryItem) -> Unit,
    onClickResume: ((LibraryItem) -> Unit)?,
) {
    if (item == null) return
    item(
        key = "library_hero_card",
        contentType = "library_hero_card",
    ) {
        LibraryHeroCard(
            item = item,
            onClick = { onClick(item) },
            onClickResume = onClickResume?.let { resume -> { resume(item) } },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

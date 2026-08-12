package eu.kanade.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.manga.components.MangaCover as MangaCoverComposable
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

/**
 * Shared shape and size tokens.
 *
 * These values were previously repeated as literals across a dozen files, which meant they only
 * matched by coincidence. Changing a radius here now changes it everywhere it should.
 */
object AppShapes {
    /** Tab chips, source chips, genre chips. */
    val Chip = RoundedCornerShape(10.dp)

    /** Grouped preference cards. */
    val Card = RoundedCornerShape(16.dp)

    /** Progress bars and anything else fully rounded. */
    val Pill = RoundedCornerShape(percent = 50)
}

object AppSizes {
    /** Cover width in a horizontal shelf. */
    val ShelfCover = 104.dp

    /** Progress bar overlaid on a cover — thin, because it sits inside the artwork. */
    val ProgressBarOnCover = 3.dp

    /** Progress bar in a card or header, where it is a first-class element. */
    val ProgressBar = 4.dp
}

/**
 * Quiet small-caps label introducing a section, optionally with a trailing action.
 */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 4.dp),
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (action != null) {
            Spacer(Modifier.weight(1f))
            action()
        }
    }
}

data class Stat(val value: Int, val label: String)

/**
 * Row of "42 SERIES · 118 CHAPTERS" style counts.
 */
@Composable
fun StatsRow(
    stats: List<Stat>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        stats.forEach { stat ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stat.value.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stat.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Read-progress bar. One implementation for cover overlays, cards and headers alike.
 */
@Composable
fun ReadProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = AppSizes.ProgressBar,
    shape: Shape = AppShapes.Pill,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
    fillColor: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(fillColor),
        )
    }
}

data class ShelfItem(
    val key: String,
    val coverData: MangaCoverModel,
    val title: String,
    val progress: Float? = null,
)

/**
 * Horizontal cover shelf. Cover-led on purpose — at this size the artwork identifies a series
 * faster than its title does, so titles sit underneath rather than over the image.
 */
@Composable
fun CoverShelf(
    items: List<ShelfItem>,
    onClick: (ShelfItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = items,
            key = { it.key },
        ) { item ->
            Column(
                modifier = Modifier
                    .width(AppSizes.ShelfCover)
                    .clickable { onClick(item) },
            ) {
                Box {
                    MangaCoverComposable.Book(
                        modifier = Modifier.fillMaxWidth(),
                        data = item.coverData,
                    )
                    if (item.progress != null) {
                        ReadProgressBar(
                            progress = item.progress,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            height = AppSizes.ProgressBarOnCover,
                            trackColor = Color.Black.copy(alpha = 0.45f),
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Small filled chip used for sources, genres and similar one-tap targets.
 */
@Composable
fun LabelChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = AppShapes.Chip,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

package eu.kanade.presentation.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.BadgeGroup
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.selectedBackground
import eu.kanade.presentation.manga.components.MangaCover as MangaCoverComposable
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

val SeriesListRowHeight = 104.dp
private val ContinueButtonWidth = 96.dp

/**
 * One series in a vertical list: cover, title, a chapter line, a muted caption, and an optional
 * "Continue reading" action.
 *
 * Shared by the library, history and the home feed so all three read as the same surface rather
 * than three separately styled lists.
 */
@Composable
fun SeriesListRow(
    coverData: MangaCoverModel,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    caption: String? = null,
    readProgress: Float? = null,
    isSelected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onClickCover: (() -> Unit)? = null,
    onClickContinue: (() -> Unit)? = null,
    showDivider: Boolean = true,
    badges: @Composable (RowScope.() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .selectedBackground(isSelected)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick ?: onClick,
                )
                .height(SeriesListRowHeight)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                MangaCoverComposable.Book(
                    modifier = Modifier.fillMaxHeight(),
                    data = coverData,
                    onClick = onClickCover,
                )
                if (badges != null) {
                    BadgeGroup(
                        modifier = Modifier.padding(4.dp),
                        content = badges,
                    )
                }
                // Same bar the grid covers carry. List is the default display mode, so it should
                // not be the view with the least progress signal.
                if (readProgress != null) {
                    ReadProgressBar(
                        progress = readProgress,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        height = AppSizes.ProgressBarOnCover,
                        trackColor = Color.Black.copy(alpha = 0.45f),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    // Slightly tighter than default tracking; at title size the stock spacing
                    // reads loose and makes truncated names look accidental rather than clipped.
                    letterSpacing = (-0.2).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (!caption.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (onClickContinue != null) {
                OutlinedButton(
                    onClick = onClickContinue,
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.width(ContinueButtonWidth),
                ) {
                    Text(
                        text = stringResource(MR.strings.library_continue_reading),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        }
    }
}

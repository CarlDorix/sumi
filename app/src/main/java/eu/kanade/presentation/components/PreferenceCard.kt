package eu.kanade.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * [SectionHeader] indented to line up with the contents of a [PreferenceCard].
 */
@Composable
fun PreferenceSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    SectionHeader(
        text = text,
        modifier = modifier,
        contentPadding = PaddingValues(start = 28.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
    )
}

/**
 * Rounded container grouping related preference rows.
 *
 * Turns a long undifferentiated list into a handful of scannable blocks, which is most of what
 * makes a settings screen readable.
 */
@Composable
fun PreferenceCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = AppShapes.Card,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            content = content,
        )
    }
}

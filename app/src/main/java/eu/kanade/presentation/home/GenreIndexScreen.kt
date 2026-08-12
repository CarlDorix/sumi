package eu.kanade.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.ui.home.GenreCount
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.screens.EmptyScreen

/**
 * Every genre in your library, most common first, with how many entries carry it.
 */
@Composable
fun GenreIndexScreen(
    genres: List<GenreCount>,
    contentPadding: PaddingValues,
    onClickGenre: (String) -> Unit,
) {
    if (genres.isEmpty()) {
        EmptyScreen(
            stringRes = MR.strings.information_no_manga_category,
            modifier = Modifier.padding(contentPadding),
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(
            items = genres,
            key = { "genre-${it.name.lowercase()}" },
            contentType = { "genre_row" },
        ) { genre ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickGenre(genre.name) }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = genre.count.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

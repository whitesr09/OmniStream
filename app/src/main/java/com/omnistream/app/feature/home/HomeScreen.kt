package com.omnistream.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.omnistream.app.core.domain.model.Movie
import com.omnistream.core.ui.theme.OmniBlack

@Composable
fun HomeScreen(onNavigateToPlayer: (String) -> Unit) {
    val mockMovies = listOf(
        Movie("bbb", "Big Buck Bunny", "A giant rabbit...", "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg", "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg", 8.5f, 10),
        Movie("sintel", "Sintel", "A girl searches...", "https://durian.blender.org/wp-content/uploads/2010/06/sintel_poster.jpg", "https://durian.blender.org/wp-content/uploads/2010/06/sintel_poster.jpg", 9.0f, 15)
    )

    LazyColumn(modifier = Modifier.fillMaxSize().background(OmniBlack)) {
        item {
            Box(modifier = Modifier.height(400.dp).fillMaxWidth()) {
                AsyncImage(model = mockMovies[0].backdropUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Column(modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart).padding(16.dp)) {
                    Text(mockMovies[0].title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Play Now", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        item { Text("Trending", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(16.dp)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(mockMovies, key = { it.id }) { movie ->
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(120.dp).height(180.dp).clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        }
    }
}
package com.omnistream.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.omnistream.app.core.ui.components.shimmerEffect
import com.omnistream.app.core.ui.theme.HeroGradientBottom
import com.omnistream.app.core.ui.theme.OmniBlack
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(onNavigateToPlayer: (String) -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize().background(OmniBlack)
    ) {
        item {
            if (state.isLoading) {
                Box(modifier = Modifier.height(400.dp).fillMaxWidth().shimmerEffect())
            } else {
                state.heroItem?.let { hero ->
                    HeroBanner(hero = hero, lazyListState = lazyListState, onPlayClick = { onNavigateToPlayer(hero.id) })
                }
            }
        }
        item { Text("Trending Now", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(16.dp)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.isLoading) {
                    items(3) { Box(modifier = Modifier.width(120.dp).height(180.dp).clip(MaterialTheme.shapes.medium).shimmerEffect()) }
                } else {
                    items(state.trendingItems, key = { it.id }) { movie ->
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
}

@Composable
fun HeroBanner(hero: com.omnistream.app.core.domain.model.Movie, lazyListState: androidx.compose.foundation.lazy.LazyListState, onPlayClick: () -> Unit) {
    val parallaxOffset by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) lazyListState.firstVisibleItemScrollOffset * 0.5f else 0f
        }
    }
    Box(modifier = Modifier.height(450.dp).fillMaxWidth()) {
        AsyncImage(model = hero.backdropUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().graphicsLayer { translationY = parallaxOffset; scaleX = 1.1f; scaleY = 1.1f })
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(androidx.compose.ui.graphics.Color.Transparent, HeroGradientBottom), startY = 200f)))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp).padding(bottom = 24.dp)) {
            Text(hero.title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.FilledTonalButton(onClick = onPlayClick) { Text("Play Now") }
        }
    }
}

package com.omnistream.app.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(mediaId: String, onBackClick: () -> Unit, viewModel: PlayerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val playerManager = viewModel.playerManager
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(mediaId) {
        val url = if (mediaId == "bbb") "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        playerManager.prepareAndPlay(url)
    }

    DisposableEffect(Unit) {
        onDispose { 
            playerManager.exoPlayer.pause() 
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = playerManager.exoPlayer
                    useController = false // We use custom Compose overlay
                }
            },
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showControls = !showControls },
                    onDoubleTap = { offset ->
                        val current = playerManager.exoPlayer.currentPosition
                        if (offset.x < size.width / 2) playerManager.exoPlayer.seekTo((current - 10000).coerceAtLeast(0))
                        else playerManager.exoPlayer.seekTo((current + 10000).coerceAtMost(playerManager.exoPlayer.duration))
                    }
                )
            }
        )

        if (showControls) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            // Auto-hide controls after 3 seconds
            LaunchedEffect(showControls) {
                if (showControls) {
                    delay(3000)
                    showControls = false
                }
            }
        }
    }
}

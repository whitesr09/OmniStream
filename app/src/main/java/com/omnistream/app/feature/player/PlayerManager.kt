package com.omnistream.app.feature.player

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(15000, 50000, 2500, 5000)
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setLoadControl(loadControl)
        .build().apply {
            playWhenReady = true
            setHandleAudioBecomingNoisy(true)
        }

    fun prepareAndPlay(url: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun release() { exoPlayer.release() }
}

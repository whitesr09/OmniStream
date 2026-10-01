package com.omnistream.app.feature.player

import android.os.Bundle
import androidx.activity.ComponentActivity

class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(androidx.media3.ui.PlayerView(this))
    }
}
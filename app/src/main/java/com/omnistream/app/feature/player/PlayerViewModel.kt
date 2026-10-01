package com.omnistream.app.feature.player

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val playerManager: PlayerManager
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}

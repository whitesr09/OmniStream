package com.omnistream.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnistream.app.core.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val heroItem: Movie? = null,
    val trendingItems: List<Movie> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            // Simulate network delay for shimmer effect
            kotlinx.coroutines.delay(800)
            val mockMovies = listOf(
                Movie("bbb", "Big Buck Bunny", "A giant rabbit with a heart bigger than himself.", "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg", "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg", 8.5f, 10),
                Movie("sintel", "Sintel", "A lonely young woman searches for a dragon.", "https://durian.blender.org/wp-content/uploads/2010/06/sintel_poster.jpg", "https://durian.blender.org/wp-content/uploads/2010/06/sintel_poster.jpg", 9.0f, 15)
            )
            _uiState.update { it.copy(isLoading = false, heroItem = mockMovies[0], trendingItems = mockMovies) }
        }
    }
}

package org.tovars.sonar.presentation.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.tovars.sonar.domain.repository.MusicRepository
import org.tovars.sonar.domain.usecase.InitConnectionUseCase


class VideoPlayerViewModel(
    private val repository: MusicRepository,
    private val initConnectionUseCase: InitConnectionUseCase
    //private val savedStateHandle: SavedStateHandle
): ViewModel() {

    var uiState = MutableStateFlow(UiState())
        private set

    data class UiState(
        val isLoading: Boolean = true,
        val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        val errorMessage: String? = null,
        val isFullScreen: Boolean = false,
        val isPlaying: Boolean = false,
        val isBuffering: Boolean = false,
        val isMute: Boolean = false,

    )

    init {

        viewModelScope.launch {
            println("init viewmodel")
            initConnectionUseCase()
        }

        viewModelScope.launch {
            uiState.update {
                UiState(
                    isLoading = false,
                    errorMessage = null,
                    isFullScreen = false,
                    isPlaying = false,
                    isBuffering = false,
                    isMute = false,
                )
            }
        }

    }

}
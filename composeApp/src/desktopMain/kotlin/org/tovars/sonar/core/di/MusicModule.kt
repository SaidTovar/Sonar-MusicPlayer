package org.tovars.sonar.core.di

import io.ktor.client.HttpClient
import okhttp3.OkHttpClient
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.tovars.sonar.data.KtorMusicRepository
import org.tovars.sonar.data.network.SpotifyService
import org.tovars.sonar.domain.repository.MusicRepository
import org.tovars.sonar.domain.usecase.InitConnectionUseCase
import org.tovars.sonar.presentation.Home.VideoPlayerViewModel

@Module
class MusicModule {


    @Single
    fun spotifyService(httpClient: HttpClient): SpotifyService = SpotifyService(httpClient)

    @Factory(binds = [MusicRepository::class])
    fun musicRepository(httpClient: HttpClient, spotifyService: SpotifyService) = KtorMusicRepository(httpClient, spotifyService)

    @Single
    fun initConnectionUseCase(repository: MusicRepository) = InitConnectionUseCase(repository)

    @KoinViewModel
    fun videoPlayerViewModel(repository: MusicRepository, initConnectionUseCase: InitConnectionUseCase) = VideoPlayerViewModel(repository, initConnectionUseCase)

}
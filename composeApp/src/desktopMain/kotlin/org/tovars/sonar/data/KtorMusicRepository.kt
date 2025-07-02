package org.tovars.sonar.data

import io.ktor.client.HttpClient
import org.koin.core.annotation.Single
import org.tovars.sonar.data.network.SpotifyService
import org.tovars.sonar.domain.model.Music
import org.tovars.sonar.domain.repository.MusicRepository

@Single
class KtorMusicRepository (
    private val ktorClient: HttpClient,
    private val spotifyService: SpotifyService
): MusicRepository {

    override suspend fun initConnection() {
        println("initConnection")
        println("Token: ${spotifyService.getAccessToken()}")
    }

    override suspend fun getListOfMusics(): List<String> {
        return listOf("1", "2", "3")
    }

    override suspend fun getMusic(id: Long): Music {
        return Music(id, "title", "artist")
    }

    override suspend fun searchMusics(query: String): List<Music> {
        return listOf(Music(1, "title", "artist"))
    }

}
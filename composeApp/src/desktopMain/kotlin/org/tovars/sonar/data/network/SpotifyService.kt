package org.tovars.sonar.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.util.encodeBase64
import io.ktor.utils.io.InternalAPI
import org.tovars.sonar.BuildKonfig
import org.tovars.sonar.data.model.SpotifyTokenResponse
import org.tovars.sonar.data.model.SpotifyTopTracksResponse
import org.tovars.sonar.data.model.SpotifyTrack

class SpotifyService (
    private val httpClient: HttpClient
){

    companion object {

        private val API_KEY_TOKEN = BuildKonfig.API_KEY
        private val CLIENT_ID = BuildKonfig.spotify_client_id
        private val CLIENT_SECRET = BuildKonfig.spotify_client_secret

    }

    suspend fun getAccessToken(): String {
        val credentials = "$CLIENT_ID:$CLIENT_SECRET".encodeToByteArray().encodeBase64()

        val response: HttpResponse = httpClient.post(API_KEY_TOKEN) {
            headers {
                append(HttpHeaders.Authorization, "Basic $credentials")
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
            }
            setBody("grant_type=client_credentials")
        }


        println("responseRaw: ${response.bodyAsText()}")

        return response.body<SpotifyTokenResponse>().accessToken
    }

    suspend fun getTopTracks(token: String, artistId: String, market: String = "US"): List<SpotifyTrack> {
        val response: HttpResponse = httpClient.get("https://api.spotify.com/v1/artists/$artistId/top-tracks") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            url {
                parameters.append("market", market)
            }
        }

        return response.body<SpotifyTopTracksResponse>().tracks
    }
}
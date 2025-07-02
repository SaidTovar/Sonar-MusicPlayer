package org.tovars.sonar.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int
)

@Serializable
data class SpotifyTrack(
    val name: String
)

@Serializable
data class SpotifyTopTracksResponse(
    val tracks: List<SpotifyTrack>
)
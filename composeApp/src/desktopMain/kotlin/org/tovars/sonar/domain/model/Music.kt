package org.tovars.sonar.domain.model

data class Music(
    val id: Long,
    val name: String = "",
    val artist: String = "",
    val album: String = "",
    val genre: String = "",
    val year: Int = 0,
    val length: String = "",
    val url: String = "",
    val image: String = ""
)
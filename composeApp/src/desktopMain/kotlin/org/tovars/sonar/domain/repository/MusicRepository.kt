package org.tovars.sonar.domain.repository

import org.tovars.sonar.domain.model.Music

interface MusicRepository {

    suspend fun initConnection()

    suspend fun getListOfMusics(): List<String>

    suspend fun getMusic(id: Long): Music

    suspend fun searchMusics(query: String): List<Music>

}
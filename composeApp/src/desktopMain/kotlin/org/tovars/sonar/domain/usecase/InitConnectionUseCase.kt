package org.tovars.sonar.domain.usecase

import org.tovars.sonar.domain.repository.MusicRepository

class InitConnectionUseCase (
    private val repository: MusicRepository
){

    suspend operator fun invoke() {
        repository.initConnection()
    }

}
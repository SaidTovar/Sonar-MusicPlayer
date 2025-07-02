package org.tovars.sonar.core.di

import org.koin.core.annotation.Single
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module

@Single
class MyService

fun initKoin(config: KoinAppDeclaration? = null){

    startKoin {
        config?.invoke(this)
        modules(
            AppModule().module,
            MusicModule().module,
        )
    }

}


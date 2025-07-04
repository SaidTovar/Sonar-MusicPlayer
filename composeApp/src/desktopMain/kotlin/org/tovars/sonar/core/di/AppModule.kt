package org.tovars.sonar.core.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.tovars.sonar.core.data.HttpClientEngineFactory

//@ComponentScan
@Module
class AppModule {

    @Single
    fun httpClientEngine(): HttpClientEngine = HttpClientEngineFactory().getEngine()


    @Single
    fun httpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine){
        //json
        //install(ContentNegotiation) {
        //    json()
        //}
        install(ContentNegotiation) {

            json(Json {
                ignoreUnknownKeys = true
            })
        }

        // .
    }

}


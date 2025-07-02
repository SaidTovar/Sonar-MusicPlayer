package org.tovars.sonar.core.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

class HttpClientEngineFactory() {

    fun getEngine() : HttpClientEngine = OkHttp.create()

}
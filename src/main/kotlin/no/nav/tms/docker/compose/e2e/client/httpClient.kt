package no.nav.tms.docker.compose.e2e.client

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

internal fun buildHttpClient(jsonSerializer: KotlinxSerializer = KotlinxSerializer(json())): HttpClient {
    return HttpClient(Apache) {
        install(HttpCookies) {
            keepAllCookiesFromPreviousRequests()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
        install(JsonFeature) {
            serializer = jsonSerializer
        }
        install(HttpTimeout)
    }
}

private fun HttpCookies.Config.keepAllCookiesFromPreviousRequests() {
    storage = AcceptAllCookiesStorage()
}

internal suspend inline fun <reified T> HttpClient.get(url: URL): T = withContext(Dispatchers.IO) {
    request<T> {
        url(url)
        method = HttpMethod.Get
        expectSuccess = false
    }
}

package no.nav.personbruker.dittnav.e2e.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

fun buildHttpClient(): HttpClient {
    return HttpClient(Apache) {
        install(HttpCookies) {
            keepAllCookiesFromPreviousRequests()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }
}

private fun HttpCookies.Config.keepAllCookiesFromPreviousRequests() {
    storage = AcceptAllCookiesStorage()
}

suspend inline fun <reified T> HttpClient.get(url: URL): T = withContext(Dispatchers.IO) {
    request<T> {
        url(url)
        method = HttpMethod.Get
    }
}

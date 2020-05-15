package no.nav.personbruker.dittnav.e2e.client

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.e2e.config.DittNavDockerComposeCommonContext
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.slf4j.LoggerFactory
import java.net.URL

class RestClient(val httpClient: HttpClient) {

    val log = LoggerFactory.getLogger(RestClient::class.java)

    suspend fun get(service: ServiceConfiguration, pathToHit: String): String {
        val baseUrl = DittNavDockerComposeCommonContext.instance.getBaseUrl(service)
        val completeUrlToHit = URL("$baseUrl$pathToHit")
        return@withContext try {
            httpClient.get<T>(completeUrlToHit)

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klate ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    suspend inline fun <reified T> getWithToken(service: ServiceConfiguration, pathToHit: String, token: TokenInfo): T = withContext(Dispatchers.IO) {
        val baseUrl = DittNavDockerComposeCommonContext.instance.getBaseUrl(service)
        val completeUrlToHit = URL("$baseUrl$pathToHit")
        return@withContext try {
            httpClient.request<T> {
                url(completeUrlToHit)
                method = HttpMethod.Get
                header(HttpHeaders.Authorization, "Bearer ${token.id_token}")
            }

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klate ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

}

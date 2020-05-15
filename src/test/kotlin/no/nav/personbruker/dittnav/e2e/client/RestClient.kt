package no.nav.personbruker.dittnav.e2e.client

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.e2e.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.config.DittNavDockerComposeCommonContext
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.operations.ServiceOperation
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.slf4j.LoggerFactory
import java.net.URL

class RestClient(val httpClient: HttpClient) {

    val log = LoggerFactory.getLogger(RestClient::class.java)

    suspend inline fun <reified T> getWithoutAuth(service: ServiceConfiguration, operation: ServiceOperation): T = withContext(Dispatchers.IO) {
        val completeUrlToHit = constructPathToHit(service, operation)
        return@withContext try {
            httpClient.get<T>(completeUrlToHit)

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klate ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    suspend inline fun <reified T> get(service: ServiceConfiguration, operation: ServiceOperation, token: TokenInfo): T = withContext(Dispatchers.IO) {
        val completeUrlToHit = constructPathToHit(service, operation)
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

    suspend inline fun <reified T> postWithoutAuth(service: ServiceConfiguration, operation: ServiceOperation, data: ProduceBrukernotifikasjonDto): T {
        val completeUrlToHit = constructPathToHit(service, operation)
        return try {
            httpClient.post<T>() {
                url(completeUrlToHit)
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                body = data
            }

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klate ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    suspend inline fun <reified T> post(service: ServiceConfiguration, operation: ServiceOperation, data: ProduceBrukernotifikasjonDto, tokenInfo : TokenInfo): T {
        val completeUrlToHit = constructPathToHit(service, operation)
        return try {
            httpClient.post<T>() {
                url(completeUrlToHit)
                method = HttpMethod.Post
                header(HttpHeaders.Authorization, "Bearer ${tokenInfo.id_token}")
                contentType(ContentType.Application.Json)
                body = data
            }

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klate ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    fun constructPathToHit(service: ServiceConfiguration, operation: ServiceOperation): URL {
        val baseUrl = DittNavDockerComposeCommonContext.instance.getBaseUrl(service)
        return URL("$baseUrl${operation.path}")
    }

}

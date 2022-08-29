package no.nav.tms.docker.compose.e2e.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.docker.compose.e2e.config.DittNavDockerComposeCommonContext
import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration
import no.nav.tms.docker.compose.e2e.operations.ServiceOperation
import no.nav.tms.docker.compose.e2e.security.BearerToken
import org.slf4j.LoggerFactory
import java.net.URL

internal class RestClient(val httpClient: HttpClient) {

    val log = LoggerFactory.getLogger(no.nav.tms.docker.compose.e2e.client.RestClient::class.java)

    suspend inline fun <reified T> getWithoutAuth(service: ServiceConfiguration,
                                                  operation: ServiceOperation,
                                                  parameters: Map<String, String> = emptyMap()): T = withContext(Dispatchers.IO) {
        val completeUrlToHit = constructPathToHit(service, operation)
        return@withContext try {
            httpClient.get<T> {
                url(completeUrlToHit)
                parameters.forEach{ (key, value) ->
                    parameter(key, value)
                }
                expectSuccess = false
            }
        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klarte ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    suspend inline fun <reified T> get(service: ServiceConfiguration,
                                       operation: ServiceOperation,
                                       token: BearerToken,
                                       parameters: Map<String, String> = emptyMap()): T = withContext(Dispatchers.IO) {
        val completeUrlToHit = constructPathToHit(service, operation)
        return@withContext try {
            httpClient.request<T> {
                url(completeUrlToHit)
                method = HttpMethod.Get
                header(HttpHeaders.Authorization, token.toString())
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klarte ikke å gjennomføre et kall mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    suspend inline fun <reified T> postWithoutAuth(service: ServiceConfiguration, operation: ServiceOperation, data: no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO): T {
        val completeUrlToHit = constructPathToHit(service, operation)
        return try {
            httpClient.post {
                url(completeUrlToHit)
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                body = data
                expectSuccess = false
            }

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klarte ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    suspend inline fun <reified T> post(service: ServiceConfiguration, operation: ServiceOperation, data: no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO, token: BearerToken): T {
        val completeUrlToHit = constructPathToHit(service, operation)
        return try {
            httpClient.post {
                url(completeUrlToHit)
                method = HttpMethod.Post
                header(HttpHeaders.Authorization, token.toString())
                contentType(ContentType.Application.Json)
                body = data
            }

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klarte ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    fun constructPathToHit(service: ServiceConfiguration, operation: ServiceOperation): URL {
        val baseUrl = DittNavDockerComposeCommonContext.instance.getBaseUrl(service)
        return URL("$baseUrl${operation.path}")
    }

}

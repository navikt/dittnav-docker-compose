package no.nav.personbruker.dittnav.e2e.client

import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.e2e.config.DittNavDockerComposeCommonContext
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import org.slf4j.LoggerFactory
import java.net.URL

class RestClient(private val httpClient: HttpClient) {

    private val log = LoggerFactory.getLogger(RestClient::class.java)

    suspend fun get(service: ServiceConfiguration, pathToHit: String): String {
        val baseUrl = DittNavDockerComposeCommonContext.instance.getBaseUrl(service)
        val completeUrlToHit = URL("$baseUrl$pathToHit")
        return try {
            httpClient.get(completeUrlToHit)

        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot $service, klate ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

}
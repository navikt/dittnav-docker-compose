package no.nav.personbruker.dittnav.e2e

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.DittNavDockerComposeCommonContext
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.get
import org.amshove.kluent.`should contain some`
import org.junit.jupiter.api.Test
import java.net.URL

internal class EndToEndTest {

    private val environment = DittNavDockerComposeCommonContext.instance

    @Test
    fun `all DittNAV services should be alive`() {
        val client = HttpClient(Apache)

        ServiceConfiguration.personbrukerServices().forEach { service ->
            val url = environment.getBaseUrl(service)
            runBlocking {
                val urlToCheck = URL("$url${service.isAlivePath}")
                val response = client.get<String>(urlToCheck)
                println("Checking if $service is alive on \t'$urlToCheck', \tresponse: $response")
                response
            } `should contain some` (listOf("ALIVE", "UP", "OK"))
        }
    }

}

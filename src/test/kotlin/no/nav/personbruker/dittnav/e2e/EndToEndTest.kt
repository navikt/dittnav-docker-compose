package no.nav.personbruker.dittnav.e2e

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.config.get
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.AnyOf.anyOf
import org.junit.jupiter.api.Test
import java.net.URL

internal class EndToEndTest :
    UsesTheCommonDockerComposeContext() {

    @Test
    fun `all DittNAV services should be alive`() {
        val client = HttpClient(Apache)

        ServiceConfiguration.personbrukerServices().forEach { service ->
            val baseUrlForService = dockerComposeContext.getBaseUrl(service)
            val completeUrlToIsAlive = URL("$baseUrlForService${service.isAlivePath}")
            runBlocking {
                val response = client.get<String>(completeUrlToIsAlive)
                assertThat(
                    "isAlive failed for $service on $completeUrlToIsAlive",
                    response,
                    anyOf(
                        containsString("ALIVE"),
                        containsString("UP"),
                        containsString("OK")
                    )
                )
            }
        }
    }

}

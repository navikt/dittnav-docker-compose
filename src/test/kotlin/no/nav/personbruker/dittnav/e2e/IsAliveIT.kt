package no.nav.personbruker.dittnav.e2e

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ServiceOperation
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.AnyOf.anyOf
import org.junit.jupiter.api.Test

internal class IsAliveIT : UsesTheCommonDockerComposeContext() {

    @Test
    fun `Alle DittNAV sine servicer skal kjore`() {
        ServiceConfiguration.dittNavServices().forEach { service ->
            assertIsAliveForSingleService(service, "/internal/isAlive")
        }
    }

    @Test
    fun `Alle stotte servicer skal kjore`() {
        ServiceConfiguration.mockingServices().forEach { service ->
            assertIsAliveForSingleService(service, "/isAlive")
        }
    }

    @Test
    fun `Dekoratoren skal kjore`() {
        assertIsAliveForSingleService(ServiceConfiguration.DEKORATOREN, "/isAlive")
    }

    private fun assertIsAliveForSingleService(service: ServiceConfiguration, isAlivePath: String) = runBlocking {
        val response = client.getWithoutAuth<String>(service, StringToServiceOperationConverter(isAlivePath))
        assertThat(
                "isAlive failed for $service",
                response,
                anyOf(
                        containsString("ALIVE"),
                        containsString("UP"),
                        containsString("OK")
                )
        )
    }

    private data class StringToServiceOperationConverter(override val path: String) : ServiceOperation

}

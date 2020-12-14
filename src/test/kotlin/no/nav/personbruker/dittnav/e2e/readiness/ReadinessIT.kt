package no.nav.personbruker.dittnav.e2e.readiness

import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.*
import org.amshove.kluent.`should be equal to`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.AnyOf.anyOf
import org.junit.jupiter.api.Test

internal class ReadinessIT : UsesTheCommonDockerComposeContext() {

    @Test
    fun `Servicer skal tilby isAlive`() {
        assertIsAliveForSingleService(ServiceConfiguration.API, ApiOperations.IS_ALIVE)
        assertIsAliveForSingleService(ServiceConfiguration.LEGACY, LegacyOperations.IS_ALIVE)
        assertIsAliveForSingleService(ServiceConfiguration.HANDLER, HandlerOperations.IS_ALIVE)
        assertIsAliveForSingleService(ServiceConfiguration.AGGREGATOR, AggregatorOperations.IS_ALIVE)
        assertIsAliveForSingleService(ServiceConfiguration.PRODUCER, ProducerOperations.IS_ALIVE)
        assertIsAliveForSingleService(ServiceConfiguration.FRONTEND, FrontendOperations.IS_ALIVE)
        assertIsAliveForSingleService(ServiceConfiguration.TIDSLINJE, TidslinjeOperations.IS_ALIVE)
        assertIsAliveForSingleService(ServiceConfiguration.VARSELBESTILLER, VarselOperations.IS_ALIVE)
    }

    @Test
    fun `Servicer skal tilby isReady`() {
        assertIsReadyForSingleService(ServiceConfiguration.API, ApiOperations.IS_READY)
        assertIsReadyForSingleService(ServiceConfiguration.LEGACY, LegacyOperations.IS_READY)
        assertIsReadyForSingleService(ServiceConfiguration.HANDLER, HandlerOperations.IS_READY)
        assertIsReadyForSingleService(ServiceConfiguration.AGGREGATOR, AggregatorOperations.IS_READY)
        assertIsReadyForSingleService(ServiceConfiguration.PRODUCER, ProducerOperations.IS_READY)
        assertIsReadyForSingleService(ServiceConfiguration.FRONTEND, FrontendOperations.IS_READY)
        assertIsReadyForSingleService(ServiceConfiguration.TIDSLINJE, TidslinjeOperations.IS_READY)
        assertIsReadyForSingleService(ServiceConfiguration.VARSELBESTILLER, VarselOperations.IS_READY)

    }

    @Test
    fun `Servicer skal tilby selftest`() {
        assertSelftestForSingleService(ServiceConfiguration.API, ApiOperations.SELFTEST)
        assertSelftestForSingleService(ServiceConfiguration.LEGACY, LegacyOperations.SELFTEST)
        assertSelftestForSingleService(ServiceConfiguration.HANDLER, HandlerOperations.SELFTEST)
        assertSelftestForSingleService(ServiceConfiguration.AGGREGATOR, AggregatorOperations.SELFTEST)
        assertSelftestForSingleService(ServiceConfiguration.FRONTEND, FrontendOperations.SELFTEST)
        assertSelftestForSingleService(ServiceConfiguration.TIDSLINJE, TidslinjeOperations.SELFTEST)
        assertSelftestForSingleService(ServiceConfiguration.VARSELBESTILLER, VarselOperations.SELFTEST)
    }

    @Test
    fun `Servicer skal tilby metrikker`() {
        assertMetricsForSingleService(ServiceConfiguration.API, ApiOperations.METRICS)
        assertMetricsForSingleService(ServiceConfiguration.LEGACY, LegacyOperations.METRICS)
        assertMetricsForSingleService(ServiceConfiguration.HANDLER, HandlerOperations.METRICS)
        assertMetricsForSingleService(ServiceConfiguration.AGGREGATOR, AggregatorOperations.METRICS)
        assertMetricsForSingleService(ServiceConfiguration.FRONTEND, FrontendOperations.METRICS)
        assertMetricsForSingleService(ServiceConfiguration.TIDSLINJE, TidslinjeOperations.METRICS)
        assertMetricsForSingleService(ServiceConfiguration.VARSELBESTILLER, TidslinjeOperations.SELFTEST)
    }

    @Test
    fun `Mocker og dekorator skal kjore`() {
        assertIsAliveForSingleService(ServiceConfiguration.DEKORATOREN, StringToServiceOperationConverter("/isAlive"))
        assertIsAliveForSingleService(ServiceConfiguration.MOCKS, StringToServiceOperationConverter("/isAlive"))
    }

    private fun assertIsAliveForSingleService(service: ServiceConfiguration, operation: ServiceOperation) = runBlocking {
        val response = client.getWithoutAuth<String>(service, operation)
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

    private fun assertIsReadyForSingleService(service: ServiceConfiguration, operation: ServiceOperation) = runBlocking {
        val response = client.getWithoutAuth<String>(service, operation)
        assertThat(
                "isReady failed for $service",
                response,
                anyOf(
                        containsString("READY"),
                        containsString("UP"),
                        containsString("OK")
                )
        )
    }

    private fun assertSelftestForSingleService(service: ServiceConfiguration, operation: ServiceOperation) = runBlocking {
        val response = client.getWithoutAuth<HttpResponse>(service, operation)
        response.status `should be equal to` OK
    }

    private fun assertMetricsForSingleService(service: ServiceConfiguration, operation: ServiceOperation) = runBlocking {
        val response = client.getWithoutAuth<HttpResponse>(service, operation)
        response.status `should be equal to` OK
    }

    private data class StringToServiceOperationConverter(override val path: String) : ServiceOperation

}

package no.nav.personbruker.dittnav.e2e.security

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.PartialContent
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.beskjed.ProduceBeskjedDTO
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.*
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be in`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SecurityIT : UsesTheCommonDockerComposeContext() {

    private lateinit var tokenAtLevel3: TokenInfo
    private lateinit var tokenAtLevel4: TokenInfo

    @BeforeEach
    fun `hent token`() {
        tokenAtLevel3 = tokenFetcher.fetchTokenForIdent("000", 3)
        tokenAtLevel4 = tokenFetcher.fetchTokenForIdent("000", 4)
    }

    @Test
    fun `Api skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val api = ServiceConfiguration.API
        val operation = ApiOperations.FETCH_BESKJED
        runBlocking {
            assertThatTheRequestWasDenied(api, operation)
            assertThatTheRequestWasAccepted(api, operation, tokenAtLevel3, definitionOfAccepted = listOf(OK, PartialContent))
            assertThatTheRequestWasAccepted(api, operation, tokenAtLevel4, definitionOfAccepted = listOf(OK, PartialContent))
        }
    }

    @Test
    fun `Handler skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val handler = ServiceConfiguration.HANDLER
        val operation = HandlerOperations.FETCH_BESKJED
        runBlocking {
            assertThatTheRequestWasDenied(handler, operation)
            assertThatTheRequestWasAccepted(handler, operation, tokenAtLevel3)
            assertThatTheRequestWasAccepted(handler, operation, tokenAtLevel4)
        }
    }

    @Test
    fun `Legacy skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val legacy = ServiceConfiguration.LEGACY
        val operation = LegacyOperations.MELDEKORT_INFO
        runBlocking {
            assertThatTheRequestWasDenied(legacy, operation)
            assertThatTheRequestWasAccepted(legacy, operation, tokenAtLevel3)
            assertThatTheRequestWasAccepted(legacy, operation, tokenAtLevel4)
        }
    }

    @Test
    fun `Tidslinje skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val tidslinje = ServiceConfiguration.TIDSLINJE
        val operation = TidslinjeOperations.TIDSLINJE
        val getParameters = mapOf("grupperingsid" to "1234", "produsent" to "produsent")
        val systembrukerHeader = mapOf("systembruker" to "produsent")
        runBlocking {
            assertThatTheRequestWasDenied(tidslinje, operation, headers = systembrukerHeader)
            assertThatTheRequestWasAccepted(tidslinje, operation, tokenAtLevel3, getParameters, headers = systembrukerHeader)
            assertThatTheRequestWasAccepted(tidslinje, operation, tokenAtLevel4, getParameters, headers = systembrukerHeader)
        }
    }

    @Test
    fun `Producer skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val data = ProduceBeskjedDTO("Sjekker sikkherhet for producer", "grupperingsid")
        val producer = ServiceConfiguration.PRODUCER
        val operation = ProducerOperations.PRODUCE_BESKJED
        runBlocking {
            val unauthResponse = client.postWithoutAuth<HttpResponse>(producer, operation, data)
            printServiceLogIfNotExpectedResult(producer, unauthResponse, Unauthorized)
            unauthResponse.status `should be equal to` Unauthorized

            val authResponse3 = client.post<HttpResponse>(producer, operation, data, tokenAtLevel3)
            printServiceLogIfNotExpectedResult(producer, authResponse3, OK)
            authResponse3.status `should be equal to` OK

            val authResponse4 = client.post<HttpResponse>(producer, operation, data, tokenAtLevel4)
            printServiceLogIfNotExpectedResult(producer, authResponse4, OK)
            authResponse4.status `should be equal to` OK
        }
    }

    private suspend fun assertThatTheRequestWasDenied(service: ServiceConfiguration,
                                                      operation: ServiceOperation,
                                                      headers: Map<String, String> = emptyMap()) {
        val unauthResponse = client.getWithoutAuth<HttpResponse>(service, operation, headers)
        printServiceLogIfNotExpectedResult(service, unauthResponse, Unauthorized)
        unauthResponse.status `should be equal to` Unauthorized
    }

    private suspend fun assertThatTheRequestWasAccepted(service: ServiceConfiguration,
                                                        operation: ServiceOperation,
                                                        tokenInfo: TokenInfo,
                                                        parameters: Map<String, String> = emptyMap(),
                                                        definitionOfAccepted: List<HttpStatusCode> = listOf(OK),
                                                        headers: Map<String, String> = emptyMap()) {
        val authResponse = client.get<HttpResponse>(service, operation, tokenInfo, parameters, headers)


        printServiceLogIfNotInExpectedResults(service, authResponse, definitionOfAccepted)
        authResponse.status `should be in` definitionOfAccepted
    }

    private fun printServiceLogIfNotExpectedResult(service: ServiceConfiguration, actualResponse: HttpResponse, expectedResponse: HttpStatusCode) {
        if (actualResponse.status != expectedResponse) {
            println("Container log for the service $service:\n ${dockerComposeContext.getLogsFor(service)}")
        }
    }

    private fun printServiceLogIfNotInExpectedResults(service: ServiceConfiguration, actualResponse: HttpResponse, expectedResponse: List<HttpStatusCode>) {
        if (actualResponse.status !in expectedResponse) {
            println("Container log for the service $service:\n ${dockerComposeContext.getLogsFor(service)}")
        }
    }
}

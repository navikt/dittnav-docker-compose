package no.nav.personbruker.dittnav.e2e.security

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.*
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class SecurityIT : UsesTheCommonDockerComposeContext() {

    private val tokenAtLevel3 = TokenFetcher.fetchTokenForIdent("000", 3)
    private val tokenAtLevel4 = TokenFetcher.fetchTokenForIdent("000", 4)

    @Test
    fun `Api skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val api = ServiceConfiguration.API
        val operation = ApiOperations.FETCH_BESKJED
        runBlocking {
            assertThatTheRequestWasDenied(api, operation)
            assertThatTheRequestWasAccepted(api, operation, tokenAtLevel3)
            assertThatTheRequestWasAccepted(api, operation, tokenAtLevel4)
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
    fun `Producer skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val data = ProduceBrukernotifikasjonDto("Sjekker sikkherhet for producer")
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

    private suspend fun assertThatTheRequestWasDenied(service: ServiceConfiguration, operation: ServiceOperation) {
        val unauthResponse = client.getWithoutAuth<HttpResponse>(service, operation)
        printServiceLogIfNotExpectedResult(service, unauthResponse, Unauthorized)
        unauthResponse.status `should be equal to` Unauthorized
    }

    private suspend fun assertThatTheRequestWasAccepted(service: ServiceConfiguration, operation: ServiceOperation, tokenInfo: TokenInfo) {
        val authResponse = client.get<HttpResponse>(service, operation, tokenInfo)
        printServiceLogIfNotExpectedResult(service, authResponse, OK)
        authResponse.status `should be equal to` OK
    }

    private fun printServiceLogIfNotExpectedResult(service: ServiceConfiguration, actualResponse: HttpResponse, expectedResponse: HttpStatusCode) {
        if (actualResponse.status != expectedResponse) {
            println("Container log for the service $service:\n ${dockerComposeContext.getLogsFor(service)}")
        }
    }
}

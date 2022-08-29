package no.nav.tms.docker.compose.e2e.security

import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.PartialContent
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import kotlinx.coroutines.runBlocking
import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration
import no.nav.tms.docker.compose.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.tms.docker.compose.e2e.debugging.ApiContainerLogs
import no.nav.tms.docker.compose.e2e.debugging.AuthMockContainerLogs
import no.nav.tms.docker.compose.e2e.debugging.HandlerContainerLogs
import no.nav.tms.docker.compose.e2e.debugging.ProducerContainerLogs
import no.nav.tms.docker.compose.e2e.operations.ApiOperations
import no.nav.tms.docker.compose.e2e.operations.HandlerOperations
import no.nav.tms.docker.compose.e2e.operations.ProducerOperations
import no.nav.tms.docker.compose.e2e.operations.ServiceOperation
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be in`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    ApiContainerLogs::class,
    HandlerContainerLogs::class,
    ProducerContainerLogs::class,
    AuthMockContainerLogs::class
)
internal class SecurityIT : UsesTheCommonDockerComposeContext() {

    private lateinit var tokenAtLevel3: TokenInfo
    private lateinit var tokenAtLevel4: TokenInfo


    @BeforeEach
    fun `hent token`() {
        tokenAtLevel3 = tokenFetcher.fetchTokenForIdent("12345678901", 3)
        tokenAtLevel4 = tokenFetcher.fetchTokenForIdent("23456789012", 4)
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
            val exchangedTokenAtLevel3 = tokenFetcher.exchangeToken(dittnavEventHandlerClientId, dittnavEventHandlerClientId, tokenAtLevel3)
            val exchangedTokenAtLevel4 = tokenFetcher.exchangeToken(dittnavEventHandlerClientId, dittnavEventHandlerClientId, tokenAtLevel4)
            assertThatTheRequestWasDenied(handler, operation)
            assertThatTheRequestWasAccepted(handler, operation, exchangedTokenAtLevel3, definitionOfAccepted = listOf(OK))
            assertThatTheRequestWasAccepted(handler, operation, exchangedTokenAtLevel4)
        }
    }

    @Test
    fun `Producer skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        val data = no.nav.tms.docker.compose.e2e.beskjed.ProduceBeskjedDTO(tekst = "Sjekker sikkherhet for producer")
        val producer = ServiceConfiguration.PRODUCER
        val operation = ProducerOperations.PRODUCE_BESKJED
        runBlocking {
            val unauthResponse = client.postWithoutAuth<HttpResponse>(producer, operation, data)
            unauthResponse.status `should be equal to` Unauthorized

            val authResponse3 = client.post<HttpResponse>(producer, operation, data, BearerToken(tokenAtLevel3.id_token))
            authResponse3.status `should be equal to` OK

            val authResponse4 = client.post<HttpResponse>(producer, operation, data, BearerToken(tokenAtLevel4.id_token))
            authResponse4.status `should be equal to` OK
        }
    }

    private suspend fun assertThatTheRequestWasDenied(service: ServiceConfiguration, operation: ServiceOperation) {
        val unauthResponse = client.getWithoutAuth<HttpResponse>(service, operation)
        unauthResponse.status `should be equal to` Unauthorized
    }

    private suspend fun assertThatTheRequestWasAccepted(service: ServiceConfiguration,
                                                        operation: ServiceOperation,
                                                        tokenInfo: TokenInfo,
                                                        parameters: Map<String, String> = emptyMap(),
                                                        definitionOfAccepted: List<HttpStatusCode> = listOf(OK)) {
        val authResponse = client.get<HttpResponse>(service, operation, BearerToken(tokenInfo.id_token), parameters)
        authResponse.status `should be in` definitionOfAccepted
    }

    private suspend fun assertThatTheRequestWasAccepted(service: ServiceConfiguration,
                                                        operation: ServiceOperation,
                                                        tokenXToken: TokenXToken,
                                                        parameters: Map<String, String> = emptyMap(),
                                                        definitionOfAccepted: List<HttpStatusCode> = listOf(OK)) {
        val authResponse = client.get<HttpResponse>(service, operation, BearerToken(tokenXToken.accessToken), parameters)
        authResponse.status `should be in` definitionOfAccepted
    }
}

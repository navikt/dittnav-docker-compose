package no.nav.personbruker.dittnav.e2e

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.*
import no.nav.personbruker.dittnav.e2e.security.TokenFetcher
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class SecurityIT : UsesTheCommonDockerComposeContext() {

    private val tokenAtLevel3 = TokenFetcher.fetchTokenForIdent("000", 3)
    private val tokenAtLevel4 = TokenFetcher.fetchTokenForIdent("000", 4)

    @Test
    fun `Api skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        runBlocking {
            assertThatTheRequestIsDenied(ServiceConfiguration.API, ApiOperations.FETCH_BESKJED)
            assertThatTheRequestWasAccepted(ServiceConfiguration.API, ApiOperations.FETCH_BESKJED, tokenAtLevel3)
            assertThatTheRequestWasAccepted(ServiceConfiguration.API, ApiOperations.FETCH_BESKJED, tokenAtLevel4)
        }
    }

    @Test
    fun `Handler skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        runBlocking {
            assertThatTheRequestIsDenied(ServiceConfiguration.HANDLER, HandlerOperations.FETCH_BESKJED)
            assertThatTheRequestWasAccepted(ServiceConfiguration.HANDLER, HandlerOperations.FETCH_BESKJED, tokenAtLevel3)
            assertThatTheRequestWasAccepted(ServiceConfiguration.HANDLER, HandlerOperations.FETCH_BESKJED, tokenAtLevel4)
        }
    }

    @Test
    fun `Legacy skal ha sikkerhet aktivert, og akseptere innlogging fra baade nivaa 3 og 4`() {
        runBlocking {
            assertThatTheRequestIsDenied(ServiceConfiguration.LEGACY, LegacyOperations.MELDEKORT_INFO)
            assertThatTheRequestWasAccepted(ServiceConfiguration.LEGACY, LegacyOperations.MELDEKORT_INFO, tokenAtLevel3)
            assertThatTheRequestWasAccepted(ServiceConfiguration.LEGACY, LegacyOperations.MELDEKORT_INFO, tokenAtLevel4)
        }
    }

    private suspend fun assertThatTheRequestIsDenied(service: ServiceConfiguration, operation: ServiceOperation) {
        val unauthResponse = client.get<HttpResponse>(service, operation.path)
        if (unauthResponse.status != Unauthorized) {
            println("Container log for the service $service:\n" + dockerComposeContext.getLogsFor(service))
        }
        unauthResponse.status `should be equal to` Unauthorized
    }

    private suspend fun assertThatTheRequestWasAccepted(service: ServiceConfiguration, operation: ServiceOperation, tokenInfo: TokenInfo) {
        val authResponse = client.getWithToken<HttpResponse>(service, operation.path, tokenInfo)
        if (authResponse.status != OK) {
            println("Container log for the service $service:\n" + dockerComposeContext.getLogsFor(service))
        }
        authResponse.status `should be equal to` OK
    }

}

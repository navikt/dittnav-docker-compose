package no.nav.personbruker.dittnav.e2e.innboks

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.security.TokenFetcher
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.junit.jupiter.api.Test

class InnboksIT : UsesTheCommonDockerComposeContext() {

    @Test
    fun `Skal produsere innboks-event paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Innboks-event 1"
        val tokenAt3 = TokenFetcher.fetchTokenForIdent("b1", 3)
        val originalInnboks = ProduceBrukernotifikasjonDto(expectedText)
        `produce innboks-event at level`(originalInnboks, tokenAt3)
        `wait for events to be processed`()
        `verify that the innboks-event is active`(tokenAt3, expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere innboks-event paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Innboks-event 2"
        val tokenAt4 = TokenFetcher.fetchTokenForIdent("b2", expectedSikkerhetsnivaa)
        val originalInnboks = ProduceBrukernotifikasjonDto(expectedText)
        `produce innboks-event at level`(originalInnboks, tokenAt4)
        `wait for events to be processed`()
        `verify that the innboks-event is active`(tokenAt4, expectedSikkerhetsnivaa, expectedText)
    }

    private fun `produce innboks-event at level`(originalInnboks: ProduceBrukernotifikasjonDto, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_INNBOKS, originalInnboks, token)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `wait for events to be processed`() {
        runBlocking {
            delay(100)
        }
    }

    private fun `verify that the innboks-event is active`(token: TokenInfo, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            var response = client.get<List<InnboksDTO>>(ServiceConfiguration.API, ApiOperations.FETCH_INNBOKS, token)
            response.`should not be empty`()
            val firstInnboksEvent = response[0]
            firstInnboksEvent.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            firstInnboksEvent.tekst `should be equal to` expectedText
        }
    }
}

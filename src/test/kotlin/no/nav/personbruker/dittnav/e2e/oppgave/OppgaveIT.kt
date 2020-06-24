package no.nav.personbruker.dittnav.e2e.oppgave

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

internal class OppgaveIT : UsesTheCommonDockerComposeContext() {

    @Test
    fun `Skal produsere oppgaver paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Oppgave 1"
        val tokenAt3 = TokenFetcher.fetchTokenForIdent("b1", expectedSikkerhetsnivaa)
        val originalOppgave = ProduceBrukernotifikasjonDto(expectedText)

        `produce oppgave at level`(originalOppgave, tokenAt3)
        `wait for events to be processed`()
        `verify that the oppgave is active`(tokenAt3, expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere oppgaver paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Oppgave 2"
        val tokenAt4 = TokenFetcher.fetchTokenForIdent("b2", expectedSikkerhetsnivaa)
        val originalOppgave = ProduceBrukernotifikasjonDto(expectedText)

        `produce oppgave at level`(originalOppgave, tokenAt4)
        `wait for events to be processed`()
        `verify that the oppgave is active`(tokenAt4, expectedSikkerhetsnivaa, expectedText)
    }

    private fun `produce oppgave at level`(originalOppgave: ProduceBrukernotifikasjonDto, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_OPPGAVE, originalOppgave, token)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `wait for events to be processed`() {
        runBlocking {
            delay(100)
        }
    }

    private fun `verify that the oppgave is active`(token: TokenInfo, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            var response = client.get<List<OppgaveDTO>>(ServiceConfiguration.API, ApiOperations.FETCH_OPPGAVE, token)
            response.`should not be empty`()
            val firstOppgave = response[0]
            firstOppgave.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            firstOppgave.tekst `should be equal to` expectedText
        }
    }
}

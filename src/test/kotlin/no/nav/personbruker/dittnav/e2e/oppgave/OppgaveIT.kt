package no.nav.personbruker.dittnav.e2e.oppgave

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.operations.VarselOperations
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class OppgaveIT : UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere oppgaver paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Oppgave 1"
        val tokenAt3 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalOppgave = ProduceBrukernotifikasjonDto(expectedText)

        `produce oppgave at level`(originalOppgave, tokenAt3)
        `wait for events to be processed`()
        val activeOppgaver = `get events`(tokenAt3, ApiOperations.FETCH_OPPGAVE)
        `verify oppgave`(activeOppgaver[0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere oppgaver paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Oppgave 2"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalOppgave = ProduceBrukernotifikasjonDto(expectedText)

        `produce oppgave at level`(originalOppgave, tokenAt4)
        `wait for events to be processed`()
        val activeOppgave = `get events`(tokenAt4, ApiOperations.FETCH_OPPGAVE)
        `verify oppgave`(activeOppgave[0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal bestille ekstern varsling for oppgaver`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val originalOppgave1 = ProduceBrukernotifikasjonDto("Oppgave med varsel 1", eksternVarsling = true)
        val originalOppgave2 = ProduceBrukernotifikasjonDto("Oppgave med varsel 2", eksternVarsling = true)
        val originalOppgave3 = ProduceBrukernotifikasjonDto("Oppgave uten varsel 1", eksternVarsling = false)
        `produce oppgave at level`(originalOppgave1, tokenAt4)
        `produce oppgave at level`(originalOppgave2, tokenAt4)
        `produce oppgave at level`(originalOppgave3, tokenAt4)
        var doknotifikasjonCount = 0
        var countAttempts = 0
        runBlocking {
            while(doknotifikasjonCount == 0 && countAttempts < 10) {
                doknotifikasjonCount = `get doknotifikasjon count`()
                countAttempts++
                delay(1000)
            }
        }
        doknotifikasjonCount `should be equal to` 2
    }

    private fun `produce oppgave at level`(originalOppgave: ProduceBrukernotifikasjonDto, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_OPPGAVE, originalOppgave, token)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `verify oppgave`(oppgave: OppgaveDTO, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            oppgave.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            oppgave.tekst `should be equal to` expectedText
        }
    }

    private fun `get events`(token: TokenInfo, operation: ApiOperations): List<OppgaveDTO> {
        return runBlocking {
            val response = client.get<List<OppgaveDTO>>(ServiceConfiguration.API, operation, token)
            response
        }
    }

    private suspend fun `get doknotifikasjon count`(): Int {
        return client.getWithoutAuth(ServiceConfiguration.MOCKS, VarselOperations.COUNT_DOKNOTIFIKASJON)
    }
}

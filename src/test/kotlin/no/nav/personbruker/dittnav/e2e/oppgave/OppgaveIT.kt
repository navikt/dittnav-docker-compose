package no.nav.personbruker.dittnav.e2e.oppgave

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.doknotifikasjon.DoknotifikasjonDTO
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.operations.VarselOperations
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain all`
import org.junit.jupiter.api.Test

internal class OppgaveIT : UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere oppgaver paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Oppgave 1"
        val tokenAt3 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalOppgave = ProduceOppgaveDTO(expectedText)

        `produce oppgave at level`(originalOppgave, tokenAt3)
        val activeOppgaver = `wait for events` {
            `get events`(tokenAt3, ApiOperations.FETCH_OPPGAVE)
        }
        `verify oppgave`(activeOppgaver!![0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere oppgaver paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Oppgave 2"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalOppgave = ProduceOppgaveDTO(expectedText)

        `produce oppgave at level`(originalOppgave, tokenAt4)
        val activeOppgave = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_OPPGAVE)
        }
        `verify oppgave`(activeOppgave!![0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal bestille ekstern varsling for oppgaver`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val originalOppgave1 = ProduceOppgaveDTO("Oppgave med varsel 1", eksternVarsling = true)
        val originalOppgave2 = ProduceOppgaveDTO("Oppgave med varsel 2", eksternVarsling = true)
        `produce oppgave at level`(originalOppgave1, tokenAt4)
        `produce oppgave at level`(originalOppgave2, tokenAt4)
        val activeOppgave = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_OPPGAVE)
        }

        val doknotifikasjonerToMatch = listOf(
                DoknotifikasjonDTO("O-username-${activeOppgave!![0].eventId}"),
                DoknotifikasjonDTO("O-username-${activeOppgave[1].eventId}")
        )

        val doknotifikasjoner = `wait for values to be returned`(doknotifikasjonerToMatch) {
            `get doknotifikasjoner`(VarselOperations.GET_DOKNOTIFIKASJON_OPPGAVE)
        }

        doknotifikasjoner!! `should contain all` doknotifikasjonerToMatch
    }

    private fun `produce oppgave at level`(originalOppgave: ProduceOppgaveDTO, token: TokenInfo) {
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

    private fun `get doknotifikasjoner`(operation: VarselOperations): List<DoknotifikasjonDTO> {
        return runBlocking {
            val response = client.getWithoutAuth<List<DoknotifikasjonDTO>>(ServiceConfiguration.MOCKS, operation)
            response
        }
    }
}

package no.nav.personbruker.dittnav.e2e.done

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.beskjed.BeskjedDTO
import no.nav.personbruker.dittnav.e2e.client.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.client.ProduceDoneDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.innboks.InnboksDTO
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.oppgave.OppgaveDTO
import no.nav.personbruker.dittnav.e2e.security.TokenFetcher
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should not be empty`
import org.junit.jupiter.api.Test

class DoneIT: UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere done-eventer for alle brukernotifikasjoner`() {
        val tokenAt4 = TokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        `produser brukernotifikasjon`(tokenAt4, "Oppgave 1", ProducerOperations.PRODUCE_OPPGAVE)
        `produser brukernotifikasjon`(tokenAt4, "Beskjed 1", ProducerOperations.PRODUCE_BESKJED)
        `produser brukernotifikasjon`(tokenAt4, "Innboks 1", ProducerOperations.PRODUCE_INNBOKS)
        `wait for events to be processed`(2000)

        `produser done-eventer for alle brukernotifikasjoner`(tokenAt4)
        `wait for events to be processed`()
        `verify no active oppgave-events`(tokenAt4)
        `verify no active beskjed-events`(tokenAt4)
        `verify no active innboks-events`(tokenAt4)
    }

    private fun `verify no active oppgave-events`(token: TokenInfo) {
        val activeOppgaveEvents: List<OppgaveDTO> = `get events`(token, ApiOperations.FETCH_OPPGAVE)
        activeOppgaveEvents.`should be empty`()
        val inactiveOppgaveEvents: List<OppgaveDTO> = `get events`(token, ApiOperations.FETCH_OPPGAVE_INACTIVE)
        inactiveOppgaveEvents.`should not be empty`()
    }

    private fun `verify no active beskjed-events`(token: TokenInfo) {
        val activeBeskjedEvents: List<BeskjedDTO> = `get events`(token, ApiOperations.FETCH_BESKJED)
        activeBeskjedEvents.`should be empty`()
        val inactiveBeskjedEvents: List<BeskjedDTO> = `get events`(token, ApiOperations.FETCH_BESKJED_INACTIVE)
        inactiveBeskjedEvents.`should not be empty`()
    }

    private fun `verify no active innboks-events`(token: TokenInfo) {
        val activeInnboksEvents: List<InnboksDTO> = `get events`(token, ApiOperations.FETCH_INNBOKS)
        activeInnboksEvents.`should be empty`()
        val inactiveInnboksEvents: List<InnboksDTO> = `get events`(token, ApiOperations.FETCH_INNBOKS_INACTIVE)
        inactiveInnboksEvents.`should not be empty`()
    }

    private fun `produser brukernotifikasjon`(token: TokenInfo, text: String, producerOperation: ProducerOperations) {
        val brukernotifikasjon = ProduceBrukernotifikasjonDto(text)
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, producerOperation, brukernotifikasjon, token)
        }
    }

    private fun `produser done-eventer for alle brukernotifikasjoner`(token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_DONE_ALL, ProduceDoneDto(), token)
        }
    }

    private inline fun <reified T> `get events`(token: TokenInfo, apiOperation: ApiOperations): T {
        return runBlocking {
            var response = client.get<T>(ServiceConfiguration.API, apiOperation, token)
            response
        }
    }
}

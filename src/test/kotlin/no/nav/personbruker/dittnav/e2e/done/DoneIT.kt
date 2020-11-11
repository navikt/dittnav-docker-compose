package no.nav.personbruker.dittnav.e2e.done

import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.beskjed.BeskjedDTO
import no.nav.personbruker.dittnav.e2e.beskjed.ProduceBeskjedDTO
import no.nav.personbruker.dittnav.e2e.client.ProduceDTO
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.innboks.InnboksDTO
import no.nav.personbruker.dittnav.e2e.innboks.ProduceInnboksDTO
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.oppgave.OppgaveDTO
import no.nav.personbruker.dittnav.e2e.oppgave.ProduceOppgaveDTO
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should not be empty`
import org.junit.jupiter.api.Test

class DoneIT: UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere done-eventer for alle brukernotifikasjoner`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val beskjed = ProduceBeskjedDTO(tekst = "Beskjed 1")
        val oppgave = ProduceOppgaveDTO(tekst = "Oppgave 1")
        val innboks = ProduceInnboksDTO(tekst = "Innboks 1")

        `produser brukernotifikasjon`(tokenAt4, beskjed, ProducerOperations.PRODUCE_BESKJED)
        `produser brukernotifikasjon`(tokenAt4, oppgave, ProducerOperations.PRODUCE_OPPGAVE)
        `produser brukernotifikasjon`(tokenAt4, innboks, ProducerOperations.PRODUCE_INNBOKS)

        val activeOppgaveEvents: List<OppgaveDTO>? = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_OPPGAVE)
        }
        activeOppgaveEvents!!.`should not be empty`()

        `produser done-eventer for alle brukernotifikasjoner`(tokenAt4)

        `verify no active oppgave-events`(tokenAt4)
        `verify no active beskjed-events`(tokenAt4)
        `verify no active innboks-events`(tokenAt4)
    }

    private fun `verify no active oppgave-events`(token: TokenInfo) {
        val inactiveOppgaveEvents: List<OppgaveDTO>? = `wait for events` { `get events`(token, ApiOperations.FETCH_OPPGAVE_INACTIVE) }
        inactiveOppgaveEvents!!.`should not be empty`()

        val activeOppgaveEvents: List<OppgaveDTO> = `get events`(token, ApiOperations.FETCH_OPPGAVE)
        activeOppgaveEvents.`should be empty`()
    }

    private fun `verify no active beskjed-events`(token: TokenInfo) {
        val inactiveBeskjedEvents: List<BeskjedDTO>? = `wait for events` { `get events`(token, ApiOperations.FETCH_BESKJED_INACTIVE) }
        inactiveBeskjedEvents!!.`should not be empty`()

        val activeBeskjedEvents: List<BeskjedDTO> = `get events`(token, ApiOperations.FETCH_BESKJED)
        activeBeskjedEvents.`should be empty`()
    }

    private fun `verify no active innboks-events`(token: TokenInfo) {
        val inactiveInnboksEvents: List<InnboksDTO>? = `wait for events` { `get events`(token, ApiOperations.FETCH_INNBOKS_INACTIVE) }
        inactiveInnboksEvents!!.`should not be empty`()

        val activeInnboksEvents: List<InnboksDTO> = `get events`(token, ApiOperations.FETCH_INNBOKS)
        activeInnboksEvents.`should be empty`()
    }

    private fun `produser brukernotifikasjon`(token: TokenInfo, brukernotifikasjon: ProduceDTO, producerOperation: ProducerOperations) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, producerOperation, brukernotifikasjon, token)
        }
    }

    private fun `produser done-eventer for alle brukernotifikasjoner`(token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_DONE_ALL, ProduceDoneDTO(), token)
        }
    }

    private inline fun <reified T> `get events`(token: TokenInfo, apiOperation: ApiOperations): T {
        return runBlocking {
            val response = client.get<T>(ServiceConfiguration.API, apiOperation, token)
            response
        }
    }
}

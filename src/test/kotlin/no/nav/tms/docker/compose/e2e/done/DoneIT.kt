package no.nav.tms.docker.compose.e2e.done

import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration
import no.nav.tms.docker.compose.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.tms.docker.compose.e2e.debugging.*
import no.nav.tms.docker.compose.e2e.doknotifikasjonStopp.DoknotifikasjonStoppDTO
import no.nav.tms.docker.compose.e2e.innboks.InnboksDTO
import no.nav.tms.docker.compose.e2e.innboks.ProduceInnboksDTO
import no.nav.tms.docker.compose.e2e.operations.ApiOperations
import no.nav.tms.docker.compose.e2e.operations.ProducerOperations
import no.nav.tms.docker.compose.e2e.operations.VarselOperations
import no.nav.tms.docker.compose.e2e.oppgave.OppgaveDTO
import no.nav.tms.docker.compose.e2e.oppgave.ProduceOppgaveDTO
import no.nav.tms.docker.compose.e2e.security.BearerToken
import no.nav.tms.docker.compose.e2e.security.TokenInfo
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should contain all`
import org.amshove.kluent.`should not be empty`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    MocksContainerLogs::class,
    ApiContainerLogs::class,
    ProducerContainerLogs::class,
    HandlerContainerLogs::class,
    VarselbestillerContainerLogs::class
)
internal class DoneIT: UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere done-eventer for alle brukernotifikasjoner`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val beskjed = no.nav.tms.docker.compose.e2e.beskjed.ProduceBeskjedDTO(tekst = "Beskjed 1")
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

    @Test
    fun `Skal avbestille ekstern varsling for brukernotifikasjon ved mottak av done-event`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val beskjed1 = no.nav.tms.docker.compose.e2e.beskjed.ProduceBeskjedDTO(tekst = "Beskjed 1", eksternVarsling = true)
        val beskjed2 = no.nav.tms.docker.compose.e2e.beskjed.ProduceBeskjedDTO(tekst = "Beskjed 2", eksternVarsling = true)
        val oppgave1 = ProduceOppgaveDTO(tekst = "Oppgave 1", eksternVarsling = true)
        val oppgave2 = ProduceOppgaveDTO(tekst = "Oppgave 2", eksternVarsling = true)
        val innboks1 = ProduceOppgaveDTO(tekst = "Oppgave 1", eksternVarsling = true)
        val innboks2 = ProduceOppgaveDTO(tekst = "Oppgave 2", eksternVarsling = true)

        `produser brukernotifikasjon`(tokenAt4, beskjed1, ProducerOperations.PRODUCE_BESKJED)
        `produser brukernotifikasjon`(tokenAt4, beskjed2, ProducerOperations.PRODUCE_BESKJED)
        `produser brukernotifikasjon`(tokenAt4, oppgave1, ProducerOperations.PRODUCE_OPPGAVE)
        `produser brukernotifikasjon`(tokenAt4, oppgave2, ProducerOperations.PRODUCE_OPPGAVE)
        `produser brukernotifikasjon`(tokenAt4, innboks1, ProducerOperations.PRODUCE_INNBOKS)
        `produser brukernotifikasjon`(tokenAt4, innboks2, ProducerOperations.PRODUCE_INNBOKS)

        val activeBeskjedEvents: List<no.nav.tms.docker.compose.e2e.beskjed.BeskjedDTO>? = `wait for events`{
            `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        }

        val activeOppgaveEvents: List<OppgaveDTO>? = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_OPPGAVE)
        }

        val activeInnboksEvents: List<InnboksDTO>? = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_INNBOKS)
        }
        activeOppgaveEvents!!.`should not be empty`()
        activeBeskjedEvents!!.`should not be empty`()
        activeInnboksEvents!!.`should not be empty`()

        `produser done-eventer for alle brukernotifikasjoner`(tokenAt4)

        val doknotifikasjonStoppForBeskjedToMatch = listOf(
                DoknotifikasjonStoppDTO(activeBeskjedEvents[0].eventId),
                DoknotifikasjonStoppDTO(activeBeskjedEvents[1].eventId)
        )

        val doknotifikasjonStoppForOppgaveToMatch = listOf(
                DoknotifikasjonStoppDTO(activeOppgaveEvents[0].eventId),
                DoknotifikasjonStoppDTO(activeOppgaveEvents[1].eventId)
        )

        val doknotifikasjonStoppForInnboksToMatch = listOf(
                DoknotifikasjonStoppDTO(activeInnboksEvents[0].eventId),
                DoknotifikasjonStoppDTO(activeInnboksEvents[1].eventId)
        )


        val doknotifikasjonStoppForBeskjed = `wait for values to be returned`(doknotifikasjonStoppForBeskjedToMatch) {
            `get doknotifikasjonstopp count`(VarselOperations.GET_DOKNOTIFIKASJONSTOPP_BESKJED)
        }

        val doknotifikasjonStoppForOppgave = `wait for values to be returned`(doknotifikasjonStoppForOppgaveToMatch) {
            `get doknotifikasjonstopp count`(VarselOperations.GET_DOKNOTIFIKASJONSTOPP_OPPGAVE)
        }

        val doknotifikasjonStoppForInnboks = `wait for values to be returned`(doknotifikasjonStoppForInnboksToMatch) {
            `get doknotifikasjonstopp count`(VarselOperations.GET_DOKNOTIFIKASJONSTOPP_INNBOKS)
        }

        doknotifikasjonStoppForBeskjed!! `should contain all` doknotifikasjonStoppForBeskjedToMatch
        doknotifikasjonStoppForOppgave!! `should contain all` doknotifikasjonStoppForOppgaveToMatch
        doknotifikasjonStoppForInnboks!! `should contain all` doknotifikasjonStoppForInnboksToMatch
    }

    private fun `verify no active oppgave-events`(token: TokenInfo) {
        val inactiveOppgaveEvents: List<OppgaveDTO>? = `wait for events` {
            `get events`(token, ApiOperations.FETCH_OPPGAVE_INACTIVE)
        }
        inactiveOppgaveEvents!!.`should not be empty`()

        val activeOppgaveEvents: List<OppgaveDTO> = `get events`(token, ApiOperations.FETCH_OPPGAVE)
        activeOppgaveEvents.`should be empty`()
    }

    private fun `verify no active beskjed-events`(token: TokenInfo) {
        val inactiveBeskjedEvents: List<no.nav.tms.docker.compose.e2e.beskjed.BeskjedDTO>? = `wait for events` {
            `get events`(token, ApiOperations.FETCH_BESKJED_INACTIVE)
        }
        inactiveBeskjedEvents!!.`should not be empty`()

        val activeBeskjedEvents: List<no.nav.tms.docker.compose.e2e.beskjed.BeskjedDTO> = `get events`(token, ApiOperations.FETCH_BESKJED)
        activeBeskjedEvents.`should be empty`()
    }

    private fun `verify no active innboks-events`(token: TokenInfo) {
        val inactiveInnboksEvents: List<InnboksDTO>? = `wait for events` {
            `get events`(token, ApiOperations.FETCH_INNBOKS_INACTIVE)
        }
        inactiveInnboksEvents!!.`should not be empty`()

        val activeInnboksEvents: List<InnboksDTO> = `get events`(token, ApiOperations.FETCH_INNBOKS)
        activeInnboksEvents.`should be empty`()
    }

    private fun `produser brukernotifikasjon`(token: TokenInfo, brukernotifikasjon: no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO, producerOperation: ProducerOperations) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, producerOperation, brukernotifikasjon, BearerToken(token.id_token))
        }
    }

    private fun `produser done-eventer for alle brukernotifikasjoner`(token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_DONE_ALL, ProduceDoneDTO(), BearerToken(token.id_token))
        }
    }

    private inline fun <reified T> `get events`(token: TokenInfo, apiOperation: ApiOperations): T {
        return runBlocking {
            val response = client.get<T>(ServiceConfiguration.API, apiOperation, BearerToken(token.id_token))
            response
        }
    }

    private fun `get doknotifikasjonstopp count`(operation: VarselOperations): List<DoknotifikasjonStoppDTO> {
        return runBlocking {
            val response = client.getWithoutAuth<List<DoknotifikasjonStoppDTO>>(ServiceConfiguration.MOCKS, operation)
            response
        }
    }
}

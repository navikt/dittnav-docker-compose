package no.nav.personbruker.dittnav.e2e.innboks

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.debugging.ApiContainerLogs
import no.nav.personbruker.dittnav.e2e.debugging.ProducerContainerLogs
import no.nav.personbruker.dittnav.e2e.doknotifikasjon.DoknotifikasjonDTO
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.operations.VarselOperations
import no.nav.personbruker.dittnav.e2e.security.BearerToken
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain all`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    ApiContainerLogs::class,
    ProducerContainerLogs::class
)
class InnboksIT : UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere innboks-eventer paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Innboks 1"
        val tokenAt3 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalInnboksEvent = ProduceInnboksDTO(expectedText)

        `produce innboks-event at level`(originalInnboksEvent, tokenAt3)
        val activeInnboksEvents = `wait for events` {
            `get events`(tokenAt3, ApiOperations.FETCH_INNBOKS)
        }
        `verify innboks-event`(activeInnboksEvents!![0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere innboks-eventer paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Innboks 2"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalInnboksEvent = ProduceInnboksDTO(expectedText)

        `produce innboks-event at level`(originalInnboksEvent, tokenAt4)
        val activeInnboksEvents = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_INNBOKS)
        }
        `verify innboks-event`(activeInnboksEvents!![0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal bestille ekstern varsling for innboks`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val originalInnboks1 = ProduceInnboksDTO("Innboks med varsel 1", eksternVarsling = true)
        val originalInnboks2 = ProduceInnboksDTO("Innboks med varsel 2", eksternVarsling = true)
        `produce innboks-event at level`(originalInnboks1, tokenAt4)
        `produce innboks-event at level`(originalInnboks2, tokenAt4)
        val activeInnboks = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_INNBOKS)
        }

        val doknotifikasjonerToMatch = listOf(
                DoknotifikasjonDTO(activeInnboks!![0].eventId),
                DoknotifikasjonDTO(activeInnboks[1].eventId)
        )

        val doknotifikasjoner = `wait for values to be returned`(doknotifikasjonerToMatch) {
            `get doknotifikasjoner`(VarselOperations.GET_DOKNOTIFIKASJON_INNBOKS)
        }

        doknotifikasjoner!! `should contain all` doknotifikasjonerToMatch
    }

    private fun `produce innboks-event at level`(originalInnboksEvent: ProduceInnboksDTO, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(
                ServiceConfiguration.PRODUCER,
                ProducerOperations.PRODUCE_INNBOKS,
                originalInnboksEvent,
                BearerToken(token.id_token)
            )
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `verify innboks-event`(innboksEvent: InnboksDTO, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            innboksEvent.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            innboksEvent.tekst `should be equal to` expectedText
        }
    }

    private fun `get events`(token: TokenInfo, operation: ApiOperations): List<InnboksDTO> {
        return runBlocking {
            val response = client.get<List<InnboksDTO>>(ServiceConfiguration.API, operation, BearerToken(token.id_token))
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

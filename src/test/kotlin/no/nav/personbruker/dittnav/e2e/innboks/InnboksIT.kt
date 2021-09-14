package no.nav.personbruker.dittnav.e2e.innboks

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.debugging.ApiContainerLogs
import no.nav.personbruker.dittnav.e2e.debugging.ProducerContainerLogs
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
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

    private fun `produce innboks-event at level`(originalInnboksEvent: ProduceInnboksDTO, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(
                ServiceConfiguration.PRODUCER,
                ProducerOperations.PRODUCE_INNBOKS,
                originalInnboksEvent,
                token
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
            val response = client.get<List<InnboksDTO>>(ServiceConfiguration.API, operation, token)
            response
        }
    }
}

package no.nav.personbruker.dittnav.e2e.tidslinje

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.client.ProduceDto
import no.nav.personbruker.dittnav.e2e.client.ProduceStatusoppdateringDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.operations.ServiceOperation
import no.nav.personbruker.dittnav.e2e.operations.TidslinjeOperations
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class TidslinjeIT : UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"
    private val produsent = "produsent"

    @Test
    fun `Skal hente alle eventer som er gruppert sammen og er paa sikkerhetsnivaa 4`() {
        val grupperingsid = "1"
        val getParameters = mapOf("grupperingsid" to grupperingsid, "produsent" to produsent)
        val expectedSikkerhetsnivaa = 4

        val expectedTextBeskjed = "Beskjed 1"
        val expectedTextOppgave = "Oppgave 1"
        val expectedStatusInternStatusoppdatering = "Statusoppdatering 1"

        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)

        val originalBeskjed = ProduceBrukernotifikasjonDto(expectedTextBeskjed, grupperingsid)
        val originalOppgave = ProduceBrukernotifikasjonDto(expectedTextOppgave, grupperingsid)
        val originalStatusoppdatering = ProduceStatusoppdateringDto(expectedStatusInternStatusoppdatering, grupperingsid)

        `produce event at level`(originalBeskjed, ProducerOperations.PRODUCE_BESKJED, tokenAt4)
        `wait for events to be processed`()

        `produce event at level`(originalOppgave, ProducerOperations.PRODUCE_OPPGAVE, tokenAt4)
        `wait for events to be processed`()

        `produce event at level`(originalStatusoppdatering, ProducerOperations.PRODUCE_STATUSOPPDATERING, tokenAt4)
        `wait for events to be processed`()

        val tidslinjeEvents = `get events from tidslinje`(tokenAt4, TidslinjeOperations.TIDSLINJE, getParameters)
        tidslinjeEvents.size `should be equal to` 3
        `verify event`(tidslinjeEvents[0], expectedSikkerhetsnivaa, "Statusoppdatering")
        `verify event`(tidslinjeEvents[1], expectedSikkerhetsnivaa, "Oppgave")
        `verify event`(tidslinjeEvents[2], expectedSikkerhetsnivaa, "Beskjed")
    }

    @Test
    fun `Skal hente alle eventer som er gruppert sammen og er paa sikkerhetsnivaa 3`() {
        val grupperingsid = "2"
        val getParameters = mapOf("grupperingsid" to grupperingsid, "produsent" to produsent)
        val expectedSikkerhetsnivaa = 3

        val expectedTextBeskjed = "Beskjed 2"
        val expectedStatusInternStatusoppdatering = "Statusoppdatering 2"

        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)

        val originalBeskjed = ProduceBrukernotifikasjonDto(expectedTextBeskjed, grupperingsid)
        val originalStatusoppdatering = ProduceStatusoppdateringDto(expectedStatusInternStatusoppdatering, grupperingsid)

        `produce event at level`(originalBeskjed, ProducerOperations.PRODUCE_BESKJED, tokenAt4)
        `wait for events to be processed`()

        `produce event at level`(originalStatusoppdatering, ProducerOperations.PRODUCE_STATUSOPPDATERING, tokenAt4)
        `wait for events to be processed`()

        val tidslinjeEvents = `get events from tidslinje`(tokenAt4, TidslinjeOperations.TIDSLINJE, getParameters)
        tidslinjeEvents.size `should be equal to` 2
        `verify event`(tidslinjeEvents[0], expectedSikkerhetsnivaa, "Statusoppdatering")
        `verify event`(tidslinjeEvents[1], expectedSikkerhetsnivaa, "Beskjed")
    }

    @Test
    fun `Skal hente tom liste hvis ingen eventer matcher grupperingsid`() {
        val expectedSikkerhetsnivaa = 4
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val getParameters = mapOf("grupperingsid" to "noMatchGrupperingsid", "produsent" to produsent)

        val tidslinjeEvents = `get events from tidslinje`(tokenAt4, TidslinjeOperations.TIDSLINJE, getParameters)
        tidslinjeEvents.size `should be equal to` 0
    }

    @Test
    fun `Skal hente tom liste hvis ingen eventer matcher produsent`() {
        val expectedSikkerhetsnivaa = 4
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val getParameters = mapOf("grupperingsid" to "2", "produsent" to "noMatchProducer")

        val tidslinjeEvents = `get events from tidslinje`(tokenAt4, TidslinjeOperations.TIDSLINJE, getParameters)
        tidslinjeEvents.size `should be equal to` 0
    }

    private fun `verify event`(event: Brukernotifikasjon, expectedSikkerhetsnivaa: Int, expectedType: String) {
        runBlocking {
            event.type `should be equal to` expectedType
            event.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
        }
    }

    private fun `produce event at level`(originalEvent: ProduceDto, operation: ServiceOperation, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, operation, originalEvent, token)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `get events from tidslinje`(token: TokenInfo,
                                            operation: TidslinjeOperations,
                                            parameters: Map<String, String>): List<Brukernotifikasjon> {
        return runBlocking {
            val response =
                    client.get<List<Brukernotifikasjon>>(
                            ServiceConfiguration.TIDSLINJE,
                            operation,
                            token,
                            parameters)
            response
        }
    }

}
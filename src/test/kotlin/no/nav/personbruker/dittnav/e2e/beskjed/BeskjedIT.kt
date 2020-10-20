package no.nav.personbruker.dittnav.e2e.beskjed

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.client.ProduceDoneDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class BeskjedIT : UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere beskjeder paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Beskjed 1"
        val expectedGrupperingsid = "1"
        val tokenAt3 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBrukernotifikasjonDto(expectedText, expectedGrupperingsid)

        `produce beskjed at level`(originalBeskjed, tokenAt3)
        `wait for events to be processed`()
        val activeBeskjeder = `get events`(tokenAt3, ApiOperations.FETCH_BESKJED)
        `verify beskjed`(activeBeskjeder[0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere beskjeder paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Beskjed 2"
        val expectedGrupperingsid = "2"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBrukernotifikasjonDto(expectedText, expectedGrupperingsid)

        `produce beskjed at level`(originalBeskjed, tokenAt4)
        `wait for events to be processed`()
        val activeBeskjeder = `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        `verify beskjed`(activeBeskjeder[0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere done-event for beskjed`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Beskjed 3"
        val expectedGrupperingsid = "3"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBrukernotifikasjonDto(expectedText, expectedGrupperingsid)

        `produce beskjed at level`(originalBeskjed, tokenAt4)
        `wait for events to be processed`()
        val activeBeskjeder = `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        `verify beskjed`(activeBeskjeder[0], expectedSikkerhetsnivaa, expectedText)

        val originalDone = ProduceDoneDto(activeBeskjeder[0].uid, activeBeskjeder[0].eventId)
        `produce done-event for beskjed`(originalDone, tokenAt4)
        val inactiveBeskjeder = `get events`(tokenAt4, ApiOperations.FETCH_BESKJED_INACTIVE)
        `verify beskjed`(inactiveBeskjeder[0], expectedSikkerhetsnivaa, expectedText)
    }

    private fun `verify beskjed`(beskjed: BeskjedDTO, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            beskjed.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            beskjed.tekst `should be equal to` expectedText
        }
    }

    private fun `produce beskjed at level`(originalBeskjed: ProduceBrukernotifikasjonDto, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_BESKJED, originalBeskjed, token)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `produce done-event for beskjed`(originalDone: ProduceDoneDto, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.API, ApiOperations.PRODUCE_DONE, originalDone, token)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `get events`(token: TokenInfo, operation: ApiOperations): List<BeskjedDTO> {
        return runBlocking {
            val response = client.get<List<BeskjedDTO>>(ServiceConfiguration.API, operation, token)
            response
        }
    }
}

package no.nav.personbruker.dittnav.e2e.beskjed

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.done.ProduceDoneDTO
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.operations.VarselOperations
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class BeskjedIT : UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere beskjeder paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Beskjed 1"
        val tokenAt3 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBeskjedDTO(expectedText)

        `produce beskjed at level`(originalBeskjed, tokenAt3)
        `wait for events to be processed`()
        val activeBeskjeder = `get events`(tokenAt3, ApiOperations.FETCH_BESKJED)
        `verify beskjed`(activeBeskjeder[0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere beskjeder paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Beskjed 2"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBeskjedDTO(expectedText)

        `produce beskjed at level`(originalBeskjed, tokenAt4)
        `wait for events to be processed`()
        val activeBeskjeder = `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        `verify beskjed`(activeBeskjeder[0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere done-event for beskjed`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Beskjed 3"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBeskjedDTO(expectedText)

        `produce beskjed at level`(originalBeskjed, tokenAt4)
        `wait for events to be processed`()
        val activeBeskjeder = `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        `verify beskjed`(activeBeskjeder[0], expectedSikkerhetsnivaa, expectedText)

        val originalDone = ProduceDoneDTO(activeBeskjeder[0].uid, activeBeskjeder[0].eventId)
        `produce done-event for beskjed`(originalDone, tokenAt4)
        val inactiveBeskjeder = `get events`(tokenAt4, ApiOperations.FETCH_BESKJED_INACTIVE)
        `verify beskjed`(inactiveBeskjeder[0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal bestille ekstern varsling for beskjeder`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val originalBeskjed1 = ProduceBeskjedDTO("Beskjed med varsel 1", eksternVarsling = true)
        val originalBeskjed2 = ProduceBeskjedDTO("Beskjed med varsel 2", eksternVarsling = true)
        val originalBeskjed3 = ProduceBeskjedDTO("Beskjed uten varsel 1", eksternVarsling = false)
        `produce beskjed at level`(originalBeskjed1, tokenAt4)
        `produce beskjed at level`(originalBeskjed2, tokenAt4)
        `produce beskjed at level`(originalBeskjed3, tokenAt4)
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

    private fun `verify beskjed`(beskjed: BeskjedDTO, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            beskjed.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            beskjed.tekst `should be equal to` expectedText
        }
    }

    private fun `produce beskjed at level`(originalBeskjed: ProduceBeskjedDTO, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_BESKJED, originalBeskjed, token)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `produce done-event for beskjed`(originalDone: ProduceDoneDTO, token: TokenInfo) {
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

    private suspend fun `get doknotifikasjon count`(): Int {
        return client.getWithoutAuth(ServiceConfiguration.MOCKS, VarselOperations.COUNT_DOKNOTIFIKASJON)
    }
}

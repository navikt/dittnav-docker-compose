package no.nav.personbruker.dittnav.e2e.beskjed

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ApiOperations
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.security.TokenFetcher
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.junit.jupiter.api.Test

internal class BeskjedIT : UsesTheCommonDockerComposeContext() {

    @Test
    fun `Skal produsere beskjeder paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Beskjed 1"

        val tokenAt3 = TokenFetcher.fetchTokenForIdent("b1", expectedSikkerhetsnivaa)

        val originalBeskjed = ProduceBrukernotifikasjonDto(expectedText)

        `produce beskjed at level 3`(originalBeskjed, tokenAt3)

        `verify that the beskjed is active`(tokenAt3, expectedSikkerhetsnivaa, expectedText)
    }

    private fun `produce beskjed at level 3`(originalBeskjed: ProduceBrukernotifikasjonDto, tokenAt3: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, ProducerOperations.PRODUCE_BESKJED, originalBeskjed, tokenAt3)
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `verify that the beskjed is active`(tokenAt3: TokenInfo, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            val response = client.get<List<BeskjedDTO>>(ServiceConfiguration.API, ApiOperations.FETCH_BESKJED, tokenAt3)
            response.`should not be empty`()

            val firstBeskjed = response[0]
            firstBeskjed.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            firstBeskjed.tekst `should be equal to` expectedText
            firstBeskjed.tekst `should be equal to` expectedText
        }
    }

}

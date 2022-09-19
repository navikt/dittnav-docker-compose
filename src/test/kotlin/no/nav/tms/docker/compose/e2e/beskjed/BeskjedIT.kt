package no.nav.tms.docker.compose.e2e.beskjed

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration
import no.nav.tms.docker.compose.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.tms.docker.compose.e2e.debugging.ApiContainerLogs
import no.nav.tms.docker.compose.e2e.debugging.MocksContainerLogs
import no.nav.tms.docker.compose.e2e.debugging.ProducerContainerLogs
import no.nav.tms.docker.compose.e2e.doknotifikasjon.DoknotifikasjonDTO
import no.nav.tms.docker.compose.e2e.done.ProduceDoneDTO
import no.nav.tms.docker.compose.e2e.operations.ApiOperations
import no.nav.tms.docker.compose.e2e.operations.ProducerOperations
import no.nav.tms.docker.compose.e2e.operations.VarselOperations
import no.nav.tms.docker.compose.e2e.security.BearerToken
import no.nav.tms.docker.compose.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain all`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    MocksContainerLogs::class,
    ApiContainerLogs::class,
    ProducerContainerLogs::class
)
internal class BeskjedIT : UsesTheCommonDockerComposeContext() {

    private val ident = "12345678901"

    @Test
    fun `Skal produsere beskjeder paa sikkerhetsnivaa 3`() {
        val expectedSikkerhetsnivaa = 3
        val expectedText = "Beskjed 1"
        val tokenAt3 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBeskjedDTO(expectedText)

        `produce beskjed at level`(originalBeskjed, tokenAt3)
        val activeBeskjeder = `wait for events` {
            `get events`(tokenAt3, ApiOperations.FETCH_BESKJED)
        }
        `verify beskjed`(activeBeskjeder!![0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere beskjeder paa sikkerhetsnivaa 4`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Beskjed 2"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBeskjedDTO(expectedText)

        `produce beskjed at level`(originalBeskjed, tokenAt4)
        val activeBeskjeder = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        }
        `verify beskjed`(activeBeskjeder!![0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal produsere done-event for beskjed`() {
        val expectedSikkerhetsnivaa = 4
        val expectedText = "Beskjed 3"
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, expectedSikkerhetsnivaa)
        val originalBeskjed = ProduceBeskjedDTO(expectedText)

        `produce beskjed at level`(originalBeskjed, tokenAt4)
        val activeBeskjeder = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        }
        `verify beskjed`(activeBeskjeder!![0], expectedSikkerhetsnivaa, expectedText)

        val originalDone = ProduceDoneDTO(activeBeskjeder[0].eventId)
        `produce done-event for beskjed`(originalDone, tokenAt4)
        val inactiveBeskjeder = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_BESKJED_INACTIVE)
        }
        `verify beskjed`(inactiveBeskjeder!![0], expectedSikkerhetsnivaa, expectedText)
    }

    @Test
    fun `Skal bestille ekstern varsling for beskjeder`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val originalBeskjed1 = ProduceBeskjedDTO("Beskjed med varsel 1", eksternVarsling = true)
        val originalBeskjed2 = ProduceBeskjedDTO("Beskjed med varsel 2", eksternVarsling = true)
        `produce beskjed at level`(originalBeskjed1, tokenAt4)
        `produce beskjed at level`(originalBeskjed2, tokenAt4)
        val activeBeskjed = `wait for events` {
            `get events`(tokenAt4, ApiOperations.FETCH_BESKJED)
        }

        val doknotifikasjonerToMatch = listOf(
            DoknotifikasjonDTO(activeBeskjed!![0].eventId),
            DoknotifikasjonDTO(activeBeskjed[1].eventId)
        )

        val doknotifikasjoner = `wait for values to be returned`(doknotifikasjonerToMatch) {
            `get doknotifikasjoner`(VarselOperations.GET_DOKNOTIFIKASJON_BESKJED)
        }

        doknotifikasjoner!! `should contain all` doknotifikasjonerToMatch
    }

    private fun `verify beskjed`(beskjed: BeskjedDTO, expectedSikkerhetsnivaa: Int, expectedText: String) {
        runBlocking {
            beskjed.sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
            beskjed.tekst `should be equal to` expectedText
        }
    }

    private fun `produce beskjed at level`(originalBeskjed: ProduceBeskjedDTO, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(
                ServiceConfiguration.PRODUCER,
                ProducerOperations.PRODUCE_BESKJED,
                originalBeskjed,
                BearerToken(token.id_token)
            )
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `produce done-event for beskjed`(originalDone: ProduceDoneDTO, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.API, ApiOperations.PRODUCE_DONE, originalDone, BearerToken(token.id_token))
        }.status `should be equal to` HttpStatusCode.OK
    }

    private fun `get events`(token: TokenInfo, operation: ApiOperations): List<BeskjedDTO> {
        return runBlocking {
            val response = client.get<List<BeskjedDTO>>(ServiceConfiguration.API, operation, BearerToken(token.id_token))
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

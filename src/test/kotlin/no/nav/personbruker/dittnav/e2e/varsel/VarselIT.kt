package no.nav.personbruker.dittnav.e2e.varsel

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.ProduceBrukernotifikasjonDto
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import no.nav.personbruker.dittnav.e2e.operations.ProducerOperations
import no.nav.personbruker.dittnav.e2e.security.TokenInfo
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class VarselIT: UsesTheCommonDockerComposeContext() {


    private val ident = "12345678901"

    @Test
    fun `Skal ha ekstern varsling for beskjeder`() {
        val tokenAt4 = tokenFetcher.fetchTokenForIdent(ident, sikkerhetsnivaa = 4)
        val originalBeskjed = ProduceBrukernotifikasjonDto("Beskjed med varsel", eksternVarsling = true)
        `produce event at level`(ProducerOperations.PRODUCE_BESKJED, originalBeskjed, tokenAt4)
    }

    private fun `produce event at level`(producerOperation: ProducerOperations, originalEvent: ProduceBrukernotifikasjonDto, token: TokenInfo) {
        runBlocking {
            client.post<HttpResponse>(ServiceConfiguration.PRODUCER, producerOperation, originalEvent, token)
        }.status `should be equal to` HttpStatusCode.OK
    }
}

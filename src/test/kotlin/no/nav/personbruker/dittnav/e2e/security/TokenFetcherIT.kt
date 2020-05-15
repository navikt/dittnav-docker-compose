package no.nav.personbruker.dittnav.e2e.security

import com.auth0.jwt.JWT
import no.nav.personbruker.dittnav.e2e.config.UsesTheCommonDockerComposeContext
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class TokenFetcherIT : UsesTheCommonDockerComposeContext() {

    @Test
    fun `Skal kunne hente OIDC-token for en bruker paa sikkerhetsnivaa 3`() {
        verifyTokenIdentAndSikkerhetsnivaa("333", 3)
    }

    @Test
    fun `Skal kunne hente OIDC-token for en bruker paa sikkerhetsnivaa 4`() {
        verifyTokenIdentAndSikkerhetsnivaa("444", 4)
    }

    private fun verifyTokenIdentAndSikkerhetsnivaa(expectedIdent: String, expectedSikkerhetsnivaa: Int) {
        val tokenInfo = TokenFetcher.fetchTokenForIdent(expectedIdent, expectedSikkerhetsnivaa)

        val decodedToken = JWT.decode(tokenInfo.id_token)
        val ident = decodedToken.getClaim("pid").asString()
        val sikkerhetsnivaa = decodedToken.getClaim("acr").asString().toInt()

        ident `should be equal to` expectedIdent
        sikkerhetsnivaa `should be equal to` expectedSikkerhetsnivaa
    }

}

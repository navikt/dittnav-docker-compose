package no.nav.personbruker.dittnav.e2e.security

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.buildHttpClient
import no.nav.personbruker.dittnav.e2e.client.get
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

internal class TokenFetcher(private val audience: String,
                   private val clientSecret: String,
                   private val oidcProviderBaseUrl: String) {

    private val log = LoggerFactory.getLogger(TokenFetcher::class.java)

    private val oidcProviderGuiUrl = "http://localhost:50000/callback"
    private val authMockUrl = "http://localhost:9051"

    private val client = buildHttpClient()

    private val authenticationHeader = "$audience:$clientSecret".base64Encode()
    private val redirectToInitTokenFlow =
        "$oidcProviderBaseUrl/auth?client_id=$audience&redirect_uri=$oidcProviderGuiUrl&response_type=code&scope=openid+profile+acr+email&nonce=123"

    fun fetchTokenForIdent(ident: String, sikkerhetsnivaa: Int): TokenInfo = runBlocking {
        try {
            val loginFormUuid = fetchLoginFormUuid(sikkerhetsnivaa)
            postLoginFormForIdent(loginFormUuid, ident)
            val authorizationCode = fetchAuthorizationCodeForUuid(loginFormUuid)
            return@runBlocking fetchToken(authorizationCode)
        } catch (e: Exception) {
            val msg = "Uventet feil ved forsøk på å hente ut token for lokal OIDC-provider"
            val fetcherException = TokenFetcherException(msg, e)
            fetcherException.addContext("ident", ident)
            fetcherException.addContext("sikkerhetsnivaa", sikkerhetsnivaa)
            throw fetcherException
        }
    }

    fun exchangeToken(clientId: String, audience: String, tokenInfo: TokenInfo): TokenXToken = runBlocking {
        val clientAssertion = getSignedAssertion(clientId, audience)

        val urlParameters = ParametersBuilder().apply {
            append("client_assertion", clientAssertion)
            append("subject_token", tokenInfo.id_token)
            append("audience", audience)
        }.build()
        return@runBlocking fetchExchangedToken(TextContent(urlParameters.formUrlEncode(), ContentType.Application.FormUrlEncoded))
    }

    private suspend fun getSignedAssertion(clientId: String, audience: String): String {
        val completeUrlToHit = "$authMockUrl/tokendings/clientassertion"
        return client.request {
            url(completeUrlToHit)
            method = HttpMethod.Get
            expectSuccess = false
            parameter("clientId", clientId)
            parameter("audience", audience)
        }
    }

    private suspend fun fetchExchangedToken(content: TextContent): TokenXToken {
        val completeUrlToHit = "$authMockUrl/tokendings/token"
        return try {
            client.request{
                url(completeUrlToHit)
                method = HttpMethod.Post
                expectSuccess = false
                body = content
            }
        } catch (e: Exception) {
            val msg = "Uventet feil skjedde mot auth-mock, klarte ikke å gjenomføre et kallet mot $completeUrlToHit"
            log.error(msg)
            throw e
        }
    }

    private suspend fun fetchLoginFormUuid(sikkerhetsnivaa: Int): String {
        val initFlowUrl = URL("$redirectToInitTokenFlow&acr_values=Level$sikkerhetsnivaa")
        try {
            client.request<String> {
                url(initFlowUrl)
                method = HttpMethod.Get
            }
            throw Exception("Dette skal ikke skje")
        } catch (re: ResponseException) {
            // Klarer ikke å gå til innloggingssiden, fisker derfor kun ut UUID for innloggings-form-en
            val urlContainingTheFormUuid = re.response.call.request.url
            return extractUuidFromUrl(urlContainingTheFormUuid.toString())
        }
    }

    private fun extractUuidFromUrl(url: String): String {
        val urlParts = url.split("/interaction/")
        val indexOfLastPart = urlParts.size - 1
        return urlParts[indexOfLastPart]
    }

    private suspend fun postLoginFormForIdent(uuid: String, ident: String) {
        val formUrl = URL("$oidcProviderBaseUrl/interaction/$uuid")
        val formSubmitUrl = URL("$formUrl/login")
        try {
            client.request<String> {
                url(formSubmitUrl)
                method = HttpMethod.Post
                header(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                header(HttpHeaders.Referrer, formUrl)
                body = "uuid=$uuid&login=$ident"
            }
        } catch (ure: RedirectResponseException) {
            // Her er det forventet at en redirect feiler, og det er ikke noe problem.
        }
    }

    private suspend fun fetchAuthorizationCodeForUuid(uuid: String): String {
        val authUrl = URL("$oidcProviderBaseUrl/auth/$uuid")
        val response = client.get<HttpResponse>(authUrl)
        return extractAuthorizationCodeFromUrl(response)
    }

    private fun extractAuthorizationCodeFromUrl(response: HttpResponse): String {
        val requestUrlContainingAuthCode = response.call.request.url.toString()
        val urlParts = requestUrlContainingAuthCode.split("&")
        val authCodeWithProtocolAndPrefix = urlParts[0]
        val authCodeWithPrefix = authCodeWithProtocolAndPrefix.replace(oidcProviderGuiUrl, "")
        return authCodeWithPrefix.replace("?code=", "")
    }

    private suspend fun fetchToken(code: String): TokenInfo {
        val tokenUrl = URL("$oidcProviderBaseUrl/token")
        return client.request {
            url(tokenUrl)
            method = HttpMethod.Post
            header(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
            header(HttpHeaders.Authorization, "Basic $authenticationHeader")
            accept(ContentType.Application.Json)
            body =
                "grant_type=authorization_code&code=${code}&redirect_uri=$oidcProviderGuiUrl&client_secret=$clientSecret"
        }
    }
}

internal fun String.base64Encode(): String {
    return Base64.getEncoder().encodeToString(toByteArray())
}

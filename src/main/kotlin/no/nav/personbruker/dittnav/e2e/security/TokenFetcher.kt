package no.nav.personbruker.dittnav.e2e.security

import io.ktor.client.features.RedirectResponseException
import io.ktor.client.features.ResponseException
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.buildHttpClient
import no.nav.personbruker.dittnav.e2e.client.get
import java.net.URL
import java.util.*

object TokenFetcher {

    private val client = buildHttpClient()

    private val audience = "stubOidcClient"
    private val clientSecret = "secretsarehardtokeep"
    private val oidcProviderBaseUrl = "http://localhost:9000"
    private val oidcProviderGuiUrl = "http://localhost:5000/callback"

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

    private suspend fun fetchLoginFormUuid(sikkerhetsnivaa: Int): String {
        val initFlowUrl = URL("$redirectToInitTokenFlow&acr_values=$sikkerhetsnivaa")
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

fun String.base64Encode(): String {
    return Base64.getEncoder().encodeToString(toByteArray())
}

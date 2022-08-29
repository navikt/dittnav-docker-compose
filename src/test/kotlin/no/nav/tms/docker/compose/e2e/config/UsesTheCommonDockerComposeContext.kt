package no.nav.tms.docker.compose.e2e.config

import no.nav.tms.docker.compose.e2e.client.buildHttpClient
import no.nav.tms.docker.compose.e2e.security.TokenFetcher
import org.awaitility.Durations
import org.awaitility.core.ConditionTimeoutException
import org.awaitility.kotlin.*
import org.slf4j.LoggerFactory

/**
 * Hjelpeklasse som tester kan arve fra for å få tilgang til den felles docker-compose-konteksten.
 *
 * Trenger denne klassen fordi det ikke er mulig å arve fra en Singleton (DittNavDockerComposeCommonContext).
 */
internal open class UsesTheCommonDockerComposeContext {

    val dockerComposeContext = DittNavDockerComposeCommonContext.instance
    val client = no.nav.tms.docker.compose.e2e.client.RestClient(buildHttpClient())

    private val oidcproviderURL = dockerComposeContext.getBaseUrl(ServiceConfiguration.OIDC_PROVIDER).toString()
    private val log = LoggerFactory.getLogger(UsesTheCommonDockerComposeContext::class.java)

    val dittnavEventHandlerClientId = "dittnav-event-handler-clientid"

    val tokenFetcher = TokenFetcher(
            audience = "stubOidcClient",
            clientSecret = "secretsarehardtokeep",
            oidcProviderBaseUrl = oidcproviderURL
    )

    fun <T> `wait for events`(functionToReturnTheResult: () -> List<T>): List<T>? {
        val timeToWait = Durations.TEN_SECONDS
        return try{
            await
                .atMost(timeToWait)
                .withPollDelay(Durations.ONE_SECOND)
                .withPollInterval(Durations.ONE_SECOND)
                .untilCallTo { functionToReturnTheResult() } matches { count -> count?.isNotEmpty()!! }

        } catch (e: ConditionTimeoutException) {
            log.info("Fikk ikke svar fra ønsket funksjon i løpet av $timeToWait.")
            emptyList()
        }
    }

    fun <T> `wait for values to be returned`(valuesToWaitFor: List<T>, functionToReturnTheResult: () -> List<T>): List<T>? {
        val timeToWait = Durations.TEN_SECONDS
        return try {
            await
                .atMost(timeToWait)
                .withPollDelay(Durations.ONE_SECOND)
                .withPollInterval(Durations.ONE_SECOND)
                .untilCallTo { functionToReturnTheResult() } matches { it!!.containsAll(valuesToWaitFor) }

        } catch (e: ConditionTimeoutException) {
            log.info("Fikk ikke svar fra ønsket funksjon i løpet av $timeToWait.")
            emptyList()
        }
    }
}

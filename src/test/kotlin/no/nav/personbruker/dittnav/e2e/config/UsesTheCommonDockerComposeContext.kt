package no.nav.personbruker.dittnav.e2e.config

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.e2e.client.ProduceDTO
import no.nav.personbruker.dittnav.e2e.client.RestClient
import no.nav.personbruker.dittnav.e2e.client.buildHttpClient
import no.nav.personbruker.dittnav.e2e.security.TokenFetcher
import org.awaitility.Durations
import org.awaitility.core.ConditionTimeoutException
import org.awaitility.kotlin.*
import org.slf4j.LoggerFactory

/**
 * Hjelpeklasse som tester kan arve fra for å få tilgang til den felles docker-compose-konteksten.
 *
 * Trenger denne klassen fordi det ikke er mulig å arve fra en Singleton (DittNavDockerComposeCommonContext).
 */
open class UsesTheCommonDockerComposeContext {

    val dockerComposeContext = DittNavDockerComposeCommonContext.instance
    val client = RestClient(buildHttpClient())

    private val oidcproviderURL = dockerComposeContext.getBaseUrl(ServiceConfiguration.OIDC_PROVIDER).toString()
    private val log = LoggerFactory.getLogger(UsesTheCommonDockerComposeContext::class.java)

    val tokenFetcher = TokenFetcher(
            audience = "stubOidcClient",
            clientSecret = "secretsarehardtokeep",
            oidcProviderBaseUrl = oidcproviderURL
    )

    fun `wait for events to be processed`(waittimeInMilliseconds: Long = 800) {
        runBlocking {
            delay(waittimeInMilliseconds)
        }
    }

    fun `wait for events`(functionToReturnTheResult: () -> List<ProduceDTO>): List<ProduceDTO>? {
        var result: List<ProduceDTO>? = emptyList()
        try {
            result = await.atLeast(Durations.ONE_SECOND)
                    .atMost(Durations.TEN_SECONDS)
                    .withPollDelay(Durations.ONE_SECOND)
                    .withPollInterval(Durations.ONE_SECOND)
                    .untilCallTo { functionToReturnTheResult() } matches { count -> count?.isNotEmpty()!! }
        }
        catch (e: ConditionTimeoutException) {
            log.info("Fikk ikke svar fra ønsket funksjon i løpet av ${Durations.TEN_SECONDS}.")
        } finally {
            return result
        }
    }

    fun `wait for value to be returned from`(valueToWaitFor: Int, functionToReturnTheResult: () -> Int): Int? {
        var result: Int? = 0
        val timeToWait = Durations.TEN_SECONDS
        try {
            result = await
                        .atMost(timeToWait)
                        .withPollDelay(Durations.ONE_SECOND)
                        .withPollInterval(Durations.ONE_SECOND)
                        .untilCallTo { functionToReturnTheResult() } matches { count -> count == valueToWaitFor }
        } catch (e: ConditionTimeoutException) {
            log.info("Fikk ikke svar fra ønsket funksjon i løpet av $timeToWait.")
        } finally {
            return result
        }
    }
}

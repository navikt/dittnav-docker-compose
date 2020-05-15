package no.nav.personbruker.dittnav.e2e.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import no.nav.personbruker.dittnav.e2e.client.RestClient

/**
 * Hjelpeklasse som tester kan arve fra for å få tilgang til den felles docker-compose-konteksten.
 *
 * Trenger denne klassen fordi det ikke er mulig å arve fra en Singleton (DittNavDockerComposeCommonContext).
 */
open class UsesTheCommonDockerComposeContext {

    val dockerComposeContext = DittNavDockerComposeCommonContext.instance
    val client = RestClient(HttpClient(Apache))

}

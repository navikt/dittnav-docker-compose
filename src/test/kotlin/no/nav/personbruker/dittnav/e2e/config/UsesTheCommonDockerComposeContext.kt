package no.nav.personbruker.dittnav.e2e.config

import no.nav.personbruker.dittnav.e2e.client.RestClient
import no.nav.personbruker.dittnav.e2e.client.buildHttpClient

/**
 * Hjelpeklasse som tester kan arve fra for å få tilgang til den felles docker-compose-konteksten.
 *
 * Trenger denne klassen fordi det ikke er mulig å arve fra en Singleton (DittNavDockerComposeCommonContext).
 */
open class UsesTheCommonDockerComposeContext {

    val dockerComposeContext = DittNavDockerComposeCommonContext.instance
    val client = RestClient(buildHttpClient())

}

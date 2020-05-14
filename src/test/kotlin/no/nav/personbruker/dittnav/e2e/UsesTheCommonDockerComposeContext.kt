package no.nav.personbruker.dittnav.e2e

import no.nav.personbruker.dittnav.e2e.config.DittNavDockerComposeCommonContext

/**
 * Hjelpeklasse som tester kan arve fra for å få tilgang til den felles docker-compose-konteksten.
 *
 * Trenger denne klassen fordi det ikke er mulig å arve fra en Singleton (DittNavDockerComposeCommonContext).
 */
open class UsesTheCommonDockerComposeContext {

    val dockerComposeContext = DittNavDockerComposeCommonContext.instance

}

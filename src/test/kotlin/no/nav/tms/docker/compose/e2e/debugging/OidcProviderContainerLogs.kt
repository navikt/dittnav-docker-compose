package no.nav.tms.docker.compose.e2e.debugging

import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration

class OidcProviderContainerLogs : PrintContainerLogsOnErrors {
    override val faildTests: MutableList<String> = mutableListOf()
    override val service = ServiceConfiguration.OIDC_PROVIDER
}

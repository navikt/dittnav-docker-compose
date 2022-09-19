package no.nav.tms.docker.compose.e2e.debugging

import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration

class DekoratorenContainerLogs : PrintContainerLogsOnErrors {
    override val faildTests: MutableList<String> = mutableListOf()
    override val service = ServiceConfiguration.DEKORATOREN
}

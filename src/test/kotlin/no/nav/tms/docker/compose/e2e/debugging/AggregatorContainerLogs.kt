package no.nav.tms.docker.compose.e2e.debugging

import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration

class AggregatorContainerLogs : PrintContainerLogsOnErrors {
    override val faildTests: MutableList<String> = mutableListOf()
    override val service = ServiceConfiguration.AGGREGATOR
}
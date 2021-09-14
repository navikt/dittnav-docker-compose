package no.nav.personbruker.dittnav.e2e.debugging

import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration

class BrukernotifikasjonbestillerContainerLogs : PrintContainerLogsOnErrors {
    override val faildTests: MutableList<String> = mutableListOf()
    override val service = ServiceConfiguration.BRUKERNOTIFIKASJONBESTILLER
}

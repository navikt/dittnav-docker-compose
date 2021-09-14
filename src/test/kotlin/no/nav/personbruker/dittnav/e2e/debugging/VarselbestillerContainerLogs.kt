package no.nav.personbruker.dittnav.e2e.debugging

import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration

class VarselbestillerContainerLogs : PrintContainerLogsOnErrors {
    override val faildTests: MutableList<String> = mutableListOf()
    override val service = ServiceConfiguration.VARSELBESTILLER
}

package no.nav.personbruker.dittnav.e2e.debugging

import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration

class AuthMockContainerLogs : PrintContainerLogsOnErrors {
    override val faildTests: MutableList<String> = mutableListOf()
    override val service = ServiceConfiguration.AUTH_MOCK
}

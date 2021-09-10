package no.nav.personbruker.dittnav.e2e.debugging

import no.nav.personbruker.dittnav.e2e.config.DittNavDockerComposeCommonContext
import no.nav.personbruker.dittnav.e2e.config.DittNavDockerComposeContainer
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface PrintContainerLogsOnErrors : TestWatcher, AfterAllCallback {

    private val log: Logger
        get() = LoggerFactory.getLogger(this.javaClass.name)

    private val dockerComposeContext: DittNavDockerComposeContainer
        get() = DittNavDockerComposeCommonContext.instance
    val faildTests: MutableList<String>

    val service: ServiceConfiguration

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        val testName = context?.displayName ?: "No name specified"
        faildTests.add(testName)
    }

    override fun testFailed(context: ExtensionContext?, e: Throwable?) {
        val testName = context?.displayName ?: "No name specified"
        faildTests.add(testName)
    }

    override fun afterAll(context: ExtensionContext?) {
        if (faildTests.isNotEmpty()) {
            log.debug("Følgende tester feilet:")
            faildTests.forEach { fT ->
                log.debug("* $fT")
            }
            log.debug("Relevante logger")
            log.debug("----------------------------------------------------------------------------------------------")
            log.debug("Container logger for $service")
            log.debug(dockerComposeContext.getLogsFor(service))
            log.debug("----------------------------------------------------------------------------------------------")
        }
    }

}

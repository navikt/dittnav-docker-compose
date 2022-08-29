package no.nav.tms.docker.compose.e2e.debugging

import no.nav.tms.docker.compose.e2e.config.DittNavDockerComposeCommonContext
import no.nav.tms.docker.compose.e2e.config.DittNavDockerComposeContainer
import no.nav.tms.docker.compose.e2e.config.ServiceConfiguration
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
            val logPrefix = "${service.name}: "
            val containerLogs = dockerComposeContext.getLogsFor(service)
            val containerLogsWithPrefix = containerLogs.replace("\n", "\n$logPrefix")

            log.info("------------------------------------------------------------------------------------------------")
            log.info("Følgende tester feilet:")
            faildTests.forEach { fT ->
                log.info("* $fT")
            }

            log.info("Relevante logger")
            log.info("Container logger for $service")
            log.info("\n${logPrefix}${containerLogsWithPrefix}")
            log.info("------------------------------------------------------------------------------------------------")
        }
    }

}

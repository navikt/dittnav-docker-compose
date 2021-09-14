package no.nav.personbruker.dittnav.e2e.done

import io.mockk.every
import io.mockk.mockk
import no.nav.personbruker.dittnav.e2e.config.ServiceConfiguration
import no.nav.personbruker.dittnav.e2e.debugging.*
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext

class PrintContainerLogsOnErrorsTest {

    @Test
    fun `Skal hente ut logger for riktig container per klasse`() {
        AggregatorContainerLogs().service `should be equal to` ServiceConfiguration.AGGREGATOR
        ApiContainerLogs().service `should be equal to` ServiceConfiguration.API
        BrukernotifikasjonbestillerContainerLogs().service `should be equal to` ServiceConfiguration.BRUKERNOTIFIKASJONBESTILLER
        DekoratorenContainerLogs().service `should be equal to` ServiceConfiguration.DEKORATOREN
        FrontendContainerLogs().service `should be equal to` ServiceConfiguration.FRONTEND
        HandlerContainerLogs().service `should be equal to` ServiceConfiguration.HANDLER
        LegacyContainerLogs().service `should be equal to` ServiceConfiguration.LEGACY
        MocksContainerLogs().service `should be equal to` ServiceConfiguration.MOCKS
        OidcProviderContainerLogs().service `should be equal to` ServiceConfiguration.OIDC_PROVIDER
        OidcProviderGuiContainerLogs().service `should be equal to` ServiceConfiguration.OIDC_PROVIDER_GUI
        ProducerContainerLogs().service `should be equal to` ServiceConfiguration.PRODUCER
        TidslinjeContainerLogs().service `should be equal to` ServiceConfiguration.TIDSLINJE
        VarselbestillerContainerLogs().service `should be equal to` ServiceConfiguration.VARSELBESTILLER
    }

    @Test
    fun `Skal lagre navnet paa tester som feiler`() {
        val containerLogger = ApiContainerLogs()
        val context = mockk<ExtensionContext>()
        val expectedNavnPaaFeilendeTest = "Navnet på testen som feilet"
        every { context.displayName } returns expectedNavnPaaFeilendeTest
        val exception = Exception("Simulert feil i en test.")

        containerLogger.testFailed(context, exception)

        containerLogger.faildTests.shouldNotBeEmpty()
        containerLogger.faildTests[0] `should be equal to` expectedNavnPaaFeilendeTest
    }

    @Test
    fun `Skal lagre navnet paa tester som blir avbrutt`() {
        val containerLogger = ApiContainerLogs()
        val context = mockk<ExtensionContext>()
        val expectedNavnPaaFeilendeTest = "Navnet på testen som feilet"
        every { context.displayName } returns expectedNavnPaaFeilendeTest
        val exception = Exception("Simulert feil i en test.")

        containerLogger.testAborted(context, exception)

        containerLogger.faildTests.shouldNotBeEmpty()
        containerLogger.faildTests[0] `should be equal to` expectedNavnPaaFeilendeTest
    }

}

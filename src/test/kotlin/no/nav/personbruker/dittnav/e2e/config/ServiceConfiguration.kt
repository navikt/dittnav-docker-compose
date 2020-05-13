package no.nav.personbruker.dittnav.e2e.config

const val defaultExposedPort = 8080
const val defaultIsAlivePath = "/internal/isAlive"

enum class ServiceConfiguration(
    val dockerComposeName: String,
    val contextPath: String,
    val exposedPort: Int,
    val isAlivePath: String
) {

    OIDC_PROVIDER("oidc-provider_1", "", 9000, ""),
    OIDC_PROVIDER_GUI("oidc-provider-gui_1", "", 5000, ""),
    FRONTEND("frontend_1", "person/dittnav", defaultExposedPort, defaultIsAlivePath),
    API("api_1", "person/dittnav-api", defaultExposedPort, defaultIsAlivePath),
    LEGACY("legacy_1", "person/dittnav-legacy-api", defaultExposedPort, defaultIsAlivePath),
    HANDLER("handler_1", "", defaultExposedPort, defaultIsAlivePath),
    AGGREGATOR("aggregator_1", "", defaultExposedPort, defaultIsAlivePath),
    PRODUCER("producer_1", "person/dittnav-event-test-producer", defaultExposedPort, "/isAlive"),
    MOCKS("mocks_1", "", defaultExposedPort, "/isAlive"),
    DEKORATOREN("dekoratoren_1", "dekoratoren", 8088, "/isAlive");

    companion object {
        fun dittNavServices(): List<ServiceConfiguration> {
            return listOf(
                FRONTEND,
                API,
                LEGACY,
                HANDLER,
                AGGREGATOR
            )
        }

        fun personbrukerServices(): List<ServiceConfiguration> {
            return dittNavServices() + listOf(
                DEKORATOREN
            )
        }

        fun mockingServices(): List<ServiceConfiguration> {
            return listOf(
                PRODUCER,
                MOCKS
            )
        }

        fun securityServices(): List<ServiceConfiguration> {
            return listOf(
                OIDC_PROVIDER,
                OIDC_PROVIDER_GUI
            )
        }
    }

}

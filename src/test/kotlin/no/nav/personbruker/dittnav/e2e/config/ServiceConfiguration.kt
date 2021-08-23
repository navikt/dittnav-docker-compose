package no.nav.personbruker.dittnav.e2e.config

const val defaultExposedPort = 8080

enum class ServiceConfiguration(
        val dockerComposeName: String,
        val contextPath: String,
        val exposedPort: Int
) {

    OIDC_PROVIDER("oidc-provider_1", "", 9000),
    OIDC_PROVIDER_GUI("oidc-provider-gui_1", "", 5000),
    FRONTEND("frontend_1", "person/dittnav", defaultExposedPort),
    API("api_1", "person/dittnav-api", defaultExposedPort),
    LEGACY("legacy_1", "person/dittnav-legacy-api", defaultExposedPort),
    HANDLER("handler_1", "", defaultExposedPort),
    AGGREGATOR("aggregator_1", "", defaultExposedPort),
    PRODUCER("producer_1", "person/dittnav-event-test-producer", defaultExposedPort),
    MOCKS("mocks_1", "", defaultExposedPort),
    DEKORATOREN("dekoratoren_1", "dekoratoren", 8088),
    TIDSLINJE("tidslinje_1", "person/dittnav-tidslinje-api", defaultExposedPort),
    VARSELBESTILLER("varselbestiller_1", "", defaultExposedPort),
    BRUKERNOTIFIKASJONBESTILLER("brukernotifikasjonbestiller_1", "", defaultExposedPort)
}

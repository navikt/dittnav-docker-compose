package no.nav.tms.docker.compose.e2e.config

const val defaultExposedPort = 8080

enum class ServiceConfiguration(
        val dockerComposeName: String,
        val contextPath: String,
        val exposedPort: Int
) {

    OIDC_PROVIDER("oidc-provider_1", "", 9000),
    OIDC_PROVIDER_GUI("oidc-provider-gui_1", "", 50000),
    FRONTEND("frontend_1", "person/dittnav", defaultExposedPort),
    API("api_1", "dittnav-api", defaultExposedPort),
    HANDLER("handler_1", "dittnav-event-handler", defaultExposedPort),
    AGGREGATOR("aggregator_1", "", defaultExposedPort),
    PRODUCER("producer_1", "tms-event-test-producer", defaultExposedPort),
    MOCKS("mocks_1", "", defaultExposedPort),
    DEKORATOREN("dekoratoren_1", "dekoratoren", 8088),
    VARSELBESTILLER("varselbestiller_1", "", defaultExposedPort),
    BRUKERNOTIFIKASJONBESTILLER("brukernotifikasjonbestiller_1", "", defaultExposedPort),
    AUTH_MOCK("auth-mock_1", "", defaultExposedPort)
}

package no.nav.tms.docker.compose.e2e.operations

enum class AuthMockOperations(override val path: String) : ServiceOperation {
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    CLIENT_ASSERTION("/tokendings/clientassertion"),
    EXCHANGE_TOKEN("/tokendings/token")
}

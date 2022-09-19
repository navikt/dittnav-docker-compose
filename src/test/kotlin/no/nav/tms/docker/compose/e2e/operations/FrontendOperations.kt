package no.nav.tms.docker.compose.e2e.operations

enum class FrontendOperations(override val path: String) : ServiceOperation {
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isAlive"),
    METRICS("/internal/metrics")
}

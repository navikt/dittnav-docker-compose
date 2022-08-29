package no.nav.tms.docker.compose.e2e.operations

enum class AggregatorOperations(override val path : String) :ServiceOperation {
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    SELFTEST("/internal/selftest"),
    METRICS("/metrics")
}

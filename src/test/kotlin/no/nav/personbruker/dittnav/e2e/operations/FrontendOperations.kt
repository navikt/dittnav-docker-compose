package no.nav.personbruker.dittnav.e2e.operations

enum class FrontendOperations(override val path: String) : ServiceOperation {
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isAlive"),
    SELFTEST("/internal/selftest"),
    METRICS("/internal/metrics")
}

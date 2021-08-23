package no.nav.personbruker.dittnav.e2e.operations

enum class BNBOperations(override val path: String) : ServiceOperation {
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    SELFTEST("/internal/selftest"),
    METRICS("/metrics"),
}

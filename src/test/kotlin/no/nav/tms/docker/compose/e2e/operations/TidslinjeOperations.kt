package no.nav.tms.docker.compose.e2e.operations

enum class TidslinjeOperations(override val path: String) : ServiceOperation {
    TIDSLINJE("/tidslinje"),

    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    SELFTEST("/internal/selftest"),
    METRICS("/metrics"),
    PING("/internal/ping");
}

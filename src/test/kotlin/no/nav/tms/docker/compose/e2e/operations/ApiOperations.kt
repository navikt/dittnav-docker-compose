package no.nav.tms.docker.compose.e2e.operations

enum class ApiOperations(override val path: String) : ServiceOperation {
    FETCH_BESKJED("/beskjed"),
    FETCH_BESKJED_INACTIVE("/beskjed/inaktiv"),

    FETCH_INNBOKS("/innboks"),
    FETCH_INNBOKS_INACTIVE("/innboks/inaktiv"),

    FETCH_OPPGAVE("/oppgave"),
    FETCH_OPPGAVE_INACTIVE("/oppgave/inaktiv"),

    PRODUCE_DONE("/produce/done"),

    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    SELFTEST("/internal/selftest"),
    METRICS("/metrics"),
    PING("/internal/ping");
}

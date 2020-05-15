package no.nav.personbruker.dittnav.e2e.operations

enum class HandlerOperations(override val path : String) :ServiceOperation {
    FETCH_BESKJED("/fetch/beskjed/aktive"),
    FETCH_BESKJED_INACTIVE("/fetch/beskjed/inaktive"),
    FETCH_BESKJED_ALL("/fetch/beskjed/all"),

    FETCH_INNBOKS("/fetch/innboks/aktive"),
    FETCH_INNBOKS_INACTIVE("/fetch/innboks/inaktive"),
    FETCH_INNBOKS_ALL("/fetch/innboks/all"),

    FETCH_OPPGAVE("/fetch/oppgave/aktive"),
    FETCH_OPPGAVE_INACTIVE("/fetch/oppgave/inaktive"),
    FETCH_OPPGAVE_ALL("/fetch/oppgave/all"),

    PRODUCE_DONE("/produce/done"),

    IS_ALIVE("/isAlive"),
    IS_READY("/isReady"),
    SELFTEST("/internal/selftest"),
    PING("/ping"),
    METRICS("/metrics")
}

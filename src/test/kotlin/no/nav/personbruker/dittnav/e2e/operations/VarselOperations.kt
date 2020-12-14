package no.nav.personbruker.dittnav.e2e.operations

enum class VarselOperations(override val path: String) : ServiceOperation {
    GET_DOKNOTIFIKASJON_BESKJED("/doknotifikasjon/beskjed"),
    GET_DOKNOTIFIKASJON_OPPGAVE("/doknotifikasjon/oppgave"),
    GET_DOKNOTIFIKASJONSTOPP_BESKJED("/doknotifikasjonstopp/beskjed"),
    GET_DOKNOTIFIKASJONSTOPP_OPPGAVE("/doknotifikasjonstopp/oppgave"),
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    SELFTEST("/internal/selftest"),
    METRICS("/metrics")
}

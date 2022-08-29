package no.nav.tms.docker.compose.e2e.operations

enum class VarselOperations(override val path: String) : ServiceOperation {
    GET_DOKNOTIFIKASJON_BESKJED("/doknotifikasjon/beskjed"),
    GET_DOKNOTIFIKASJON_OPPGAVE("/doknotifikasjon/oppgave"),
    GET_DOKNOTIFIKASJON_INNBOKS("/doknotifikasjon/innboks"),
    GET_DOKNOTIFIKASJONSTOPP_BESKJED("/doknotifikasjonstopp/beskjed"),
    GET_DOKNOTIFIKASJONSTOPP_OPPGAVE("/doknotifikasjonstopp/oppgave"),
    GET_DOKNOTIFIKASJONSTOPP_INNBOKS("/doknotifikasjonstopp/innboks"),
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    SELFTEST("/internal/selftest"),
    METRICS("/metrics")
}

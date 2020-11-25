package no.nav.personbruker.dittnav.e2e.operations

enum class VarselOperations(override val path: String) : ServiceOperation {
    COUNT_DOKNOTIFIKASJON_BESKJED("/count/doknotifikasjon/beskjed"),
    COUNT_DOKNOTIFIKASJON_OPPGAVE("/count/doknotifikasjon/oppgave"),
    COUNT_DOKNOTIFIKASJONSTOPP_BESKJED("/count/doknotifikasjonstopp/beskjed"),
    COUNT_DOKNOTIFIKASJONSTOPP_OPPGAVE("/count/doknotifikasjonstopp/oppgave")
}

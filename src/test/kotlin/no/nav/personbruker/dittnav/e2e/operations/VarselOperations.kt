package no.nav.personbruker.dittnav.e2e.operations

enum class VarselOperations(override val path: String) : ServiceOperation {
    COUNT_DOKNOTIFIKASJON_BESKJED("/count/doknotifikasjon/beskjed"),
    COUNT_DOKNOTIFIKASJON_OPPGAVE("/count/doknotifikasjon/oppgave")
}

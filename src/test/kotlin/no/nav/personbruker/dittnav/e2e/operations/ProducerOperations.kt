package no.nav.personbruker.dittnav.e2e.operations

enum class ProducerOperations(override val path : String) : ServiceOperation {
    PRODUCE_BESKJED("/produce/beskjed"),
    PRODUCE_INNBOKS("/produce/innboks"),
    PRODUCE_OPPGAVE("/produce/beskjed"),
    PRODUCE_DONE_ALL("/produce/done/all"),
    IS_ALIVE("/isAlive"),
    IS_READY("/isReady"),
    PING("/ping")
}

package no.nav.personbruker.dittnav.e2e.oppgave

import no.nav.personbruker.dittnav.e2e.client.BrukernotifikasjonDTO

data class ProduceOppgaveDTO(
        val tekst: String,
        val link: String = "http://dummylenke.no",
        val grupperingsid: String = "123",
        val eksternVarsling: Boolean = false
) : BrukernotifikasjonDTO()

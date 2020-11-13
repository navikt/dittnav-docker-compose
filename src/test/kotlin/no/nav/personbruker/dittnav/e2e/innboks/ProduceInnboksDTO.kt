package no.nav.personbruker.dittnav.e2e.innboks

import no.nav.personbruker.dittnav.e2e.client.BrukernotifikasjonDTO

data class ProduceInnboksDTO(
        val tekst: String,
        val link: String = "http://dummylenke.no",
        val grupperingsid: String = "123"
) : BrukernotifikasjonDTO()

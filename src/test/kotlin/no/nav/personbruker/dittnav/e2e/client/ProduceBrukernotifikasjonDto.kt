package no.nav.personbruker.dittnav.e2e.client

import no.nav.personbruker.dittnav.e2e.client.ProduceDto

data class ProduceBrukernotifikasjonDto(val tekst: String, val link: String = "http://dummylenke.no"): ProduceDto()

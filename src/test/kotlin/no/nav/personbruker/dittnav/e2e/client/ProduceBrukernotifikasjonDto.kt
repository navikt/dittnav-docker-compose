package no.nav.personbruker.dittnav.e2e.client

data class ProduceBrukernotifikasjonDto(
        val tekst: String,
        val link: String = "http://dummylenke.no",
        val eksternVarsling: Boolean = false
) : ProduceDto()

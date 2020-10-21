package no.nav.personbruker.dittnav.e2e.client

data class ProduceBrukernotifikasjonDto(val tekst: String,
                                        val grupperingsid: String = "1",
                                        val link: String = "http://dummylenke.no") : ProduceDto()

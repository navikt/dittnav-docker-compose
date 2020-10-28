package no.nav.personbruker.dittnav.e2e.client

data class ProduceBrukernotifikasjonDto(val tekst: String,
                                        val grupperingsid: String = "123",
                                        val link: String = "http://dummylenke.no") : ProduceDto()

package no.nav.personbruker.dittnav.e2e.client

class ProduceStatusoppdateringDto(
        val statusIntern: String,
        val grupperingsid: String = "123",
        val link: String = "http://dummylenke.no",
        val statusGlobal: String = "SENDT",
        val sakstema: String = "sakstema"
) : ProduceDto()
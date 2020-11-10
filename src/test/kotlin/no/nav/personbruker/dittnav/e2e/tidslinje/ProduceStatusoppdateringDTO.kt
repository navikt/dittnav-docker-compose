package no.nav.personbruker.dittnav.e2e.tidslinje

import no.nav.personbruker.dittnav.e2e.client.ProduceDTO

class ProduceStatusoppdateringDTO(
        val statusIntern: String,
        val grupperingsid: String = "123",
        val link: String = "http://dummylenke.no",
        val statusGlobal: String = "SENDT",
        val sakstema: String = "sakstema"
) : ProduceDTO()

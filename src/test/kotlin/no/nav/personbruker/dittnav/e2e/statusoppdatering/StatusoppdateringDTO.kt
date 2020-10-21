package no.nav.personbruker.dittnav.e2e.statusoppdatering

import no.nav.personbruker.dittnav.e2e.tidslinje.Brukernotifikasjon
import java.time.ZonedDateTime

class StatusoppdateringDTO(
        val produsent: String?,
        val eventId: String,
        val eventTidspunkt: ZonedDateTime,
        val fodselsnummer: String,
        val grupperingsId: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val statusGlobal: String,
        val statusIntern: String?,
        val sakstema: String
)
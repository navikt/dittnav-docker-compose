package no.nav.personbruker.dittnav.e2e.beskjed

import no.nav.personbruker.dittnav.e2e.tidslinje.Brukernotifikasjon
import java.time.ZonedDateTime

data class BeskjedDTO(
        val uid: String,
        val eventTidspunkt: ZonedDateTime,
        val eventId: String,
        val tekst: String,
        val link: String,
        val produsent: String?,
        val sistOppdatert: ZonedDateTime,
        val sikkerhetsnivaa: Int
): Brukernotifikasjon

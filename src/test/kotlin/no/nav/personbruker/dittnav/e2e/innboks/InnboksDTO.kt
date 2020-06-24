package no.nav.personbruker.dittnav.e2e.innboks

import java.time.ZonedDateTime

data class InnboksDTO(
        val uid: String?,
        val eventTidspunkt: ZonedDateTime,
        val eventId: String,
        val tekst: String,
        val link: String,
        val produsent: String?,
        val sistOppdatert: ZonedDateTime,
        val sikkerhetsnivaa: Int
)

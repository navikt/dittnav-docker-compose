package no.nav.personbruker.dittnav.e2e.oppgave

import java.time.ZonedDateTime

data class OppgaveDTO(
        val uid: String,
        val eventTidspunkt: ZonedDateTime,
        val eventId: String,
        val tekst: String,
        val link: String,
        val produsent: String?,
        val sistOppdatert: ZonedDateTime,
        val sikkerhetsnivaa: Int
)

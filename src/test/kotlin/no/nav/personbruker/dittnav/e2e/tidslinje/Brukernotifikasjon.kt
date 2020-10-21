package no.nav.personbruker.dittnav.e2e.tidslinje

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Brukernotifikasjon(
        val type: String,
        val sikkerhetsnivaa: Int
)

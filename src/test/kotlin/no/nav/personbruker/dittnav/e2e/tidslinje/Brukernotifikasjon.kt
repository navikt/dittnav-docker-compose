
package no.nav.personbruker.dittnav.e2e.tidslinje

import kotlinx.serialization.Serializable

@Serializable
data class Brukernotifikasjon(
        val type: String,
        val sikkerhetsnivaa: Int
)

package no.nav.tms.docker.compose.e2e.doknotifikasjonStopp

import kotlinx.serialization.Serializable

@Serializable
data class DoknotifikasjonStoppDTO(
        val bestillingsId: String
)

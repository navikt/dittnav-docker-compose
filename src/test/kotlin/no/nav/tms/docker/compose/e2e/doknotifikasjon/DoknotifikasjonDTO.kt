package no.nav.tms.docker.compose.e2e.doknotifikasjon

import kotlinx.serialization.Serializable

@Serializable
data class DoknotifikasjonDTO(
        val bestillingsId: String
)

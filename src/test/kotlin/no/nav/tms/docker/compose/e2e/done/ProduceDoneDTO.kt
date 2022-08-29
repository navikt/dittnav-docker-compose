package no.nav.tms.docker.compose.e2e.done

import kotlinx.serialization.Serializable

@Serializable
data class ProduceDoneDTO(val eventId: String = "") : no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO()

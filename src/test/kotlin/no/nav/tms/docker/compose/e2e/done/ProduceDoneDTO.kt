package no.nav.tms.docker.compose.e2e.done

import kotlinx.serialization.Serializable
import no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO

@Serializable
data class ProduceDoneDTO(val eventId: String = "") : BrukernotifikasjonDTO()

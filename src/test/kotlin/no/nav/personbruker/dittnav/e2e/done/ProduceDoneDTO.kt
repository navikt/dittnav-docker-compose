package no.nav.personbruker.dittnav.e2e.done

import kotlinx.serialization.Serializable
import no.nav.personbruker.dittnav.e2e.client.BrukernotifikasjonDTO

@Serializable
data class ProduceDoneDTO(val uid: String = "", val eventId: String = "") : BrukernotifikasjonDTO()

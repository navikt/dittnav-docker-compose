package no.nav.personbruker.dittnav.e2e.done

import no.nav.personbruker.dittnav.e2e.client.BrukernotifikasjonDTO

data class ProduceDoneDTO(val uid: String = "", val eventId: String = "") : BrukernotifikasjonDTO()

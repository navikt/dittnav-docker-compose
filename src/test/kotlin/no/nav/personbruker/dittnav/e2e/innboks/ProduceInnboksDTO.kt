package no.nav.personbruker.dittnav.e2e.innboks

import kotlinx.serialization.Serializable
import no.nav.personbruker.dittnav.e2e.client.BrukernotifikasjonDTO

@Serializable
data class ProduceInnboksDTO(
        val tekst: String,
        val link: String = "http://dummylenke.no",
        val grupperingsid: String = "123",
        val eksternVarsling: Boolean = false,
        val prefererteKanaler: List<String> = emptyList()
) : BrukernotifikasjonDTO()

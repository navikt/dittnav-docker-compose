package no.nav.tms.docker.compose.e2e.innboks

import kotlinx.serialization.Serializable

@Serializable
data class ProduceInnboksDTO(
        val tekst: String,
        val link: String = "http://dummylenke.no",
        val grupperingsid: String = "123",
        val eksternVarsling: Boolean = false,
        val prefererteKanaler: List<String> = emptyList()
) : no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO()

package no.nav.tms.docker.compose.e2e.beskjed

import kotlinx.serialization.Serializable
import no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO

@Serializable
data class ProduceBeskjedDTO(
        val tekst: String,
        val link: String = "http://dummylenke.no",
        val grupperingsid: String = "123",
        val eksternVarsling: Boolean = false,
        val prefererteKanaler: List<String> = emptyList()
) : BrukernotifikasjonDTO()

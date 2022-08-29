@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.tms.docker.compose.e2e.beskjed

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.tms.docker.compose.e2e.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class BeskjedDTO(
        val forstBehandlet: ZonedDateTime,
        val eventId: String,
        val tekst: String,
        val link: String,
        val produsent: String?,
        val sistOppdatert: ZonedDateTime,
        val sikkerhetsnivaa: Int,
        val aktiv: Boolean,
        val grupperingsId: String
) : no.nav.tms.docker.compose.e2e.client.BrukernotifikasjonDTO()

package no.nav.personbruker.dittnav.e2e.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.features.json.*

fun buildJsonSerializer(): JacksonSerializer {
    return JacksonSerializer {
        enableDittNavJsonConfig()
    }
}

fun ObjectMapper.enableDittNavJsonConfig() {
    registerModule(JavaTimeModule())
    registerKotlinModule()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}

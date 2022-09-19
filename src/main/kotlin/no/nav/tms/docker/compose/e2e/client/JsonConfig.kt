package no.nav.tms.docker.compose.e2e.client

import kotlinx.serialization.json.Json

internal fun json(ignoreUnknownKeys: Boolean = false): Json {
    return Json {
        this.ignoreUnknownKeys = ignoreUnknownKeys
        this.encodeDefaults = true
    }
}

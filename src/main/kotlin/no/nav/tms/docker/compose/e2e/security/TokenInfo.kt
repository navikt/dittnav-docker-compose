package no.nav.tms.docker.compose.e2e.security

import kotlinx.serialization.Serializable

@Serializable
internal data class TokenInfo(
    val access_token: String,
    val expires_in: Int,
    val id_token: String,
    val scope: String,
    val token_type: String
)

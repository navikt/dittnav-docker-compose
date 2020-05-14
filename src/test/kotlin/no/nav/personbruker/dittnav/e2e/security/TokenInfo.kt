package no.nav.personbruker.dittnav.e2e.security

data class TokenInfo(
    val access_token: String,
    val expires_in: Int,
    val id_token: String,
    val scope: String,
    val token_type: String
)

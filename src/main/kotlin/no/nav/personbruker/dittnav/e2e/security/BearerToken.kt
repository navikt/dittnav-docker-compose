package no.nav.personbruker.dittnav.e2e.security

internal data class BearerToken(
        val token: String
) {

    override fun toString(): String {
        return "Bearer $token"
    }
}

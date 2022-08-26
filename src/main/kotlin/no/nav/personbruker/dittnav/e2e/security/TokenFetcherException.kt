package no.nav.personbruker.dittnav.e2e.security

internal class TokenFetcherException(msg: String, cause: Throwable) : Exception(msg, cause) {
    private val context = mutableMapOf<String, Any>()

    fun addContext(key: String, value: Any) {
        context[key] = value
    }

    override fun toString(): String {
        return "${super.toString()}, context=$context)"
    }

}

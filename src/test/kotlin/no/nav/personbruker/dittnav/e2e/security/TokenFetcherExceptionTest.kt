package no.nav.personbruker.dittnav.e2e.security

import org.amshove.kluent.`should contain all`
import org.junit.jupiter.api.Test

internal class TokenFetcherExceptionTest {

    @Test
    fun `added context should be a part of the toString method`() {
        val expetedKey1 = "key1"
        val expetedKey2 = "key2"
        val expetedValue1 = "value1"
        val expetedValue2 = "value2"

        val msg = "Simulert feil i en test"
        val exception = TokenFetcherException(msg, Exception("$msg, cause"))
        exception.addContext(expetedKey1, expetedValue1)
        exception.addContext(expetedKey2, expetedValue2)

        val toStringOfException = exception.toString()

        toStringOfException `should contain all` listOf(expetedKey1, expetedValue1, expetedKey2, expetedValue2)
    }

}

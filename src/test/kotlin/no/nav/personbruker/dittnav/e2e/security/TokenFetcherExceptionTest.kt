package no.nav.personbruker.dittnav.e2e.security

import org.amshove.kluent.`should contain all`
import org.junit.jupiter.api.Test

internal class TokenFetcherExceptionTest {

    @Test
    fun `added context should be a part of the toString method`() {
        val expectedKey1 = "key1"
        val expectedKey2 = "key2"
        val expectedValue1 = "value1"
        val expectedValue2 = "value2"

        val msg = "Simulert feil i en test"
        val exception = TokenFetcherException(
            msg,
            Exception("$msg, cause")
        )
        exception.addContext(expectedKey1, expectedValue1)
        exception.addContext(expectedKey2, expectedValue2)

        val toStringOfException = exception.toString()

        toStringOfException `should contain all` listOf(expectedKey1, expectedValue1, expectedKey2, expectedValue2)
    }

}

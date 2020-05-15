package no.nav.personbruker.dittnav.e2e.operations

enum class LegacyOperations(override val path : String) :ServiceOperation {
    SAKSOVERSIKT_PAABEGYNTE("/saker/paabegynte"),
    SAKSOVERSIKT_SAKSTEMA("/saker/sakstema"),

    PERSONALIA_NAVN("/personalia/navn"),
    PERSONALIA_IDENT("/personalia/ident"),

    OPPFOLGING("/oppfolging"),

    MIN_INNBOKS_UBEHANDLEDE("/meldinger/ubehandlede"),
    MELDEKORT_INFO("/meldekortinfo"),

    AUTH_PING("/authPing"),
    IS_ALIVE("/internal/isAlive"),
    IS_READY("/internal/isReady"),
    SELFTEST("/internal/selftest"),
    PING("/internal/ping")
}

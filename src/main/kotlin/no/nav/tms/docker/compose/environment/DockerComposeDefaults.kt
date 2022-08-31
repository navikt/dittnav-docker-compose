package no.nav.tms.docker.compose.environment

import no.nav.tms.docker.compose.environment.DockerComposeDefaults.aggregatorEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.brukernotifikasjonbestillerEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.commonEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.handlerEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.varselbestillerEnvironment

object DockerComposeDefaults {

    val commonEnvironment : Map<String, String> = mapOf(
            "CORS_ALLOWED_ORIGINS" to "localhost:9002",

            "NAIS_CLUSTER_NAME" to "dev-gcp",
            "NAIS_NAMESPACE" to "min-side",

            "EVENT_HANDLER_URL" to "http://localhost:8092",
            "INNLOGGINGSSTATUS_URL" to "http://localhost:9081/person/innloggingsstatus",

            "OIDC_ISSUER" to "http://localhost:9000",
            "OIDC_DISCOVERY_URL" to "http://localhost:9000/.well-known/openid-configuration",
            "OIDC_ACCEPTED_AUDIENCE" to "stubOidcClient",
            "LOGINSERVICE_IDPORTEN_DISCOVERY_URL" to "http://localhost:9000/.well-known/openid-configuration",
            "LOGINSERVICE_IDPORTEN_AUDIENCE" to "stubOidcClient",
            "OIDC_CLAIM_CONTAINING_THE_IDENTITY" to "pid",

            "SERVICEUSER_USERNAME" to "username",
            "SERVICEUSER_PASSWORD" to "password",
            "GROUP_ID" to "dittnav_events",

            "SENSU_HOST" to "stub",
            "SENSU_PORT" to "0",
            "PRODUCER_ALIASES" to "",
            "INFLUXDB_HOST" to "",
            "INFLUXDB_PORT" to "0",
            "INFLUXDB_DATABASE_NAME" to "",
            "INFLUXDB_USER" to "",
            "INFLUXDB_PASSWORD" to "",
            "INFLUXDB_RETENTION_POLICY" to "",

            "KAFKA_BOOTSTRAP_SERVERS" to "localhost:29092",

            "KAFKA_BROKERS" to "localhost:29092",
            "KAFKA_SCHEMAREGISTRY_SERVERS" to "http://localhost:8081",
            "KAFKA_SCHEMA_REGISTRY" to "http://localhost:8081",
            "INTERN_BESKJED_TOPIC" to "min-side.privat-brukernotifikasjon-beskjed-v1",

            "INTERN_OPPGAVE_TOPIC" to "min-side.privat-brukernotifikasjon-oppgave-v1",
            "INTERN_INNBOKS_TOPIC" to "min-side.privat-brukernotifikasjon-innboks-v1",
            "INTERN_DONE_TOPIC" to "min-side.privat-brukernotifikasjon-done-v1",
            "OPEN_INPUT_BESKJED_TOPIC" to "min-side.aapen-brukernotifikasjon-nyBeskjed-v1",

            "OPEN_INPUT_OPPGAVE_TOPIC" to "min-side.aapen-brukernotifikasjon-nyOppgave-v1",
            "OPEN_INPUT_INNBOKS_TOPIC" to "min-side.aapen-brukernotifikasjon-nyInnboks-v1",
            "OPEN_INPUT_DONE_TOPIC" to "min-side.aapen-brukernotifikasjon-done-v1",
            "INPUT_DONE_TOPIC" to "min-side.aapen-brukernotifikasjon-done-v1",

            "DOKNOTIFIKASJON_STATUS_TOPIC" to "teamdokumenthandtering.aapen-dok-notifikasjon-status",

            "DB_HOST" to "localhost:5432",
            "DB_PORT" to "5432",
            "DB_USERNAME" to "testuser",
            "DB_PASSWORD" to "testpassword",

            "AZURE_APP_JWK" to """{"p":"9IzYnqdnQlSXw8jqcK5XVU39j7JAcnaZgzXAQGLp7kDp3RS3vT_-pikWZfpC7y8IWybPpZ-aaPIonMOwR1exf20eM5RRl_cJmlqIZ0rzDNfhrO7yNyOvqYsNyrgC2IVmTl5uAGU8T6GTPGm7e-9AZB8jJHP107nrUBbkf6Q8U00","kty":"RSA","q":"hlzRg6ve3QGqPzEqw7ft5zxTcdNDulMTcC5_aHMfqJPjfFoxZjwzeIPR2uDv-HCJUCi23S1lmLooAJofNxNdL51PPaTD3Wm27zl7f7xcJPX-nx-anm4hpq0YcutM57ES0KB4x1D3Z94vR4LdCtzrINSJX5X9XRSgxyGc-DnJCpM","d":"JhKqAkIIAKO7wuQM94h5nYIiwdNX6V4bBUXMNm0bVM6j1Y4oBbANHcBgf9iEQ3HnR3vLO3N1k-_C1QBKjavj3lsPJZaXYQIUneWx0V0P7htmAM-R5R-iI_fnpAfiJ_cvfN0q1c1vkafj9nlnOLBkR_DHOFAXjq65IWQrzbEwQLn80ydZW7jc7g-mVYyI7JPWbWkaqGScj9pQ1PPsEb555GMT8vCH_mNoRE3IZCPzhkbkBGEshJc3TiUdU-z221RgLpTJ6zpAWhaoLu7eSyyM0pW9rJZajgf0QLfJUthkP1XHAsucCrW9rclCFHT6GHlr3_1b8LLf9JUeN74GBD2KgQ","e":"AQAB","use":"sig","kid":"KID","qi":"sDdnHRSTjeuWsKFWEZVkBVNDoeQ1K-AcWJpLOBGZju96bahAwtdp99AnCM5Ryo2wnlGER3vl-hTv-4_a-7T-mrbXV0DH1R42sqsuK0ldQB-TGzzmR3Zw572idEr2b22fmFiGstWAMxxFwySUT2GhMMjpbw0YDq0dvjixFe0HLuw","dp":"bqz_rG3e0aovNTBMycpdRAIT62CkwijiuPsukvUxId7G-INT1JaNzO1zcZudh1ol0frypuZgn3ZY4vm9YcxSTHU54eYSN1BaTD0E1pqY_QsG25kYjdPcwEJqb4Bv6UYWUwlxh0RJ01Awrnq7mw9geO-OnATPEI9n68swhoPxuVE","alg":"RS256","dq":"NKNqZWZRAk5jrywI7_I_3sZwfnhg4T8or1tYCMiJT5bYmWAQWFgfZghmj5RuDuuvr9qzgUUGt2W-xDWdIwuQB1Zes-y32ydORrxDWNCnXMeOwDI2dfj5b_4CRQtP7rkdgORNTPC4bFXMOfzXsijIaO07AFqXjv7E2h6eC1VAIMc","n":"gFpkJam_j6QWa-Aut5dMJC8zQI3t1iZntWxRs6OGqYilqkV_vAzjvMD6UGUwnlEMw_2301CAM-cEEOWoHIeAB5IxT-5C5E_t4Sm2-u2vqOtn0zHCv4vKG7vi3rQE2BcQfOabUx_i6Ve5mtRa1s2aV4HHpvhpwUtSmILOuIiBmwE8t_6UJL0SJ3AAhFbsP-Fnihl8zO8QnOMcDAc0msJQtLxkeAefkq4ZTHyK1k35qt-NiisUQmirkUxXUpF6vB0ptPLtU0P3A-0GYs3tRWXYPthLPqSG02DUcTJiBK1lFOSpU9kRwCDdQr-QU0yn2AP3EHNc0rsnNn0q_DI3NlnXNw"}""",
            "AZURE_APP_TENANT_ID" to "auth-mock",
            "AZURE_OPENID_CONFIG_ISSUER" to "http://localhost:9051",
            "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT" to "http://localhost:9051/azure/token",
            "AZURE_APP_WELL_KNOWN_URL" to "http://localhost:9051/azure/.well-known/oauth-authorization-server",

            "TOKEN_X_PRIVATE_JWK" to """{"p":"5ihR4ZMespGN6LAOrOEbQ8k06p-P0s16R-mBTbDIS7UcCkd8uOh2x0tDcm1fjYKZI82rSCZi0VOFCuX3w-9KEqEivrCZLu8f33JDe1dNC8LnmVi13Ov2CFwvpmGoHnt-SEo3L_1VFzPmcly77WlierecbjP-faQNqNvm35eK9Rs","kty":"RSA","q":"kqQMgE0D9fKFQLBGLC9HkW61fwm30Bq07PzJ2Rnv5WCBIRYkRcAESYCSbWyZzdE8WMAnSX8bt446UQwV-fcRCd-rz6cbZy_h4lJK8mJnAUhUF8RK0ZkuuezLzzfuih9Ol0KGyXLUK02O02k_b0R8QiWlDlk9-npbR_cXzWWDQI0","d":"M2r7amAlJaF1__5ACvP-XDb1_bqPyj9clriGNVgkXMcAWAUzUcI6NL5Lezx4pYZHegA19F25iqFDQIfSjJfVInQ5Zk4RCa4FiluYjZy_OFCpPgflG-udtyfufxfCvva7C0byAslo5UQ6J8SxyeYB2hH5WNGPuMlXu_Vpwf_E0wXVkPCryG6FtPXX4KNfqDrnZH1H3frEiwLqK5JkZAUJGl3LClsvm9zku9a_nol2xbF1qwouIfIHXrbUH1xKWIJYuMlCxDhylUgA7ZeW5GQK9Qj_Vi4ioca-iLgbzKDwHqnKKQVuMMV8lgsw3CYGDSMcDjlW8DcDhbyA_24SJ9dHQQ","e":"AQAB","use":"sig","kid":"KID","qi":"QLnGLFZu27Y8me2V8DBcf9vrtLkm8e4h_ET-K4oatMWwZI1yQxitHnCZTmMBwg9fzjKIFWbGP9vaH9RepayrezblaKz11Os5w0VZRkLusWunEJKE7vFupSKoRSwGsIN9JkQga9HrSw99sgADbdNPgGICRz-KEO4YDXsZsFT6XQM","dp":"ghY0AZ2X8jIW1Xf9M3WgZXXqNNqu_eUpwk-WyAStkSzyhlTVh-XMjORbYOe05FT2naf3sbYQkGP2hKxqmBI9-b2d184VJtP9XdxFjAdSAt3M7X2YfR_ZVexU-mOkFGaUf-OCwdscJxssm2MSzSONh_MTta4pn3YESnbQTXxoDBU","alg":"RS256","dq":"V7FzKGbmL3bQ6VaBIHtFuVsTft0452KRQAgMTaCNLgcAMeEj3_JsnegdqQKBF-xAwnJnDDTPRn3vsfggTFPMBLOV86Bjatnfo5DC8SCXqNSwNWZVt_J7VwSc_VbdeAww25QVNvNMnnjLKPKP1OKHzKbmhiYoQlpZrDfALDzAuSE","n":"g9Z7xFem9WjXEhIb0MQ9YMOwFP-TE_FTT52TVDHvYhtfvWQwoCVMRNUioSGcgSaroYd9wyRGmAaMjrvYJFIQxOGXTTw_YGGVrXuAiG0ZSkZdkrh6yj5GDqOsGWHZ7YGAnv0CGcEje3GxHeALrybRcrKNm_PoEOrFpE7mg6k2bth0I4b5FTOLyS2mwcvWNzE7SrY2-YL0SrujTASkF35khNEQhnmXxJHTv4fyflJZUN8inlOpkKnQwvYc8vBu3UTEzCT3cQ6imRA92462wmLgqlJANWpf_xugtQ68E9OucyhN6QrOeReGkT2TFmqZC4xNBdpqx4v9p6omYyF3SKC_3w"}""",
            "TOKEN_X_WELL_KNOWN_URL" to "http://localhost:9051/tokendings/.well-known/oauth-authorization-server",
            "TOKEN_X_WELL_KNOWN_URL" to "http://localhost:9051/tokendings/.well-known/oauth-authorization-server"
    )

    val aggregatorEnvironment = mapOf(
            "DB_DATABASE" to "brukernotifikasjon-cache",
            "RAPID_ENABLED" to "false",
            "RAPID_TOPIC" to "min-side.brukervarsel-v1",
            "GROUP_ID_DOKNOTIFIKASJON_STATUS" to "tms-1",
            "ARCHIVING_ENABLED" to "true",
            "ARCHIVING_THRESHOLD" to "365"
    )

    val handlerEnvironment = mapOf(
            "DB_EVENTHANDLER_DATABASE" to "brukernotifikasjon-cache",
            "DB_EVENTHANDLER_HOST" to "localhost:5432",
            "DB_EVENTHANDLER_USERNAME" to "testuser",
            "DB_EVENTHANDLER_PASSWORD" to "testpassword",
            "DB_EVENTHANDLER_PORT" to "5432",

            "TOKEN_X_CLIENT_ID" to "dittnav-event-handler-clientid",
            "AZURE_APP_CLIENT_ID" to "dittnav-event-handler-clientid"
    )

    val brukernotifikasjonbestillerEnvironment = mapOf(
            "DB_DATABASE" to "brukernotifikasjonbestiller",
            "FEILRESPONS_TOPIC" to "min-side.aapen-brukernotifikasjon-feilrespons-v1"
    )

    val varselbestillerEnvironment = mapOf(
            "DB_DATABASE" to "dittnav-varselbestiller"
    )
}

class CommonConfig {
    companion object COMMON: DockerComposeAppConfig {
        override fun getAppName() = "generic environment"

        override fun getEnvironment() = commonEnvironment
    }
}

class AggregatorConfig {
    companion object AGGREGATOR: DockerComposeAppConfig {
        override fun getAppName() = "dittnav-event-aggregator"

        override fun getEnvironment() = commonEnvironment + aggregatorEnvironment
    }
}

class HandlerConfig {
    companion object HANDLER: DockerComposeAppConfig {
        override fun getAppName() = "dittnav-event-handler"

        override fun getEnvironment() = commonEnvironment + handlerEnvironment
    }
}

class BrukernotifikasjonbestillerConfig {
    companion object BRUKERNOTIFIKASJONBESTILLER: DockerComposeAppConfig {
        override fun getAppName() = "dittnav-brukernotifikasjonbestiller"

        override fun getEnvironment() = commonEnvironment + brukernotifikasjonbestillerEnvironment
    }
}

class VarselbestillerConfig {
    companion object VARSELBESTILLER: DockerComposeAppConfig {
        override fun getAppName() = "dittnav-varselbestiller"

        override fun getEnvironment() = commonEnvironment + varselbestillerEnvironment
    }
}

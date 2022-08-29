package no.nav.tms.docker.compose.environment

import no.nav.tms.docker.compose.environment.DockerComposeDefaults.aggregatorEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.brukernotifikasjonbestillerEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.commonEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.handlerEnvironment
import no.nav.tms.docker.compose.environment.DockerComposeDefaults.kafkaEnvironment
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

            "DB_HOST" to "localhost:5432",
            "DB_PORT" to "5432",
            "DB_USERNAME" to "testuser",
            "DB_PASSWORD" to "testpassword",
            "DB_MOUNT_PATH" to "notUsedOnLocalhost",

            "SENSU_HOST" to "stub",
            "SENSU_PORT" to "0",
            "PRODUCER_ALIASES" to "",
            "INFLUXDB_HOST" to "",
            "INFLUXDB_PORT" to "0",
            "INFLUXDB_DATABASE_NAME" to "",
            "INFLUXDB_USER" to "",
            "INFLUXDB_PASSWORD" to "",
            "INFLUXDB_RETENTION_POLICY" to ""
    )

    val kafkaEnvironment = mapOf(
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

            "DOKNOTIFIKASJON_STATUS_TOPIC" to "teamdokumenthandtering.aapen-dok-notifikasjon-status"
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
            "DB_DATABASE" to "brukernotifikasjon-cache"
    )

    val brukernotifikasjonbestillerEnvironment = mapOf(
            "DB_DATABASE" to "brukernotifikasjonbestiller"
    )

    val varselbestillerEnvironment = mapOf(
            "DB_DATABASE" to "dittnav-varselbestiller"
    )
}

class CommonConfig {
    companion object COMMON: DockerComposeAppConfig {
        override fun getEnvironment() = commonEnvironment
    }
}

class AggregatorConfig {
    companion object AGGREGATOR: DockerComposeAppConfig {
        override fun getEnvironment() = commonEnvironment + kafkaEnvironment + aggregatorEnvironment
    }
}

class HandlerConfig {
    companion object HANDLER: DockerComposeAppConfig {
        override fun getEnvironment() = commonEnvironment + kafkaEnvironment + handlerEnvironment
    }
}

class BrukernotifikasjonbestillerConfig {
    companion object BRUKERNOTIFIKASJONBESTILLER: DockerComposeAppConfig {
        override fun getEnvironment() = commonEnvironment + kafkaEnvironment + brukernotifikasjonbestillerEnvironment
    }
}

class VarselbestillerConfig {
    companion object VARSELBESTILLER: DockerComposeAppConfig {
        override fun getEnvironment() = commonEnvironment + kafkaEnvironment + varselbestillerEnvironment
    }
}

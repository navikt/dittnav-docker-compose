package no.nav.tms.docker.compose.environment

interface DockerComposeAppConfig {
    fun getAppName(): String
    fun getEnvironment(): Map<String, String>
}



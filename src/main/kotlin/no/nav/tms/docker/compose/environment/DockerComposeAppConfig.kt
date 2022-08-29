package no.nav.tms.docker.compose.environment

interface DockerComposeAppConfig {
    fun getEnvironment(): Map<String, String>
}



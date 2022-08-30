package no.nav.tms.docker.compose.environment

import org.gradle.api.tasks.JavaExec

fun JavaExec.setupEnvironment(dockerComposeAppConfig: DockerComposeAppConfig) {
    println("Setting predefined environment variables for ${dockerComposeAppConfig.getAppName()}...")

    dockerComposeAppConfig.getEnvironment().forEach { (name, value) ->
        println(" - $name=$value")
        environment(name, value)
    }
}

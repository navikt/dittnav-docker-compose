package no.nav.tms.docker.compose.environment

import org.gradle.api.tasks.JavaExec

fun JavaExec.setupEnvironment(dockerComposeAppConfig: DockerComposeAppConfig) {
    println("Setting predefined environment variables for ${dockerComposeAppConfig.getAppName()}...")

    dockerComposeAppConfig.getEnvironment().forEach { (name, value) ->
        println("$name=$value")
        environment(name, value)
    }
}

fun JavaExec.setEnvVars(envMap: Map<String, String>) {
    envMap.forEach { (name, value) ->
        println("$name=$value")
        environment(name, value)
    }
}

fun JavaExec.setEnvVars(vararg envMappings: Map<String, String>) {
    envMappings.forEach { envMap ->
        envMap.forEach { (name, value) ->
            println("$name=$value")
            environment(name, value)
        }
    }
}

fun JavaExec.setEnvVars(vararg envMappings: Pair<String, String>) {
    envMappings.forEach { (name, value) ->
        println("$name=$value")
        environment(name, value)
    }
}

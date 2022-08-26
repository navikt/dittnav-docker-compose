import org.gradle.api.tasks.JavaExec

fun JavaExec.setEnvVars(dockerComposeAppConfig: DockerComposeAppConfig) {
    dockerComposeAppConfig.getEnvironment().forEach { (name, value) ->
        environment(name, value)
    }
}

fun JavaExec.setEnvVars(envMap: Map<String, String>) {
    envMap.forEach { (name, value) ->
        environment(name, value)
    }
}

fun JavaExec.setEnvVars(vararg envMappings: Map<String, String>) {
    envMappings.forEach { envMap ->
        envMap.forEach { (name, value) ->
            environment(name, value)
        }
    }
}

fun JavaExec.setEnvVars(vararg envMappings: Pair<String, String>) {
    envMappings.forEach { (name, value) ->
        environment(name, value)
    }
}

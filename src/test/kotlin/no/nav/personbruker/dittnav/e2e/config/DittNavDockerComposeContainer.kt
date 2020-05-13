package no.nav.personbruker.dittnav.e2e.config

import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.net.URL

class DittNavDockerComposeContainer(composeFiles: File) : DockerComposeContainer<DittNavDockerComposeContainer>(composeFiles) {

    private val serviceBaseUrls = mutableMapOf<ServiceConfiguration, URL>()

    fun withExposedService(service: ServiceConfiguration) {
        withExposedService(service.dockerComposeName, service.exposedPort)
    }

    fun getBaseUrl(service: ServiceConfiguration): URL {
        return serviceBaseUrls.getOrPut(service) {
            fetchCompleteBaseUrlForService(service)
        }
    }

    private fun fetchCompleteBaseUrlForService(service: ServiceConfiguration): URL {
        val actualHostName = getServiceHost(service.dockerComposeName, service.exposedPort)
        val actualServicePort = getServicePort(service.dockerComposeName, service.exposedPort)

        return if (isServiceWithoutContextPath(service)) {
            URL("http://$actualHostName:$actualServicePort")

        } else {
            URL("http://$actualHostName:$actualServicePort/${service.contextPath}")
        }
    }

    private fun isServiceWithoutContextPath(service: ServiceConfiguration) =
        service.contextPath.isBlank()

}

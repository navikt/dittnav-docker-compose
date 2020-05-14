package no.nav.personbruker.dittnav.e2e.config

import java.io.File

/**
 * Singleton som sørger for at DittNAV sin docker-compose context kun startes en gang, slik at alle tester kan
 * gjenbruke den samme context-en.
 */
object DittNavDockerComposeCommonContext {

    val instance by lazy { startDittNavDockerCompose() }

    private fun startDittNavDockerCompose() = DittNavDockerComposeContainer(
        commentOutContainerNameFieldFromComposeFile("docker-compose.yml")

    ).apply {
        withPull(true)
        ServiceConfiguration.personbrukerServices().forEach { service ->
            withExposedService(service)
        }
        start()
    }

    /**
     * Test Containers har for tiden ikke støtte for feltet container_name, og dette feltet må fjernes for å kunne
     * bruke Test Containers. Ønsker ikke å fjerne dette feltet permanent fra docker-compose-filen, fordi det er dette
     * feltet som gjør at vi kan bruke kortnavn på service-ene. Mao at vi ikke må forholde oss til veldig lange
     * servicenavn, f.eks. i stede for "<docker-nettverksnavn>-<servicenavn>_X" kan vi bruke "servicenavn".
     *
     * Ref: https://github.com/testcontainers/testcontainers-java/issues/2472
     */
    fun commentOutContainerNameFieldFromComposeFile(dockerComposeFileName: String): File {
        val composeConfig = File(dockerComposeFileName).readText()
        val composeConfigWithDeactivatedContainerName = composeConfig.replace("container_name:", "#container_name:")
        val temporaryFile = createTempFile(suffix = ".yml", directory = File("."))
        temporaryFile.bufferedWriter().use { out -> out.write(composeConfigWithDeactivatedContainerName) }
        temporaryFile.deleteOnExit()
        return temporaryFile
    }

}

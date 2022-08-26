plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-library`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    mavenCentral()
}

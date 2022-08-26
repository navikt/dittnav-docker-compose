import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "no.nav.personbruker.dittnav"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api("dev.gradleplugins:gradle-api:6.8.3")
}


publishing {
    repositories{
        mavenLocal()
    }

    publications {
        create<MavenPublication>("local") {
            from(components["java"])
        }
    }
}

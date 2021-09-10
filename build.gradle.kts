plugins {
    kotlin("jvm") version Kotlin.version
}

group = "no.nav.personbruker.dittnav"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(Auth0.javajwt)
    implementation(Jackson.dataTypeJsr310)
    implementation(Ktor.clientApache)
    implementation(Ktor.clientJackson) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(Ktor.clientJson)
    implementation(Ktor.clientLogging)
    implementation(Ktor.clientLoggingJvm)
    implementation(Logback.classic)

    testImplementation(Awaitility.awaitilityKotlin)
    testImplementation(Junit.api)
    testImplementation(Junit.params)
    testImplementation(Kluent.kluent)
    testImplementation(Mockk.mockk)
    testImplementation(TestContainers.junitJupiter)
    testImplementation(TestContainers.testContainers) {
        exclude("junit", "junit")
    }

    testRuntimeOnly(Junit.engine)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "13"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "13"
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
}

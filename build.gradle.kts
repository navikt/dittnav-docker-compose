plugins {
    `maven-publish`
    kotlin("jvm") version Kotlin.version
    kotlin("plugin.serialization") version Kotlin.version
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Auth0.javajwt)
    implementation(Ktor.clientApache)
    implementation(Ktor.clientJson)
    implementation(Ktor.clientLogging)
    implementation(Ktor.clientLoggingJvm)
    implementation(Ktor.clientSerializationJvm)
    implementation(Ktor.serialization)

    api("dev.gradleplugins:gradle-api:7.5")

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
        kotlinOptions.jvmTarget = "17"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
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

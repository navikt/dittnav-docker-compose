val junitVersion = "5.6.2"
val testContainersVersion = "1.14.1"
val ktorVersion = "1.3.1"
val logbackVersion = "1.2.3"
val javaJwtVersion = "3.10.3"
val jacksonVersion = "2.10.4"


plugins {
    kotlin("jvm") version "1.3.72"
}

group = "no.nav.personbruker.dittnav"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(Logback.classic)

    implementation(Ktor.clientApache)
    implementation(Ktor.clientLogging)
    implementation(Ktor.clientLoggingJvm)
    implementation(Ktor.clientJson)
    implementation(Ktor.clientJackson)

    implementation(Jackson.dataTypeJsr310)




    implementation("com.auth0:java-jwt:$javaJwtVersion")

    testImplementation(Junit.api)
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly(Junit.engine)

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion") {
        exclude("junit", "junit")
    }
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")

    testImplementation(Kluent.kluent)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
}

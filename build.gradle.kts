val junitVersion = "5.6.2"
val kluentVersion = "1.59"
val testContainersVersion = "1.14.1"
val ktorVersion = "1.3.1"
val logbackVersion = "1.2.3"
val javaJwtVersion = "3.10.3"

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

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.slf4j:slf4j-api:1.7.30")

    implementation("io.ktor:ktor-client-apache:$ktorVersion") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")

    implementation("com.auth0:java-jwt:$javaJwtVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion") {
        exclude("junit", "junit")
    }
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
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

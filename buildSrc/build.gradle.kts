plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    maven("https://jitpack.io")
}

val dittNavDependenciesVersion = "2020.10.07-15.47-b9026ff285ab"

dependencies {
    implementation("com.github.navikt:dittnav-dependencies:$dittNavDependenciesVersion")
}

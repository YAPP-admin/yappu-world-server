plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":api"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
    implementation(project(":domain"))
    implementation(project(":common"))

    developmentOnly(libs.spring.boot.docker.compose)

    // apm
    implementation(libs.spring.boot.starter.actuator)
    runtimeOnly(libs.micrometer.prometheus)

    // logging
    implementation(libs.loki.logback.appender)

    // test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotest.extensions.spring)
}

tasks {
    bootJar {
        archiveBaseName = "yappu-world"

        val profile = System.getProperty("spring.profiles.active")
        archiveFileName = "yappu-world-$profile.jar"

        enabled = false
    }
}

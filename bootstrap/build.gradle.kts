plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
    implementation(project(":domain"))
    implementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter")
    runtimeOnly("org.springframework.boot:spring-boot-docker-compose")

    // apm
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // logging
    implementation("com.github.loki4j:loki-logback-appender:2.0.1")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showExceptions = true
            showCauses = true
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
    bootJar {
        archiveBaseName = "yappu-world"

        val profile = System.getProperty("spring.profiles.active")
        archiveFileName = "yappu-world-$profile.jar"

        enabled = false
    }
}

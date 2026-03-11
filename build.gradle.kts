plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.sentry)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    group = "co"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    runtimeOnly(libs.spring.boot.docker.compose)

    runtimeOnly(libs.bundles.db.drivers)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.bundles.kotlin.jdsl)
    implementation(libs.p6spy)

    // ulid
    implementation(libs.ulid.creator)

    // test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest)
    implementation(libs.kotest.extensions.spring)

    // logging
    implementation(libs.kotlin.logging)
    implementation(libs.loki.logback.appender)

    // swagger
    implementation(libs.springdoc.openapi.webmvc)

    // jwt
    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt.runtime)

    // fcm
    implementation(libs.firebase)

    // apm
    implementation(libs.spring.boot.starter.actuator)
    runtimeOnly(libs.micrometer.prometheus)

    // flyway
    implementation(libs.bundles.flyway)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

ktlint {
    version.set("1.5.0")
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
    }
    sentry {
        debug = true
        org = "yapp-co"
        projectName = "yappu-world-server"
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${rootProject.libs.versions.spring.boot.get()}")
        }
    }

    dependencies {
        implementation(rootProject.libs.kotlin.logging)

        // test
        testImplementation(rootProject.libs.kotlin.test.junit5)
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)
        testImplementation(rootProject.libs.mockk)
        testImplementation(rootProject.libs.bundles.kotest)
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
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    ktlint {
        version.set("1.5.0")
    }
}

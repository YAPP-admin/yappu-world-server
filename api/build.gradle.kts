plugins {
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(project(":application"))
    implementation(project(":common"))

    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)

    // swagger
    implementation(libs.springdoc.openapi.webmvc)

    // test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotest.extensions.spring)
}

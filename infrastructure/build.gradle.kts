plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    implementation(project(":domain"))

    runtimeOnly(libs.bundles.db.drivers)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.bundles.kotlin.jdsl)
    implementation(libs.p6spy)

    // ulid
    implementation(libs.ulid.creator)

    // fcm
    implementation(libs.firebase)

    // flyway
    implementation(libs.bundles.flyway)

    // test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotest.extensions.spring)
}

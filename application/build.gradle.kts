plugins {
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))

    implementation(libs.spring.context)
    implementation(libs.spring.tx)

    // test
    testImplementation(libs.spring.test)
}

plugins {
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(libs.spring.context)

    // jwt
    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt.runtime)

    // test
    testImplementation(libs.spring.test)
}

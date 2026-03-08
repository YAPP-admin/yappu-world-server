plugins {
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")

    // test
    testImplementation("org.springframework:spring-test")
}

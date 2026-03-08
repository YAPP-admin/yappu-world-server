plugins {
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework:spring-context")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // test
    testImplementation("org.springframework:spring-test")
}

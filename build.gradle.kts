plugins {
    id("java")
    id("org.springframework.boot") version "3.2.5"
}
apply(plugin = "io.spring.dependency-management")

group = "com.stormerg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}
dependencies {
    // Firebase Admin SDK
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.guava:guava:33.1.0-jre") // patching CVE-2023-2976

    // Discord bot
    implementation("net.dv8tion:JDA:5.0.0-beta.23")

    // Spring application
    implementation("org.springframework.boot:spring-boot-starter-web") // Essential for REST API

    // Unit tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
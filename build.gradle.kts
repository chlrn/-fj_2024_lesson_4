plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    application

    id("com.github.ben-manes.versions") version "0.46.0"
}


group = "com.kudago"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-core:2.2.0")
    implementation("io.ktor:ktor-client-cio:2.2.0") // HTTP клиент
    implementation("io.ktor:ktor-client-content-negotiation:2.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.0")
    implementation("io.ktor:ktor-client-logging:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:atomicfu:0.25.0") // AtomicFU
// https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.18.1")
}

application {
    mainClass.set("MainKt")
}

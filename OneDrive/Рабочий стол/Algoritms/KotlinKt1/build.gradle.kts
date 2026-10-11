plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    application
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.12"

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.5.6")
}

// ВАЖНО: Настройки совместимости JVM для Kotlin
kotlin {
    jvmToolchain(21) // Используем JVM 21 для Kotlin
}

// ВАЖНО: Настройки совместимости JVM для Java
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    // Если ты делал "Вариант Б" (без пакетов), замени на "ApplicationKt"
    mainClass.set("com.example.ApplicationKt")
}
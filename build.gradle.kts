plugins {
    kotlin("jvm") version "1.6.0"
}

group = "com.karrot.commerce"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // kotlin
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk9:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.5.2")
    implementation("io.smallrye.reactive:mutiny-kotlin:1.2.0")

    // reactor
    implementation("io.projectreactor:reactor-core:3.4.12")
    implementation("io.projectreactor.addons:reactor-adapter:3.4.5")

    // rxjava
    implementation("io.reactivex.rxjava3:rxjava:3.1.3")

    // mutiny
    implementation("io.smallrye.reactive:mutiny:1.2.0")
    implementation("io.smallrye.reactive:mutiny-reactor:1.2.0")

    // faker
    implementation("com.github.javafaker:javafaker:0.15")
}

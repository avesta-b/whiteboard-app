/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "cs346.whiteboard"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.3.0")
    implementation("io.ktor:ktor-client-core:2.2.3")
    implementation("io.ktor:ktor-client-okhttp:2.2.3")
    implementation("org.hildan.krossbow:krossbow-stomp-core:5.1.0")
    implementation("org.hildan.krossbow:krossbow-websocket-okhttp:5.1.0")
    implementation("org.hildan.krossbow:krossbow-stomp-kxserialization-json:5.1.0")
    implementation("org.slf4j:slf4j-simple:1.7.9")
    implementation("com.alialbaali.kamel:kamel-image:0.4.0")
    implementation(project(":shared"))
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "cs346.whiteboard.client.MainKt"
        val iconsRoot = project.file("src/main/resources")
        nativeDistributions {
            packageVersion = "1.3.0"
            packageName = "Whiteboard"
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            macOS {
                iconFile.set(iconsRoot.resolve("icon.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("icon.ico"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

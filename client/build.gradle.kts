import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "cs346.whiteboard"
version = "1.0-SNAPSHOT"

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
    implementation("org.hildan.krossbow:krossbow-stomp-core:5.0.0")
    implementation("org.hildan.krossbow:krossbow-websocket-ktor:5.0.0")
    implementation("org.hildan.krossbow:krossbow-stomp-kxserialization-json:5.0.0")
    implementation("org.slf4j:slf4j-simple:1.7.9")
    implementation(project(":shared"))
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "cs346.whiteboard.client.MainKt"
        val iconsRoot = project.file("src/main/resources")
        nativeDistributions {
            packageVersion = "1.0.0"
            packageName = "Whiteboard"
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            macOS {
                iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windows.ico"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("launcher_icons/linux.png"))
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

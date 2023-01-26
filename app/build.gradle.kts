/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("cs346.whiteboard.kotlin-application-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text")
    implementation(project(":utilities"))
}

application {
    // Define the main class for the application.
    mainClass.set("cs346.whiteboard.app.AppKt")
}
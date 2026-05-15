import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.changelog")
    id("org.jetbrains.intellij.platform")
}

tasks.named<Zip>("buildPlugin") {
    destinationDirectory.set(file("${System.getProperty("user.home")}/Desktop"))
}

dependencies {
    testImplementation(libs.junit)
    intellijPlatform {
        intellijIdea("2025.3.5")
        testFramework(TestFrameworkType.Platform)
    }
}
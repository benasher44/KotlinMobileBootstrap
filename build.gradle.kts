import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.3.61"
    id("maven-publish")
}
repositories {
    mavenCentral()
}
group = "com.example"
version = "0.0.1"

kotlin {
    jvm()
    ios()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

if (HostManager.hostIsMac) {
    val iosTest = tasks.register("iosTest", Exec::class) {
        val iosX64 = kotlin.iosX64()
        val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"
        dependsOn(iosX64.binaries.getTest("DEBUG").linkTaskName)
        group = JavaBasePlugin.VERIFICATION_GROUP
        description = """Runs tests for target "ios" on an iOS simulator"""
        executable = "xcrun"
        setArgs(
            listOf(
                "simctl",
                "spawn",
                "-s",
                device,
                iosX64.binaries.getTest("DEBUG").outputFile
            )
        )
    }

    val checkTask = tasks.named("check")
    checkTask.configure {
        dependsOn(iosTest)
    }
}

import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.3.61"
    id("maven-publish")
    id("com.android.library") version "3.5.3"
}
repositories {
    mavenCentral()
}
group = "com.example"
version = "0.0.1"

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }
    ios()

    /**
     * Other Apple Options– un-comment each to enable
     */
    //watchos()
    //iosArm32()
    //tvos()

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
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

// Enough settings to gradle sync, but more can be added
android {
    compileSdkVersion = "29"
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

import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform") version "1.4.0"
    id("maven-publish")
    id("com.android.library") version "4.0.1"
}
repositories {
    mavenCentral()
}
group = "com.example"
version = "0.0.1"

kotlin {
    android {
        publishLibraryVariants("release", "debug")
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    ios {
        binaries {
            framework()
        }
    }

    /**
     * Other Apple Options– un-comment each to enable
     *
     * each target will need its own `binaries { framework() }` configuration
     */
    //watchos()
    //iosArm32()
    //tvos()

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test-multiplatform"))
            }
        }
        val androidTest by getting {
            dependencies {
                // https://youtrack.jetbrains.com/issue/KT-41097
                implementation(kotlin("test-junit"))
            }
        }
    }
}

tasks.register("debugFatFramework", FatFrameworkTask::class) {
    baseName = project.name

    // Framework is output here
    destinationDir = buildDir.resolve("fat-framework/debug")

    val targets = mutableListOf(
        kotlin.iosX64(),
        kotlin.iosArm64()
    )

    /**
     * Un-comment/modify to include additional targets
     *
     * targets.add(kotlin.iosArm32())
     */

    // Specify the frameworks to be merged.
    from(targets.map { it.binaries.getFramework(NativeBuildType.DEBUG) })
}

// Enough settings to gradle sync, but more can be added
android {
    compileSdkVersion(29)
}

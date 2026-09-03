plugins {
    kotlin("multiplatform")
    id("com.bevansbench.kotlin-quality")
}

group = "com.framesmith.media.paint.builtins"
version = "0.1.0-SNAPSHOT"

kotlin {
    jvm()
    linuxX64()
    jvmToolchain(17)

    sourceSets {
        commonMain.dependencies {
            api("com.framesmith.media.paint:kotlin:0.1.0-SNAPSHOT")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

bevansBenchKotlinQuality {
    toolkitDirectory.set(rootProject.layout.projectDirectory.dir("vendor/bevans-bench-kotlin-quality"))
}

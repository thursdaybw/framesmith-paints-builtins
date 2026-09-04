pluginManagement {
    includeBuild("vendor/bevans-bench-kotlin-quality") {
        name = "framesmith-paints-builtins-quality"
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "framesmith-paints-builtins"

if (gradle.parent == null) {
    includeBuild("vendor/bevans-bench-kotlin-quality") {
        name = "framesmith-paints-builtins-quality"
    }
}
includeBuild("../framesmith-paint-core")
include(":kotlin")

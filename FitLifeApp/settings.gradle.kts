pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // 🔥 NECESARIO PARA MPAndroidChart
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FitLifeApp"
include(":app")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }
}

rootProject.name = "kobweb-site"

include(":site")
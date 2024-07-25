import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.script

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

group = "com.varabyte.kobweb.site"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            head.add {
                link {
                    rel = "stylesheet"
                    href = "/highlight.js/styles/dracula.css"
                }
                script {
                    src = "/highlight.js/highlight.min.js"
                }
            }
        }
    }
}

kotlin {
    configAsKobwebApplication("kobweb-site")

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
        }
    }
}

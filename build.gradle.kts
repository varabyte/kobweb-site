import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
}

group = "com.varabyte.kobweb.site"
version = "1.0-SNAPSHOT"

kobweb {
    index {
        head.add {
            link {
                rel = "stylesheet"
                href = "/highlight.js/styles/dracula.css"
            }
            script {
                src = "/highlight.js/highlight.min.js"
            }
            script {
                unsafe {
                    raw("hljs.highlightAll()")
                }
            }
        }
    }
}

kotlin {
// Consider re-enabling JVM support if we add API routes
//    jvm {
//        tasks.named("jvmJar", Jar::class.java).configure {
//            archiveFileName.set("kobweb-site.jar")
//        }
//    }
    js(IR) {
        moduleName = "kobweb-site"
        browser {
            commonWebpackConfig {
                outputFileName = "kobweb-site.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk.core)
                implementation(libs.kobweb.silk.icons.fa)
             }
        }

// Consider re-enabling JVM support if we add API routes
//        val jvmMain by getting {
//            dependencies {
//                implementation(libs.kobweb.api)
//             }
//        }
    }
}
---
follows: CreatingAndRunningAKobwebProject
---

When you create a Kobweb site from any of the provided Kobweb templates, they will already be set up in the way that
this section recommends. However, if you are trying to add Kobweb to an existing project, or even if you're just curious
about this for learning purposes, the information here might be useful.

## Kobweb artifact repositories

Kobweb publishes its libraries to Maven Central and its plugins to the Gradle Plugin Portal. Therefore, Kobweb
recommends setting up your project's `settings.gradle.kts` like so:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
```

Dependencies on Maven Central and the Gradle Plugin Portal are so standard, it's hard to imagine a project that isn't
already using them, so in most cases, you won't have to do anything.

### Testing snapshots

Occasionally, especially if you file an issue for a bug fix or a feature request, our team may ask you if you're willing
to try using a snapshot build (a dev build, essentially).

Snapshots are, by design, not supported in either Maven Central nor the Gradle Plugin Portal. Therefore, we host all
plugin and library artifacts in a separate official snapshot repository (at
`https://s01.oss.sonatype.org/content/repositories/snapshots/`). As a result, you will have to declare this repository
for both plugin *and* library blocks.

An easy way to enable this is by adding the following block of code into your `settings.gradle.kts` file:

```diff
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

+ // The following block registers dependencies to enable Kobweb snapshot support. It is safe to delete or comment out
+ // this block if you never plan to use them.
+ gradle.settingsEvaluated {
+     fun RepositoryHandler.kobwebSnapshots() {
+         maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
+             content { includeGroupByRegex("com\\.varabyte\\.kobweb.*") }
+             mavenContent { snapshotsOnly() }
+         }
+     }
+
+     pluginManagement.repositories { kobwebSnapshots() }
+     dependencyResolutionManagement.repositories { kobwebSnapshots() }
+ }
```

> [!NOTE]
> The above code, adding repositories inside the `settingsEvaluated` block, is actually not idiomatic Gradle -- that
> approach would be to create a settings plugin or just copy/paste the repository declaration in all relevant places --
> but at the moment we are suggesting this approach for its simplicity:
>
> 1. If we could have declared `kobwebSnapshots` as a top level method, that would have been an easy option to
>    recommend. However, the `pluginManagement` block is "magic" and you cannot share code with it. This approach lets
>    us at least mimic that kind of solution.
> 2. Keeping the snapshot declaration logic separated in its own block makes it easy to remove it later if you decide
     you don't want to keep it anymore.
> 3. This approach is isolated inside a single file, while a settings plugin would be a lot of work that would require
     touching several files, which is probably not worth it just for enabling snapshots.

## Gradle version catalogs

The project templates created by Kobweb all embrace Gradle version catalogs.

Version catalogs are declarations of dependency coordinates that live at `gradle/libs.versions.toml`. If you find
yourself wanting to update the dependencies for projects you originally created via `kobweb create`, that file is where
you'll find them.

For example, here is the
[libs.versions.toml](https://github.com/varabyte/kobweb-site/blob/main/gradle/libs.versions.toml) we use for this site.

```toml
[versions]
jetbrains-compose = "..."
kobweb = "..."
kotlin = "..."

[libraries]
compose-html-core = { module = "org.jetbrains.compose.html:html-core", version.ref = "jetbrains-compose" }
compose-runtime = { module = "org.jetbrains.compose.runtime:runtime", version.ref = "jetbrains-compose" }
kobweb-core = { module = "com.varabyte.kobweb:kobweb-core ", version.ref = "kobweb" }
kobweb-silk = { module = "com.varabyte.kobweb:kobweb-silk", version.ref = "kobweb" }
silk-icons-fa = { module = "com.varabyte.kobwebx:silk-icons-fa", version.ref = "kobweb" }
kobwebx-markdown = { module = "com.varabyte.kobwebx:kobwebx-markdown", version.ref = "kobweb" }

[plugins]
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
jetbrains-compose = { id = "org.jetbrains.compose", version.ref = "jetbrains-compose" }
kobweb-application = { id = "com.varabyte.kobweb.application", version.ref = "kobweb" }
kobwebx-markdown = { id = "com.varabyte.kobwebx.markdown", version.ref = "kobweb" }
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
```

Once a version catalog file is defined, you can reference library and plugin dependencies with compile-time checked
paths.

The following snippets are extracted from this
site's [build.gradle.kts](https://github.com/varabyte/kobweb-site/blob/main/site/build.gradle.kts) build script:

```kotlin
plugins {
     alias(libs.plugins.kotlin.multiplatform)
     alias(libs.plugins.compose.compiler)
     alias(libs.plugins.kobweb.application)
     alias(libs.plugins.kobwebx.markdown)
}

kotlin {
     sourceSets {
          jsMain.dependencies {
               implementation(libs.compose.html.core)
               implementation(libs.kobweb.core)
               implementation(libs.kobweb.silk)
          }
     }
}
```

To read more about the version catalogs, please check out the
[official docs](https://docs.gradle.org/current/userguide/version_catalogs.html).

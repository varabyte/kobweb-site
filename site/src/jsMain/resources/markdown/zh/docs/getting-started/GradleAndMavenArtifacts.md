---
description: Information about where Kobweb artifacts are hosted.
title: Gradle 及 Maven 产物
follows: KobwebProject
---

当您使用任何提供的 Kobweb 模板创建 Kobweb 站点时，它们已经按照本节推荐的方式进行设置。然而，如果您正试图将 Kobweb 添加到现有项目中，或者即使您只是出于学习目的而感到好奇，这里的信息可能会有用。

## Kobweb 构件仓库

Kobweb 将其库发布到 Maven Central，并将其插件发布到 Gradle Plugin Portal。因此，Kobweb 建议按如下方式设置项目的 `settings.gradle.kts`：

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

对 Maven Central 和 Gradle Plugin Portal 的依赖如此标准，很难想象有项目不使用它们，所以在大多数情况下，您不需要做任何事情。

### 测试快照版本

有时候，特别是当您提交了 bug 修复或功能请求的问题时，我们的团队可能会询问您是否愿意尝试使用快照构建（本质上是开发构建）。

根据设计，Maven Central 和 Gradle Plugin Portal 都不支持快照版本。因此，我们在单独的官方快照仓库（位于 `https://s01.oss.sonatype.org/content/repositories/snapshots/`）中托管所有插件和库构件。因此，您需要为插件和库块都声明这个仓库。

启用快照仓库的简单方法是在 `settings.gradle.kts` 文件中添加以下代码块：

```bash
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

+ // 以下代码块注册依赖项以启用 Kobweb 快照支持。如果您永远不打算使用快照版本，
+ // 可以安全地删除或注释掉这个代码块。
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
> 上述代码在 `settingsEvaluated` 块中添加仓库实际上不是惯用的 Gradle 做法 —— 标准方法是创建一个设置插件或者
> 直接在所有相关位置复制/粘贴仓库声明 —— 但目前我们建议这种方法是因为它的简单性：
>
> 1. 如果我们可以将 `kobwebSnapshots` 声明为顶层方法，那将是一个容易推荐的选择。但是，`pluginManagement` 块是
>    "魔法"的，你无法与它共享代码。这种方法至少让我们能够模仿这种解决方案。
> 2. 将快照声明逻辑分离在自己的块中，使得以后如果您决定不再需要它时，可以轻松删除。
> 3. 这种方法仅限于单个文件，而设置插件需要涉及多个文件的大量工作，仅仅为了启用快照可能不值得。

## Gradle 版本目录

通过 Kobweb 创建的项目模板都采用了 Gradle 版本目录。

版本目录是位于 `gradle/libs.versions.toml` 的依赖坐标声明。如果您想更新通过 `kobweb create` 最初创建的项目的依赖项，您可以在该文件中找到它们。

例如，这是我们用于本站点的 [libs.versions.toml](https://github.com/varabyte/kobweb-site/blob/main/gradle/libs.versions.toml)：

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

一旦定义了版本目录文件，您就可以使用编译时检查的路径引用库和插件依赖。

以下代码片段摘自本站点的 [build.gradle.kts](https://github.com/varabyte/kobweb-site/blob/main/site/build.gradle.kts) 构建脚本：

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

要了解更多关于版本目录的信息，请查看[官方文档](https://docs.gradle.org/current/userguide/version_catalogs.html)。

---
title: Kobweb服务插件
follows: Fullstack
---

创建全栈应用程序的用户通常希望能够完全掌控客户端和服务器端代码。

然而，作为一个有主见的框架，Kobweb 提供了一个自定义的 Ktor 服务器来实现一些特性，比如 ${DocsLink("API 路由", "fullstack#define-api-routes")} 和实时重载。

将这些行为重构到用户可以导入到自己的后端服务器的库中并不容易。作为折衷方案，一些服务器配置可以通过 `.kobweb/conf.yaml` 文件进行暴露，这一直是用户可以影响服务器行为的主要方式。

话虽如此，总会有一些 Kobweb 无法预料的用例。因此作为一个后备方案，Kobweb 允许了解相关知识的用户编写自己的插件来扩展服务器。

创建 Kobweb 服务器插件相对简单。你需要：

* 在你的项目中创建一个新模块，生成一个包含 `KobwebServerPlugin` 接口实现的 JAR 文件。
* 在你的站点构建脚本中将该模块添加为 `kobwebServerPlugin` 依赖。
    * 这样可以确保该 jar 的副本被放置在项目的 `.kobweb/server/plugins` 目录下。

### 创建 Kobweb 服务器插件

以下步骤将指导你创建第一个 Kobweb 服务器插件。

> [!TIP]
> 你可以下载[这个项目](https://github.com/varabyte/data/raw/main/kobweb/projects/serverplugin.zip)，查看将本节说明应用到 `kobweb create app` 站点的完整结果。

* 在你的项目中创建一个新模块。
    * 例如，将其命名为 "demo-server-plugin"。
    * 确保更新 `settings.gradle.kts` 文件以包含新项目。
* 在 `.gradle/libs.versions.toml` 中为 `kobweb-server-project` 库和 kotlin JVM 插件添加新条目：
  ```toml
  [libraries]
  kobweb-server-plugin = { module = "com.varabyte.kobweb:kobweb-server-plugin", version.ref = "kobweb" }

  [plugins]
  kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
  ```
* **对于所有剩余步骤，在新模块的目录下创建所有文件/目录（例如 `demo-server-plugin/`）。**
* 创建 `build.gradle.kts`：
  ```kotlin
    plugins {
      alias(libs.plugins.kotlin.jvm)
    }
    group = "org.example.app" // 更新为你自己的项目组
    version = "1.0-SNAPSHOT"

    dependencies {
      compileOnly(libs.kobweb.server.plugin)
    }
  ```
* 创建 `src/main/kotlin/DemoKobwebServerPlugin.kt`：
  ```kotlin
  import com.varabyte.kobweb.server.plugin.KobwebServerPlugin
  import io.ktor.server.application.Application
  import io.ktor.server.application.log

  class DemoKobwebServerPlugin : KobwebServerPlugin {
    override fun configure(application: Application) {
      application.log.info("将我替换为真实的配置")
    }
  }
  ```
> [!TIP]
> 由于 Kobweb 服务器是用 Ktor 编写的，你应该熟悉[Ktor 的文档](https://ktor.io/docs/plugins.html)。

* 创建 `src/main/resources/META-INF/services/com.varabyte.kobweb.server.plugin.KobwebServerPlugin`，将其内容设置为插件的完全限定类名。例如：
  ```text
  org.example.app.DemoKobwebServerPlugin
  ```
> [!NOTE]
> 如果你不熟悉 `META-INF/services`，可以阅读[这篇有用的文章](https://www.baeldung.com/java-spi)了解更多关于服务实现的信息，这是一个非常有用的 Java 特性。

### 注册你的服务器插件 jar

Kobweb Gradle 应用插件提供了一种通知它有关你的服务器插件项目的方法。设置它，Gradle 将自动为你构建并复制插件 jar。

在你的 Kobweb 项目的构建脚本中，在顶层依赖块中包含以下 `kobwebServerPlugin` 行：

```kotlin
// site/build.gradle.kts

// 重要！顶层依赖块，
// 而不是嵌套在 kotlin 块中的。
dependencies {
  kobwebServerPlugin(project(":demo-server-plugin"))
}

kotlin { /* ... */ }
```

设置完成后，在下次运行 Kobweb 服务器时（例如通过 `kobweb run`），如果你检查日志 ${DocsAside("服务器日志", "fullstack#server-logs")}，你应该会看到类似这样的内容：

```diff
[main] INFO  ktor.application - Autoreload is disabled because the development mode is off.
+[main] INFO  ktor.application - 将我替换为真实的配置
[main] INFO  ktor.application - Application started in 0.112 seconds.
[main] INFO  ktor.application - Responding at http://0.0.0.0:8080
```

### 挂钩到 Ktor 路由事件

尽管 `KobwebServerPlugin` 接口很简单，但传入 `KobwebServerPlugin.configure` 的 `application` 参数非常强大。

虽然这听起来可能有点抽象，但你可以在 Kobweb 服务器插件中创建和安装 Ktor 应用插件。一旦完成，你就可以访问网络调用的所有阶段，以及一些其他钩子，比如接收应用生命周期事件的钩子。

> [!TIP]
> 请阅读 [Extending Ktor 文档](https://ktor.io/docs/custom-plugins.html)了解更多信息。

实现方式如下：

```kotlin
import com.varabyte.kobweb.server.plugin.KobwebServerPlugin
import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install

class DemoKobwebServerPlugin : KobwebServerPlugin {
  override fun configure(application: Application) {
    val demo = createApplicationPlugin("DemoKobwebServerPlugin") {
      onCall { call -> /* ... */ } // 请求进入
      onCallRespond { call -> /* ... */ } // 响应发出
    }
    application.install(demo)
  }
}
```

### 更改 Kobweb 服务器插件需要重启服务器

需要注意的是，与 Kobweb 的其他部分不同，Kobweb 服务器插件不支持实时重载。我们在服务器的生命周期中只启动和配置一次 Kobweb 服务器。

如果你对 Kobweb 服务器插件进行更改，必须退出并重启服务器才能使其生效。

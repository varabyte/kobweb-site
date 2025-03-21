---
title: 调试
follows: Index
---

Kobweb 项目始终包含前端，如果配置为全栈站点，还会包含后端。两者需要不同的调试步骤。

## 调试前端

目前，为 Kotlin/JS 代码配置调试器需要 IntelliJ Ultimate 版本。如果你有这个版本，可以按照官方文档中的[这些步骤](https://kotlinlang.org/docs/js-debugging.html#debug-in-the-ide)操作。

> [!IMPORTANT]
> 请确保 URL 中的端口与你在 `.kobweb/conf.yaml` 文件中指定的端口匹配。默认情况下，端口是 8080。

如果你无法使用 IntelliJ Ultimate，那么你只能依靠 `println` 进行调试。虽然这远非理想的方案，但实时重载加上 Kotlin 的类型系统通常可以帮助你逐步构建网站，而不会遇到太多问题。

> [!TIP]
> 如果你是学生，可以在[这里](https://www.jetbrains.com/community/education/#students)申请免费的 IntelliJ Ultimate 许可证。
> 如果你维护一个开源项目，可以在[这里](https://www.jetbrains.com/community/opensource/#support)申请。

## 调试后端

调试后端首先需要配置 Kobweb 服务器以支持[远程调试](https://en.wikipedia.org/wiki/Debugging#Remote_debugging)。
这可以通过修改构建脚本中的 `kobweb` 块来启用：

```kotlin
kobweb {
  app {
    server {
      remoteDebugging {
        enabled.set(true)
        port.set(5005)
      }
    }
  }
}
```

> [!NOTE]
> 指定端口是可选的。否则，默认为 5005，这是一个常见的远程调试默认端口。不过，如果你需要同时调试多个 Kobweb 服务器，
> 更改端口会很有用。

一旦启用了远程调试支持，你就可以按照[官方文档](https://www.jetbrains.com/help/idea/attaching-to-local-process.html#attach-to-remote)
在你的 IDE 中添加一个 *远程 JVM 调试* 配置。

> [!IMPORTANT]
> 要使远程调试工作：
> * *调试器模式* 应设置为 *连接到远程 JVM*
> * 你需要正确指定 *使用模块类路径* 的值。通常，使用与你的 Kobweb 应用关联的 `jvmMain` 类路径，例如 `app.site.jvmMain`。
>   如果你已将后端代码重构到另一个模块，你应该可以使用那个模块。

这时，使用 `kobweb run` 启动你的 Kobweb 服务器。

> [!CAUTION]
> 远程调试仅在开发模式下支持。使用 `kobweb run --env prod` 启动的服务器不会启用远程调试。

在 Kobweb 服务器运行并选择"远程调试"运行配置后，按下调试按钮。如果一切设置正确，你应该会在 IDE 调试器控制台中看到类似
这样的消息：`Connected to the target VM, address: 'localhost:5005', transport: 'socket'`

如果你看到一个红色弹窗，显示类似 `Unable to open debugger port (localhost:5005): java.net.ConnectException "Connection refused"`
的消息，请仔细检查 `conf.yaml` 文件中的值，重启服务器，然后重试。

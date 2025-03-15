---
title: "全栈项目"
follows: Index
---

## 声明全栈项目

在你的网站构建脚本中，确保调用 `configAsKobwebApplication(includeServer = true)`。完成这一步后，你就可以为你的 Kobweb 网站编写服务器逻辑了。

Kobweb 项目始终至少会有一个 JavaScript 目标，代表前端，但如果你声明要实现服务器，它也会为你创建一个 JVM 目标。你可以向这个目标添加依赖项，使它们可用于你的服务器代码：

```kotlin
// site/build.gradle.kts
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
}

/* ... */

kotlin {
    configAsKobwebApplication(includeServer = true)
    sourceSets {
        /* ... */
        jvmMain.dependencies {
            /* ... */
        }
    }
}
```

检查是否一切设置正确的简单方法是在 IntelliJ IDEA 中打开你的项目，等待它完成索引，然后检查 `jvmMain` 文件夹是否被检测为模块（如果是，它会有一个特殊图标，看起来和 `jsMain` 文件夹一样）：

![Kobweb JVM main 设置正确](https://github.com/varabyte/media/raw/main/kobweb/images/kobweb-jvm-main.png)

## 定义 API 路由

你可以定义和注解方法来生成可交互的服务器端点。添加一个端点：

1. 在 `jvmMain` 源目录下的 `api` 包中的某个文件中定义你的方法（可选择是否为 `suspend` 方法）。
2. 该方法应该只接受一个参数：`ApiContext`。
3. 用 `@Api` 注解标记它

例如，这是一个简单的方法，它会回显传入的参数：

```kotlin
// jvmMain/kotlin/com/mysite/api/Echo.kt

@Api
suspend fun echo(ctx: ApiContext) {
    // ctx.req 用于传入请求，ctx.res 用于响应客户端

    // 参数从 URL 解析，例如这里的 "/api/echo?message=..."
    val msg = ctx.req.params["message"] ?: ""
    ctx.res.setBodyText(msg)
}
```

运行项目后，你可以通过访问 `mysite.com/api/echo?message=hello` 来测试这个端点

你也可以在前端代码中使用添加到 `kotlinx.browser.window` 类的扩展 `api` 属性来触发端点：

```kotlin
@Page
@Composable
fun ApiDemoPage() {
  val coroutineScope = rememberCoroutineScope()

  Button(onClick = {
    coroutineScope.launch {
      println("回显: " + window.api.get("echo?message=hello").decodeToString())
    }
  }) { Text("点击我") }
}
```

支持所有的 HTTP 方法（`post`、`put` 等）。

如果请求因任何原因失败，这些方法都会抛出异常。注意，对于每个 HTTP 方法，都有一个相应的"try"版本，它会返回 null 而不是抛出异常（`tryPost`、`tryPut` 等）。

如果你知道自己在做什么，当然也可以直接使用 [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch)。

## 响应 API 请求

当你定义 API 路由时，你需要为响应设置状态码，否则它将默认为状态码 `404`。

换句话说，以下 API 路由存根将返回 404：

```kotlin
@Api
suspend fun error404(ctx: ApiContext) {
}
```

相比之下，这个最小的 API 路由返回 OK 状态码：

```kotlin
@Api
suspend fun noActionButOk(ctx: ApiContext) {
    ctx.res.status = 200
}
```

> [!IMPORTANT]
> `ctx.res.setBodyText` 方法会自动为你设置状态码为 200，这就是为什么前面章节的代码在没有直接设置状态码的情况下也能工作。当然，如果你想在设置响应体文本后返回不同的状态码值，你可以在调用 `setBodyText` 后显式设置它。例如：
> ```kotlin
> ctx.res.setBodyText("...")
> ctx.res.status = 201
> ```

选择默认为 404 的设计是为了允许你根据输入条件有条件地处理 API 路由，早期中止自动导致客户端收到错误。

一个很常见的情况是创建一个只处理 POST 请求的 API 路由：

```kotlin
@Api
suspend fun updateUser(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return
    // ...
    ctx.res.status = 200
}
```

最后，注意你可以向响应添加头部。一些服务器提供的常见端点是带有更新后 URL 位置的重定向（302）。这看起来像：

```kotlin
@Api
suspend fun redirect(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return
    ctx.res.headers["Location"] = "..."
    ctx.res.status = 302
  
    // 注意：为此提供了一个便捷方法：
    // `ctx.res.setAsRedirect("...", 302)
}
```

很简单！

## 拦截 API 路由

Kobweb 提供了一种拦截所有传入 API 请求的方法，在请求传递给实际的 API 路由处理程序之前先处理它们。

要拦截所有路由，声明一个带有 `@ApiInterceptor` 注解的 suspend 方法。该方法必须接受一个 `ApiInterceptorContext` 参数并返回一个 `Response`。

你可以检查上下文参数中与传入请求相关的路径。如果你不想处理特定路径，可以调用 `ctx.dispatcher.dispatch()` 来正常传递它：

```kotlin
@ApiInterceptor
suspend fun interceptRequest(ctx: ApiInterceptorContext): Response {
    return when {
        ctx.path == "/example" -> Response().apply { setBodyText("已拦截！") }
        // 默认：将请求传递给通常处理它的路由
        else -> ctx.dispatcher.dispatch()
    }
}
```

`ApiInterceptorContext` 类提供了对传入请求的可变版本的访问，这让你有机会在处理之前先修改它（例如添加 cookies、更新头部），这可能很有用。

`ctx.dispatcher.dispatch` 方法接受一个可选的路径参数，你可以指定它来将请求委托给不同的 API 路由：

```kotlin
@ApiInterceptor
suspend fun interceptRequest(ctx: ApiInterceptorContext): Response {
    return when {
        // 用户会认为 "/legacy" 处理了请求；
        // 实际上是 "/new" 处理的
        ctx.path == "/legacy" -> ctx.dispatcher.dispatch("/new")
        else -> ctx.dispatcher.dispatch()
    }
}
```

也许你对干预任何传入请求不感兴趣，但你想在响应发送回客户端之前修改所有响应。你可以使用这种模式：

```kotlin
@ApiInterceptor
suspend fun interceptResponse(ctx: ApiInterceptorContext): Response {
    return ctx.dispatcher.dispatch().also { res ->
        res.headers["X-Intercepted"] = "true"
    }
}
```

> [!CAUTION]
> API 拦截器只对 API 路由有效，对静态文件或其他资源无效。换句话说，虽然你*可以*使用拦截器来拦截对 "/api/users/edit" 的调用，但该功能并不是设计用来处理用户导航到 `https://example.com/admin/dashboard` 然后重定向到 `https://example.com/login` 的情况。
>
> API 拦截器也不适用于 API 流（将在本文后面讨论），因为那里没有什么通用的东西可以拦截。API 流建立连接后就会持续存在。

## 动态 API 路由

类似于 ${DocsLink("动态路由", "../foundation/routing#dynamic-routes")}，你可以用相同的方式使用大括号来定义 API 路由，表示应该用某个绑定名称捕获的动态值。

例如，以下端点在查询 `articles/123` 时会将值 "123" 捕获到名为 "article" 的键名中：

```kotlin
// jvmMain/kotlin/com/mysite/api/articles/Article.kt

@Api("{}")
suspend fun fetchArticle(ctx: ApiContext) {
    val articleId = ctx.req.params["article"] ?: return
    // ...
}
```

回想一下 `@Page` 文档中提到的，在大括号内指定名称定义了用于捕获值的变量名。当为空时，如上所示，Kobweb 使用文件名来生成它。换句话说，在上面的例子中，你可以显式指定 `@Api("{article}")` 来达到完全相同的效果。

一旦定义了这个 API 端点，你就可以像查询任何普通 API 端点一样查询它：

```kotlin
coroutineScope.launch {
    // 这将导致服务器上的 "article" 变量
    // 被设置为 "123"
    val articleText = window.api.get("articles/123").decodeToString()
    // ...
}
```

## `@InitApi` 方法和初始化服务

Kobweb 服务器支持声明在启动时应该运行的方法。这些方法必须用 `@InitApi` 注解，并且必须接受一个 `InitApiContext` 参数。

> [!IMPORTANT]
> 如果你正在运行开发服务器，并更改了任何后端代码，导致实时重载事件，初始化方法将再次运行。

`InitApiContext` 类暴露了一个可变的 set 属性（称为 `data`），你可以在其中放入任何内容。同时，`@Api` 方法暴露了 `data` 的不可变版本。这允许你在 `@InitApi` 方法中初始化一个服务，然后在 `@Api` 方法中访问它。

让我们演示一个具体的例子。假设你有一个名为 `Database` 的接口和一个实现它并提供额外 API 来修改数据库的可变子类 `MutableDatabase`。

注册和后续查询这样一个数据库实例的框架可能如下所示：

```kotlin
@InitApi
fun initDatabase(ctx: InitApiContext) {
    val db = MutableDatabase()
    db.createTable("users", listOf("id", "name")).apply {
        addRow(listOf("1", "Alice"))
        addRow(listOf("2", "Bob"))
    }
    db.loadResource("products.csv")

    ctx.data.add<Database>(db)
}

@Api
fun getUsers(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return
    val db = ctx.data.get<Database>()
    ctx.res.setBodyText(db.query("SELECT * FROM users").toString())
}
```

## 定义 API 流

Kobweb 服务器通过流支持持久连接。流本质上是在客户端和服务器之间保持持续联系的命名通道，允许任何一方随时向另一方发送消息。如果你希望服务器能够在不需要轮询的情况下向客户端传达更新，这特别有用。

此外，多个客户端可以连接到同一个流。在这种情况下，服务器可以选择不仅向你的客户端发送消息，还可以向同一流上的所有用户（或经过筛选的用户子集）广播消息。你可以用这个来实现一个带房间的聊天服务器。

### API 流示例

像 API 路由一样，API 流必须在 `jvmMain` 源目录的 `api` 包下定义。默认情况下，流的名称将从声明它的文件名和路径派生（例如，`api/lobby/Chat.kt` 将创建一个名为 "lobby/chat" 的流）。

与 API 路由不同，API 流被定义为属性，而不是方法。这是因为 API 流需要比路由更灵活，因为流由多个不同的事件组成：客户端连接、客户端消息和客户端断开连接。

另外与 API 路由不同，流不必被注解。Kobweb Application 插件可以自动检测它们。

例如，这是一个简单的流，在后端声明，它会回显它收到的任何参数：

```kotlin
// jvmMain/kotlin/com/mysite/api/Echo.kt

val echo = object : ApiStream {
    override suspend fun onClientConnected(ctx: ClientConnectedContext) {
        // 可选：ctx.stream.broadcast 向所有其他客户端发送消息，表明 ctx.clientId 已连接
        // 可选：在此更新 ctx.data，初始化与 ctx.clientId 关联的数据
    }
    override suspend fun onTextReceived(ctx: TextReceivedContext) {
        ctx.stream.send(ctx.text)
    }
    override suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) {
        // 可选：ctx.stream.broadcast 向所有其他客户端发送消息，表明 ctx.clientId 已断开连接
        // 可选：在此更新 ctx.data，删除与 ctx.clientId 关联的数据
    }
}
```

要从你的网站与 API 流通信，你需要在客户端创建一个流连接。我们提供了 `rememberApiStream` 方法来帮助实现这一点：

```kotlin
@Page
@Composable
fun ApiStreamDemoPage() {
    val echoStream = rememberApiStream("echo", object : ApiStreamListener {
        override fun onConnected(ctx: ConnectedContext) {}
        override fun onTextReceived(ctx: TextReceivedContext) {
            console.log("回显: ${ctx.text}")
        }
        override fun onDisconnected(ctx: DisconnectedContext) {}
    })

    Button(onClick = {
        echoStream.send("你好！")
    }) { Text("点击我") }
}
```

运行你的项目后，你可以点击按钮并检查控制台日志。如果一切正常，每次按按钮时你都会看到 "回显: 你好！"。

> [!TIP]
> 运行 `kobweb create examples/chat` 来实例化一个使用 API 流实现简单聊天应用的项目。随意参考该项目以获取更实际的示例。

### API 流便利功能

上面的例子故意很详细，以展示 API 流的更广泛功能。但是，根据你的用例，你可以省略相当多的样板代码。

首先，连接和断开连接处理程序是可选的，如果你不需要它们，可以省略它们。让我们简化回显示例：

```kotlin
// 后端
val echo = object : ApiStream {
    override suspend fun onTextReceived(ctx: TextReceivedContext) {
        ctx.stream.send(ctx.text)
    }
}

// 前端
val echoStream = rememberApiStream("echo", object : ApiStreamListener {
    override fun onTextReceived(ctx: TextReceivedContext) {
        console.log("回显: ${ctx.text}")
    }
})
```

此外，如果你只关心文本事件，有便利方法可以使用：

```kotlin
// 后端
val echo = ApiStream { ctx -> ctx.stream.send(ctx.text) }

// 前端
val echoStream = rememberApiStream("echo") {
    ctx -> console.log("回显: ${ctx.text}")
}
```

在实践中，你的 API 流可能比上面的回显示例复杂一些，但知道你只需要在服务器上写一行代码，在客户端写另一行代码就可以创建一个持久的客户端-服务器连接，这很好！

> [!NOTE]
> 如果你需要创建一个对实际连接到服务器的时间有更严格控制的 API 流，你可以直接创建 `ApiStream` 对象，而不是使用 `rememberApiStream`：
> ```kotlin
> val echoStream = remember { ApiStream("echo") }
> val scope = rememberCoroutineScope()
>
> // 稍后，也许在按钮被点击后...
> scope.launch {
>     echoStream.connect(object : ApiStreamListener { /* ... */ })
> }
> ```

## API 路由 vs. API 流

当面临选择时，尽可能多地使用 API 路由。它们在概念上更简单，你可以使用像 curl 这样的 CLI 程序查询 API 端点，有时甚至可以直接在浏览器中访问 URL。它们非常适合处理对服务器资源的查询或更新，以响应用户驱动的操作（如访问页面或点击按钮）。你执行的每个操作除了一些负载信息外，还会返回一个明确的响应代码。

同时，API 流非常灵活，可能是处理高频通信的自然选择。但它们也更复杂。与简单的请求/响应模式不同，你选择的是管理一个可能很长的生命周期，在此期间你可以接收任意数量的事件。你可能还必须关注流上所有客户端之间的交互。API 流本质上是有状态的。

使用 API 流时，你通常需要做出很多决定。如果客户端或服务器意外断开连接，你应该怎么做？你想如何向客户端传达他们的最后一个操作成功或失败（你需要明确到底是哪个操作，因为他们可能在此期间已经发送了另一个）？对于双方都可以随时向对方发送消息的客户端和服务器连接，你想强制执行什么结构（如果有的话）？

最重要的是，API 流可能无法像 API 路由那样好地水平扩展。在某些时候，你可能会发现自己处于需要启动新的 Web 服务器来处理某些密集负载的情况。

如果你使用的是 API 路由，你可能已经在使用数据库服务作为数据后端，所以这可能会无缝工作。

但对于 API 流，你可能自然会发现自己在编写大量广播代码。然而，这只能用于在连接到同一服务器的所有客户端之间通信。连接到不同服务器上的同一流的两个客户端实际上处于不同的、断开连接的世界中。

上述情况通常通过使用发布订阅服务（如 Redis）来处理。这感觉有点类似于在 API 路由情况下使用数据库作为服务，但这段代码可能不那么容易迁移。

API 路由和 API 流不是必须使用其中之一的情况。你的项目可以同时使用两者！一般来说，试着想象可能会启动新服务器的情况，并设计你的代码来优雅地处理这种情况。API 路由通常是安全的，所以经常使用它们。

然而，如果你有需要实时通信事件的情况，特别是你希望你的客户端通过事件被服务器持续指导要做什么的情况，API 流是一个很好的选择。

> [!NOTE]
> 你也可以在网上搜索关于 REST vs WebSockets 的内容，因为这些是 API 路由和 API 流实现所使用的技术。任何关于它们的讨论都应该适用于这里。

## 服务器日志

当你运行 `kobweb run` 时，启动的 Web 服务器默认会记录到 `.kobweb/server/logs` 目录。

> [!NOTE]
> 你可以使用提供给 `@Api` 调用的 `ctx.logger` 属性生成日志。

你可以通过编辑 `.kobweb/conf.yaml` 文件来配置日志行为。以下我们显示将所有参数设置为它们的默认值：

```yaml
server:
    logging:
        level: DEBUG # ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF
        enableConsoleLogging: true # 如果为 false，日志不会写入 stdout/stderr
        enableFileLogging: true # 如果为 false，不会创建日志文件
        logRoot: ".kobweb/server/logs"
        clearLogsOnStart: true # 警告 - 如果为 true，会清除 logRoot 中的所有文件，所以不要在那里放其他文件！
        logFileBaseName: "kobweb-server" # 例如 "kobweb-server.log"，"kobweb-server.2023-04-13.log"
        maxFileCount: null # null = 无限制。每天创建一个日志文件，所以 30 = 1 个月的日志
        totalSizeCap: 10MiB # null = 无限制。接受的单位：B, K, M, G, KB, MB, GB, KiB, MiB, GiB
        compressHistory: true # 如果为 true，旧日志文件会用 gzip 压缩
```

上述默认值是为在开发者模式下在本地机器上运行项目的大多数用户选择的合理值。但是，对于生产服务器，你可能想将 `clearLogsOnStart` 设为 false，在审查 Web 服务器主机的磁盘限制后增加 `totalSizeCap`，也许将 `maxFileCount` 设置为一个合理的限制。

> [!NOTE]
> 大多数用户可能认为 "10MB" 是 10 * 1024 * 1024 字节，但在这里它实际上会导致 10 * 1000 * 1000 字节。
> 在配置这个值时，你可能想使用 "KiB"、"MiB" 或 "GiB"。

## CORS

[CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)，即*跨源资源共享*，是一个建立在这样一个想法上的安全特性：网页不应该能够从不是提供该页面的服务器请求资源，*除非*它来自受信任的域。

要为 Kobweb 后端配置 CORS，Kobweb 的 `.kobweb/conf.yaml` 文件允许你使用 `cors` 块声明这些受信任的域：

```yaml
server:
    cors:
        hosts:
            - name: "example.com"
              schemes:
                  - "https"
```

> [!NOTE]
> 指定 schemes 是可选的。如果你不指定它们，Kobweb 默认为 "http" 和 "https"。

> [!NOTE]
> 你也可以指定子域，例如
> ```yaml
> - name: "example.com"
>   subdomains:
>     - "en"
>     - "de"
>     - "es"
> ```
> 这将为 `en.example.com`、`de.example.com` 和 `es.example.com`，以及 `example.com` 本身添加 CORS 支持。

配置完成后，你的 Kobweb 服务器将能够响应来自任何指定主机的数据请求。

> [!TIP]
> 如果你发现你的全栈网站在开发期间在本地工作正常，但在生产版本中拒绝请求，请检查你的浏览器的控制台日志。如果你在那里看到关于违反 CORS 策略的错误，这意味着你没有正确配置 CORS。

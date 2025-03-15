---
title: 前后端之间共享数据对象
follows: CustomFonts
---

在开始一个全栈项目时，您可能会发现自己在原型阶段通过在客户端和服务器之间传递原始字符串来进行实验。然而，您最终可能会想要发送富数据对象，因为它们更适合封装复杂性，同时还提供类型安全。

感谢 Kotlin 多平台和 Kotlinx 序列化，这很容易实现！

## 概述

总的来说，您需要：

* 在您网站的构建脚本中添加 `kotlinx.serialization` 和（可选的）`kobwebx.serialization.kotlinx` 依赖。
* 创建一个 `commonMain` 文件夹，这是您的数据对象将存放的地方。
* 添加后端和前端代码，在通过网络发送这些对象之前序列化和反序列化它们。

为了展示这一点，我们将演示实现一对请求/响应对象。为了简单起见，请求将包含一个应该发送到服务器并在响应对象中回显给客户端的字符串。为了使请求更有趣，我们还将包括一个操作指令，服务器在发送回字符串之前应该对其执行该操作。

> [!NOTE]
> 如果您不熟悉请求/响应模式，这是一种常见的方式来构建通过网络通信的服务的API。当您坚持使用它时，这个约定易于阅读和快速理解，并且当您不可避免地需要在以后添加新字段时，这种方法通常有利于向后兼容性。
> （然而，编写向后兼容的数据对象超出了本指南的范围。）

## 构建脚本

`gradle/libs.versions.toml`
```toml
[versions]
# 请在您的项目中指定所需的版本
kobweb = "..."
kotlin = "..."
kotlinx-serialization = "..."

[libraries]
kobwebx-serialization-kotlinx = { module = "com.varabyte.kobwebx:kobwebx-serialization-kotlinx", version.ref = "kobweb" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }

[plugins]
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

`site/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example"

kotlin {
    configAsKobwebApplication(includeServer = true)
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kobwebx.serialization.kotlinx)
        }
    }
}
```

## 公共代码

`site/src/commonMain/kotlin/com/example/models/Echo.kt`
```kotlin
package com.exmaple.models

import kotlinx.serialization.Serializable

enum class EchoOperation {
    AS_IS, // Example -> Example
    REVERSE, // Example -> elpmaxE
    LOWERCASE, // Example -> example
    UPPERCASE, // Example -> EXAMPLE
}

@Serializable
class EchoRequest(
    val text: String,
    val operation: EchoOperation = EchoOperation.AS_IS,
)

@Serializable
class EchoResponse(
    val text: String,
)
```

## 后端代码

我们将把这个 API 设计为 POST 请求，尽管在这种情况下它是无状态的，使用 GET 可能会被认为更合适。这是因为在实践中，当您向服务器发送请求对象时，通常希望在服务器上添加、更新或删除某些状态。

但更重要的是，HTTP POST 请求支持包含正文，这是嵌入富数据对象的自然位置；GET 请求则不支持。（我们稍后也会展示如何处理 GET 请求。）

`site/src/jvmMain/kotlin/com/example/api/Echo.kt`
```kotlin
package com.example.api

@Api
fun Echo(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return

    val echoRequest = ctx.req.readBody<EchoRequest>()!!
    val echoResponse = EchoResponse(
        text = when (echoRequest.operation) {
            EchoOperation.AS_IS -> echoRequest.text
            EchoOperation.REVERSE -> echoRequest.text.reversed()
            EchoOperation.LOWERCASE -> echoRequest.text.lowercase()
            EchoOperation.UPPERCASE -> echoRequest.text.uppercase()
        }
    )
    ctx.res.setBody(echoResponse)
}
```

希望这个端点相当容易理解。我们解析请求体，并根据操作填充响应体。

> [!NOTE]
> `Request.readBody` 和 `Response.setBody` 是由我们之前包含的 `com.varabyte.kobwebx:kobwebx-serialization-kotlinx` 
> 构件提供的便利方法。
> 
> 如果您不介意冗长并且想跳过添加依赖，您也可以写成 `ctx.req.readBodyText()?.let { text -> Json.decodeFromString<EchoRequest>(text) }` 
> 和 `ctx.res.setBodyText(Json.encodeToString(echoResponse))`。

如果您确实想通过 GET 请求传递数据对象，您可以使用查询参数：
```kotlin
@Api
fun Echo(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return

    val echoRequest = Json.decodeFromString<EchoRequst>(
        ctx.req.params.getValue("data")
    )
    /* ... */
}
```

## 前端代码

有了我们定义的端点，我们现在可以从前端触发它。在某个 `@Page` 中，您需要调用其中一个可以接受和序列化正文参数的 `window.api` HTTP 方法。

由于 `window.api` 方法都是挂起函数，调用它们最简单的方法之一（因此也是演示之一）是从 `LaunchedEffect` 块中调用：
```kotlin
LaunchedEffect(Unit) {
    val response = window.api.post<EchoRequest, EchoResponse>(
        "echo", body = EchoRequest("test", EchoOperation.REVERSE)
    )
    println("Got response: ${response.text}") // Got response: tset
}
```

就是这样！

> [!NOTE]
> 如果上述代码出现编译错误，请确保导入相关的扩展方法。对于上面的示例，这将是 `import com.varabyte.kobweb.browser.post`。

在实践中，您可能会使用类似 `rememberCoroutineScope` 的东西，并在响应某些用户事件（如点击按钮）时使用 `scope.launch`：
```kotlin
val scope = rememberCoroutineScope()

Button(onClick = {
    scope.launch {
        val response = ... // 与之前相同的代码
    }
}) {
    Text("Click me")
}
```

如果您想通过 GET 请求传递数据对象，可以将其作为查询参数嵌入：

```kotlin
val response = window.api.get<EchoResponse>(
  "echo?data=\${Json.encodeToString(EchoRequest("test"))}"
)
```

当您这样做时，字符将自动进行 URL 编码。

## 最后的想法

感谢 Kotlin 多平台和 Kotlinx 序列化，在 Kobweb 全栈项目的前端和后端之间共享数据对象变得很容易。

历史上，共享数据一直是全栈开发人员的痛点。一个常见的解决方案是使用像 [Protocol Buffers](https://protobuf.dev/) 这样的解决方案，这是一种以与语言无关的方式声明数据值的方法，然后使用自定义 protobuf 编译器将该声明转换为您关心的目标代码。

如果您必须支持使用不同语言的客户端和服务器，那么这仍然是一个合理的方法，但如果您可以在两端都使用 Kotlin，那么本指南中讨论的方法要简单得多。

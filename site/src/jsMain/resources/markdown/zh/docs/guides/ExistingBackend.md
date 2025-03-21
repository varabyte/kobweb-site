---
description: A guide for how to use Kobweb if you already have your own existing backend.
title: 使用自定义后端
follows: ExistingProject
---

你可能已经有一个现成的复杂后端，可能是用 Ktor 或 Spring Boot 编写的，如果是这样，你可能想知道是否可以将 Kobweb 与它集成。

目前推荐的解决方案是使用静态布局导出你的网站
${DocsAside("静态布局与全栈网站", "/kobweb/concepts/foundation/exporting#static-layout-vs-full-stack-sites")}，
然后在你的后端添加代码来自行提供文件服务，这是相当简单的。

当你静态导出一个网站时，它会将所有文件生成到你的 `.kobweb/site` 文件夹中。然后，如果使用 Ktor，
提供这些文件服务只需要一行代码：

```kotlin
routing {
    staticFiles("/", File(".kobweb/site"))
}
```

如果使用 Ktor，你还应该安装 [`IgnoreTrailingSlash` 插件](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.routing/-ignore-trailing-slash.html)，
这样当用户访问一个目录（例如 `/docs/`）时，你的 Web 服务器会提供 `index.html`，而不是返回 404：

```kotlin
embeddedServer(...) { // 在这个作用域中 `this` 是 `Application`
  this.install(IgnoreTrailingSlash)
  // 剩余配置
}
```

如果你需要访问后端暴露的 HTTP 端点，你可以直接使用 [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch)，
或者你可以使用 Kobweb 添加到 `window` 对象的便捷 `http` 属性，它暴露了所有 HTTP 方法（`get`、`post`、`put` 等）：

```kotlin
@Page
@Composable
fun CustomBackendDemoPage() {
  LaunchedEffect(Unit) {
    val endpointResponse = window.http.get("/my/endpoint?id=123").decodeToString()
    /* ... */
  }
}
```

不幸的是，使用你自己的后端意味着你选择退出使用 Kobweb 的全栈解决方案，这意味着你将无法使用 Kobweb 的 API 路由、
API 流或实时重载支持。这是我们希望将来能够改进的情况（[相关问题链接](https://github.com/varabyte/kobweb/issues/22)），
但我们没有足够的资源在 1.0 版本中优先解决这个问题。
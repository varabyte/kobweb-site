---
title: "路由"
follows: Index
---

## `@Page`

创建页面非常简单！它就是一个普通的 `@Composable` 方法。要将你的 composable 升级为页面，你只需要：

1. 在 `jsMain` 源目录下的 `pages` 包中的某个位置定义你的 composable。
1. 用 `@Page` 注解标注它

仅凭这些，Kobweb 就会自动为你创建一个站点入口。

例如，如果我创建以下文件：

```kotlin
// jsMain/kotlin/com/mysite/pages/admin/Settings.kt

@Page
@Composable
fun SettingsPage() {
    /* ... */
}
```

这将创建一个可以通过访问 `mysite.com/admin/settings` 访问的页面。

> [!IMPORTANT]
> URL 的最后一部分，这里是 `settings`，被称为 *slug*。

默认情况下，slug 来自文件名，它会被转换为 kebab-case。例如，文件名 `AboutUs.kt` 会转换为 `about-us`。不过，你可以根据需要逐个覆盖这个默认行为（稍后会详细介绍）。

换句话说，生成 URL 时不会使用你的方法名。你可以随意命名方法，但按照惯例，我们建议使用与文件名匹配的名称，并附加 `Page` 后缀。

文件名 `Index.kt` 是特殊的。如果在这样的文件中定义了页面，它将被视为该 URL 下的默认页面。例如，如果在 `.../pages/admin/Index.kt` 中定义的页面，用户访问 `mysite.com/admin/` 时就会看到这个页面。

## Route Override

如果你需要更改为页面生成的路由，可以设置 `Page` 注解的 `routeOverride` 字段：

```kotlin
// jsMain/kotlin/com/mysite/pages/admin/Settings.kt

@Page(routeOverride = "config")
@Composable
fun SettingsPage() {
    /* ... */
}
```

上述代码将创建一个可以通过访问 `mysite.com/admin/config` 访问的页面。

`routeOverride` 还可以包含斜杠，如果值以斜杠开头和/或结尾，则具有特殊含义。

* 以斜杠开头 - 表示从根目录开始的整个路由
* 以斜杠结尾 - 仍然会从文件名生成一个 slug 并附加到路由覆盖中。

如果将覆盖设置为 "index"，其行为与将文件设置为 `Index.kt` 相同，如上所述。

一些示例可以澄清这些规则（以及它们在组合时的行为）。假设我们在文件 `a/b/c/Slug.kt` 中为我们的网站 `example.com` 定义一个页面：

| 注解                    | 生成的 URL                      |
|-------------------------|---------------------------------|
| `@Page`                 | `example.com/a/b/c/slug`        |
| `@Page("other")`        | `example.com/a/b/c/other`       |
| `@Page("index")`        | `example.com/a/b/c/`            |
| `@Page("d/e/f/")`       | `example.com/a/b/c/d/e/f/slug`  |
| `@Page("d/e/f/other")`  | `example.com/a/b/c/d/e/f/other` |
| `@Page("d/e/f/index")`  | `example.com/a/b/c/d/e/f/`      |
| `@Page("/d/e/f/")`      | `example.com/d/e/f/slug`        |
| `@Page("/d/e/f/other")` | `example.com/d/e/f/other`       |
| `@Page("/d/e/f/index")` | `example.com/d/e/f/`            |
| `@Page("/")`            | `example.com/slug`              |
| `@Page("/other")`       | `example.com/other`             |
| `@Page("/index")`       | `example.com/`                  |

> [!CAUTION]
> 尽管这里允许了灵活性，但你不应该频繁使用此功能，甚至完全不使用。Kobweb 项目的好处在于用户可以轻松地将你网站上的 URL 与代码库中的文件关联起来，但此功能允许你打破这些假设。它主要用于启用动态路由
> ${DocsAside("动态路由", "#dynamic-routes")} 或提供一个有效的 URL 名称，该名称使用 Kotlin 文件名中不允许的字符。

## Package

虽然 slug 是从文件名派生的，但路由的早期部分，称为 URL 路由段，是从文件的包派生的。

包将通过删除任何前导或尾随下划线（因为这些通常用于解决包名中允许的值和关键字的限制，例如 `site.pages.blog._2022` 和 `site.events.fun_`）并将 camelCase 包转换为连字符单词（因此 `site.pages.team.ourValues` 生成路由 `/team/our-values/`）。

### `@PackageMapping`

如果你想覆盖为包生成的路由段，可以使用 `PackageMapping` 注解。

例如，假设你的团队出于美观原因不喜欢使用 camelCase 包。或者你有意在网站的路由段中添加前导下划线以强调（因为前面提到前导下划线会自动删除），例如在路由 `/team/_internal/contact-numbers` 中。你可以为此使用包映射。

你将包映射注解应用于当前文件。使用它看起来像这样：

```kotlin
// site/pages/team/values/PackageMapping.kt
@file:PackageMapping("our-values")

package site.pages.blog.values

import com.varabyte.kobweb.core.PackageMapping
```

有了上述包映射，位于 `site/pages/team/values/Mission.kt` 的文件将可以在 `/team/our-values/mission` 访问。

## Page context

每个页面方法通过 `rememberPageContext()` 方法提供对其 `PageContext` 的访问。

关键是，页面的上下文为其提供了对路由器的访问，允许你导航到其他页面。

它还提供了有关当前页面 URL 的动态信息（在下一节中讨论）。

```kotlin
@Page
@Composable
fun ExamplePage() {
    val ctx = rememberPageContext()
    Button(onClick = { ctx.router.navigateTo("/other/page") }) {
        Text("Click me")
    }
}
```

### Query parameters

你可以使用页面上下文检查传递到当前页面 URL 的任何查询参数的值。

因此，如果你访问 `site.com/posts?id=12345&mode=edit`，你可以像这样查询这些值：

```kotlin
enum class Mode {
    EDIT, VIEW;

    companion object {
        fun from(value: String) {
           entries.find { it.name.equals(value, ignoreCase = true) }
               ?: error("Unknown mode: $value")
        }
    }
}

@Page
@Composable
fun Posts() {
    val ctx = rememberPageContext()
    // Here, I'm assuming these params are always present, but you can use
    // `get` instead of `getValue` to handle the nullable case. Care should
    // also be taken to parse invalid values without throwing an exception.
    val postId = ctx.route.params.getValue("id").toInt()
    val mode = Mode.from(ctx.route.params.getValue("mode"))
    /* ... */
}
```

## Dynamic routes

除了查询参数，Kobweb 还支持直接在 URL 中嵌入参数。例如，你可能想注册路径 `users/{user}/posts/{post}`，如果站点访问者输入类似 `users/bitspittle/posts/20211231103156` 的 URL，则会访问该路径。

我们如何设置它？幸运的是，这相当容易。

但首先，请注意在示例动态路由 `users/{user}/posts/{post}` 中实际上有两个不同的动态段，一个在中间，一个在末尾。这些可以通过 `PackageMapping` 和 `Page` 注解分别处理。

### `@PackageMapping`

注意映射名称中使用的大括号！这让 Kobweb 知道这是一个动态包。

```kotlin
// pages/users/user/PackageMapping.kt
@file:PackageMapping("{user}") // or @file:PackageMapping("{}")

package site.pages.users.user

import com.varabyte.kobweb.core.PackageMapping
```

如果你将空的 `"{}"` 传递给 `PackageMapping` 注解，它会指示 Kobweb 使用通常从包生成的路由段名称（即在此特定情况下为 `user`）。

### `@Page`

与 `PackageMapping` 类似，`Page` 注解也可以使用大括号来表示动态值。

```kotlin
// pages/users/user/posts/Post.kt

@Page("{post}") // Or @Page("{}")
@Composable
fun PostPage() {
   /* ... */
}
```

空的 `"{}"` 告诉 Kobweb 使用通常从文件名生成的 slug 名称（即在此特定情况下为 `post`）。

请记住，`Page` 注解允许你重写整个路由。该值也接受动态段，因此你甚至可以这样做：

```kotlin
// pages/users/user/posts/Post.kt

@Page("/users/{user}/posts/{post}") // Or @Page("/users/{user}/posts/{}")
@Composable
fun PostPage() {
    /* ... */
}
```

但强大的力量带来了巨大的责任。像这样的技巧可能很难找到和/或以后更新，特别是当你的项目变大时。虽然它有效，但你应该只在绝对需要的情况下使用这种格式（例如在代码重构后你必须支持旧的 URL 路径）。

### Querying dynamic route values

查询动态路由值的方式与请求查询参数完全相同。也就是说，使用 `ctx.params`：

```kotlin
@Page("{}")
@Composable
fun PostPage() {
    val ctx = rememberPageContext()
    val postId = ctx.route.params.getValue("post")
    /* ... */
}
```

> [!IMPORTANT]
> 你应该避免创建动态路径和查询参数同名的 URL 路径，如 `mysite.com/posts/{post}?post=...`，因为这在复杂项目中可能非常难以调试。如果存在冲突，则动态路由参数将优先。 （在这种情况下，如果需要，你仍然可以通过 `ctx.route.queryParams` 访问查询参数值。）

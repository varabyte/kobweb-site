---
follows: BasePath
imports:
  - com.varabyte.kobweb.silk.components.display.LeftBorderedCalloutVariant
  - com.varabyte.kobweb.silk.components.display.LeftBorderedFilledCalloutVariant
  - com.varabyte.kobweb.silk.components.display.MatchingLinkCalloutVariant
  - com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant
---

如果你在 `jsMain/resources/markdown` 文件夹下创建一个 Markdown 文件，在构建时会自动为你创建一个对应的页面，使用文件名作为其路径。

例如，如果我创建以下文件：

```markdown
// jsMain/resources/markdown/docs/tutorial/Kobweb.md

# Kobweb 教程

...
```

这将创建一个页面，你可以通过访问 `mysite.com/docs/tutorial/kobweb` 来访问它

## Front Matter

Front Matter 是你可以在文档开头指定的元数据，如下所示：

```text
---
title: 教程
author: bitspittle
---

...
```

然后你可以使用页面的 ${DocsLink("PageContext", "routing#page-context")} 来查询这些键值对：

```kotlin
@Composable
fun AuthorWidget() {
  val ctx = rememberPageContext()
  // 注意：只有当你确定这个 composable 是在由 Markdown 生成的
  // 页面内调用时，才可以使用 `markdown!!`
  val author = ctx.markdown!!.frontMatter.getValue("author").single()
  Text("作者：$author")
}
```

> [!IMPORTANT]
> 如果你没有看到 `ctx.markdown` 自动补全，你需要确保在项目的构建脚本中依赖了
> `com.varabyte.kobwebx:kobwebx-markdown` 工件。

### Root

在你的 front matter 中，有一个特殊的值 root，如果设置了它，将用于渲染一个根 `@Composable`，
它将剩余的 Markdown 代码作为其内容包装。这对于指定布局很有用，例如：

```text
---
root: .components.layout.DocsLayout
---

# Kobweb 教程
```

上面的代码将生成类似下面的代码：

```kotlin
@Composable
@Page
fun KobwebPage() {
    com.mysite.components.layout.DocsLayout {
    H1 {
      Text("Kobweb 教程")
    }
  }
}
```

> [!NOTE]
> 你可能注意到上面的代码路径前面有一个 `.`（这里是 `.components.layouts.DocsLayout`）。
> 当你在 Kobweb Markdown 中这样做时，框架会检测到并将其转换为你网站的完整包名。

如果你有一个想在大多数/所有 Markdown 文件中使用的默认 root，你可以在构建脚本的 `markdown` 块中指定它：

```kotlin
// site/build.gradle.kts

kobweb {
  markdown {
    defaultRoot.set(".components.layout.MarkdownLayout")
  }
}
```

### 路由覆盖

Kobweb Markdown 的 front matter 支持 `routeOverride` 键。如果存在，其值将被传递到生成的 `@Page` 注解中 ${DocsAside("Route override", "routing#route-override")}。

这允许你为 URL 指定一个普通 Kotlin 文件名规则不允许的名称，比如连字符：

`# AStarDemo.md`

```text
---
routeOverride: a*-demo
---
```

上面的代码将生成类似下面的代码：

```kotlin
@Composable
@Page("a*-demo")
fun AStarDemoPage() { /* ... */
}
```

## Kobweb 调用

Kotlin + Compose HTML 的强大之处在于交互式组件，而不是静态文本！因此，Kobweb Markdown 支持可用于在页面中插入实时 Kotlin 代码的特殊语法。

### 块语法

通常，你会定义独立的小部件，周围没有文本或其他组件。对于这种情况，使用三个大括号（把这个想象成 Markdown 的三个反引号语法，但是用于代码）：

```markdown
# Kobweb 教程

...

{{{ .components.widgets.VisitorCounter }}}
```

这将为你生成类似下面的代码：

```kotlin
@Composable
@Page
fun KobwebPage() {
  /* ... */
  com.mysite.components.widgets.VisitorCounter()
}
```

### 内联语法

有时，你可能想在单个句子的流程中插入一个较小的小部件。对于这种情况，使用 `${...}` 内联语法：

```markdown
按下 ${.components.widgets.ColorButton} 来切换网站的当前颜色。
```

> [!CAUTION]
> 大括号内不允许有空格！如果你在那里有空格，Markdown 会跳过整个内容并将其保留为文本。

## 导入

你可能希望向从 Markdown 生成的代码添加导入。Kobweb Markdown 支持注册*全局*导入（将添加到每个生成的文件中的导入）和*本地*导入（仅适用于单个目标文件的导入）。

### 全局导入

要注册全局导入，请在构建脚本中配置 `markdown` 块：

```kotlin
// site/build.gradle.kts

kobweb {
  markdown {
    imports.add(".components.widgets.*")
  }
}
```

上面的代码会确保每个生成的 Markdown 文件都有以下导入：

```kotlin
import com.mysite.components.widgets.*
```

导入可以帮助你简化 Kobweb 调用。重新看一下刚才的例子：

```markdown
# 不使用导入

按下 ${.components.widgets.ColorButton} 来切换网站的当前颜色。

# 使用导入

按下 ${ColorButton} 来切换网站的当前颜色。
```

### 本地导入

本地导入在 Markdown 的 front matter 中指定（甚至可以被 root 声明使用！）：

```text
---
root: DocsLayout
imports:
  - .components.layouts.DocsLayout
  - .components.widgets.VisitorCounter
---

...

{{{ VisitorCounter }}}
```

## 提示框

Kobweb Markdown 支持提示框，这是一种在文档中突出显示信息的方式。例如，你可以用它们来突出显示注释、提示、警告或重要消息。

要使用提示框，将引用文本的第一行设置为 `[!类型]`，其中*类型*是以下之一：

* CAUTION - 提醒用户需要特别注意的事项。
* IMPORTANT - 用户应该了解的重要上下文。
* NOTE - 用户应该注意的中性信息，即使是在快速浏览时。
* QUESTION - 留给读者思考的问题。
* QUOTE - 直接引用。
* TIP - 用户可能觉得有用的建议。
* WARNING - 用户应该知道以防止错误的信息。

```markdown
> [!NOTE]
> Lorem ipsum...

> [!QUOTE]
> Lorem ipsum...
```

> [!NOTE]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!QUOTE]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!TIP]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!IMPORTANT]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!QUESTION]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!CAUTION]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!WARNING]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

如果你想更改显示的默认标题的值，你可以在引号中指定它：

```markdown
> [!QUESTION "你知道吗..."]
```

> [!QUESTION "你知道吗..."]
> *这里是有趣的事实！*

另一个例子，使用引号时，你可以将标签设置为空字符串：

```markdown
> [!QUOTE ""]
> ...
```

这样看起来很简洁：

> [!QUOTE ""]
> 互联网上引用的问题在于你永远不知道它们是否真实。
>
> — 亚伯拉罕·林肯

如果你想指定一个应该全局应用的标签，你可以通过在项目的构建脚本中使用便利方法 `SilkCalloutBlockquoteHandler` 来覆盖块引用处理程序：

```kotlin
kobweb {
  markdown {
    handlers.blockquote.set(SilkCalloutBlockquoteHandler(labels = mapOf("QUOTE" to "")))
  }
}
```

> [!CAUTION]
> 提示框是由 Silk 提供的。如果你的项目不使用 Silk 并且你像这样覆盖块引用处理程序，它将生成导致编译错误的代码。

### 提示框变体

Silk 提供了一些提示框变体。

例如，一个带边框的变体：

> [!NOTE {variant = OutlinedCalloutVariant}]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

和一个填充变体：

> [!NOTE {variant = LeftBorderedFilledCalloutVariant}]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit。

你也可以将任何标准变体与附加的匹配链接变体组合（例如 `LeftBorderedCalloutVariant.then(MatchingLinkCalloutVariant)`），
这样提示框内的任何超链接都将匹配提示框本身的颜色：

> [!TIP {variant = LeftBorderedCalloutVariant.then(MatchingLinkCalloutVariant)}]
> 一个带有[示例链接](https://example.com)的简单提示框。

> [!TIP {variant = LeftBorderedFilledCalloutVariant.then(MatchingLinkCalloutVariant)}]
> 一个带有[示例链接](https://example.com)的简单提示框。

> [!TIP {variant = OutlinedCalloutVariant.then(MatchingLinkCalloutVariant)}]
> 一个带有[示例链接](https://example.com)的简单提示框。

如果你比较喜欢这些样式中的任何一种而不是默认样式，你可以在 `SilkCalloutBlockquoteHandler` 中设置 `variant` 参数。
例如，这里我们将其设置为带边框的变体：

```kotlin
kobweb {
  markdown {
    handlers.blockquote.set(SilkCalloutBlockquoteHandler(
      variant = "com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant")
    )
  }
}
```

你也可以在 Markdown 语法中使用大括号语法作为参数来指定变体：

```markdown
> [!NOTE {variant = com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant}]
```

当然，你也可以在自己的代码库中定义自己的变体并在这里使用。

### 自定义提示框

如果你想注册自定义提示框，这分两部分完成。

首先，在你的代码中某处声明你的自定义提示框设置：

```kotlin
package com.mysite.components.widgets.callouts

val CustomCallout = CalloutType(
    /* ... 在这里指定图标、标签和颜色 ... */
)
```

然后在你的构建脚本中注册它，用你的自定义提示框扩展默认的处理程序列表（即 `SilkCalloutTypes`）：

```kotlin
kobweb {
  markdown {
    handlers.blockquote.set(
      SilkCalloutBlockquoteHandler(types =
        SilkCalloutTypes +
          mapOf("CUSTOM" to ".components.widgets.callouts.CustomCallout")
      )
    )
  }
}
```

就这样！此时，你可以在 Markdown 中使用它：

```markdown
> [!CUSTOM]
> 很好。
```

## 遍历所有 Markdown 文件

在构建网站时处理所有 Markdown 文件可能非常有用。一个常见的例子是收集所有 Markdown 文章并从中生成一个列表页面。

你实际上可以使用纯 Gradle 代码来做这件事，但这种情况很常见，因此 Kobweb 通过 `markdown` 块的 `process` 回调提供了一个便利的 API。

你可以注册一个回调，它将在构建时触发，并获取项目中所有 Markdown 文件的列表。

```kotlin
kobweb {
  markdown {
    process.set { markdownEntries ->
      // `markdownEntries` 的类型是 `List<MarkdownEntry>`，
      // 其中一个条目包括文件的路径、它将被服务的路由
      // 以及任何解析的 front matter。

      println("处理 markdown 文件：")
      markdownEntries.forEach { entry ->
        println("\t* ${entry.filePath} -> ${entry.route}")
      }
    }
  }
}
```

在回调内部，你还可以调用 `generateKotlin` 和 `generateMarkdown` 方法，轻松创建将包含在最终网站中的文件。

这里是一个非常粗略的例子，为网站中的所有博客文章（在 `resources/markdown/blog` 文件夹下找到的）创建一个列表页面：

```kotlin
kobweb {
  markdown {
    process.set { markdownEntries ->
      generateMarkdown("blog/index.md", buildString {
        appendLine("# 博客索引")
        markdownEntries.forEach { entry ->
          if (entry.filePath.startsWith("blog/")) {
            val title = entry.frontMatter["title"] ?: "无标题"
            appendLine("* [$title](${entry.route})")
          }
        }
      })
    }
  }
}
```

参考[本站](https://github.com/varabyte/kobweb-site/blob/main/site/build.gradle.kts)的构建脚本，
搜索 "process.set" 以查看此功能在生产环境中的使用。
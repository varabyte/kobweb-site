---
description: 如何将你的 Kobweb 站点导出为可以由网络服务器提供服务的最终布局。
title: 项目导出
follows: ApplicationRoot
---

Kobweb 在 Compose HTML 基础上的一个主要增强是导出功能。

这个功能将框架从生成单页应用提升为生成完整的、可导航的网站。导出过程会对每个页面进行快照，从而提供更好的 SEO 支持和更快的初始渲染。

一个正常的开发工作流程是使用 `kobweb run` 来迭代开发你的网站，当准备发布时，使用 `kobweb export` 导出生产版本。

## 具体导出示例

让我们详细了解这个过程，以便理解其工作原理。

如果你不使用 Kobweb 而是直接使用 Compose HTML，建议创建一个如下所示的 `index.html` 文件：

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>My Site Title</title>
</head>
<body>
<div id="root"></div>
<script src="mysite.js"></script>
</body>
</html>
```

> [!NOTE]
> 例如，你可以在[官方《入门指南》说明](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/HTML/Getting_Started#6-add-the-indexhtml-file-to-the-resources)中找到完全相同的结构。

这段代码声明了一个根 `<div>` 元素，其子元素将在运行时动态填充。文件末尾的 `mysite.js` 脚本包含了生成网站每个页面所需的所有逻辑。

这种方法非常强大，但是使用这种方式构建网站时，你会遇到两个主要问题：

1. 随着代码库的增长，`mysite.js` 会变得越来越大，这意味着在网站渲染之前需要下载更大的文件。在脚本运行之前，初始视图将只是一个空白页面，这取决于脚本的大小和用户的下载速度。
2. 搜索引擎更难索引你的网站，因为在 JavaScript 执行之前它们无法看到内容。任何不执行 JavaScript 的网络爬虫永远只能看到空白页面。

好的，让我们引入 Kobweb。这里，我们构建一个非常简单的页面并导出我们的网站（使用 `kobweb export`）来看看会发生什么。

```kotlin
@Page
@Composable
fun ExampleKobwebPage() {
    Text("这是一个用于演示导出功能的最小示例。")
}
```

导出会在你的 `kobweb/.site` 文件夹下生成以下 HTML（这里省略了大量样式）：

```html
<!doctype html>
<html lang="en">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>My Site Title</title>
  <meta content="Powered by Kobweb" name="description">
  <link href="/favicon.ico" rel="icon">
  <meta content="width=device-width, initial-scale=1" name="viewport">
 </head>
 <body>
  <div id="root" style="...">
   <style>...</style>
   <div class="..." style="min-height: 100vh;">
    这是一个用于演示导出功能的最小示例。
   </div>
  </div>
  <script src="/mysite.js"></script>
 </body>
</html>
```

如你所见，Kobweb 填充了许多额外信息，不过网站脚本仍然链接在文件底部。这很重要，因为如前所述，它包含了不仅渲染这个页面，而且渲染整个网站所需的所有信息。

换句话说，你可以只下载这个页面，然后继续浏览网站的其他部分，而无需下载更多文件。

简而言之，导出过程会发现你代码库中所有带有 `@Page` 注解的方法，并为每个方法生成快照。你可以将每个快照视为一个对 SEO 友好的起点，从这里你可以访问网站的其余部分。

## 导出需要浏览器

为了让 Kobweb 导出能够获取你网站的快照，它需要以无头模式启动浏览器。这个浏览器负责加载简单的 Compose HTML 版本的 `index.html` 页面并运行其 JavaScript 来填充页面。然后浏览器会被查询最终的 html，Kobweb 将其保存到磁盘。

Kobweb 将这项任务的大部分工作委托给了微软优秀的 [Playwright 框架](https://playwright.dev/)。希望对大多数用户来说这是不可见的，但对于高级用例，了解底层运行的技术可能会很有用。

对于自定义 CI/CD 设置，你至少需要知道 Kobweb 导出过程需要浏览器。对于想了解更多信息的用户， ${DocsLink("我们在后面的指南中分享了一个具体示例", "/kobweb/guides/git-hub-workflow-export")}。

## 静态布局与全栈网站

Kobweb 网站有两种类型：*静态*和*全栈*。

### 静态布局网站

*静态*网站（或更完整地说，*静态布局*网站）是指你将一堆前端文件（例如 `html`、`js` 和公共资源）导出到一个单一的、组织良好的文件夹中，这个文件夹直接由[静态网站托管提供商](https://en.wikipedia.org/wiki/Web_hosting_service#Static_page_hosting)提供服务。

换句话说，你不需要编写任何服务器代码。在这种情况下，服务器是为你提供的，它使用一个相当简单的算法 - 它将你上传的所有内容作为原始的静态资源提供服务。

*静态*这个名称并不是指你的网站的行为，而是指你的托管提供商解决方案的行为。如果有人请求一个页面，每次都会提供相同的响应字节（即使该页面充满了允许它以非常交互方式运行的自定义代码）。

### 全栈网站

*全栈*网站是指你同时编写在前端（即用户机器上）运行的逻辑和在后端（即某个服务器上）运行的逻辑。这个自定义服务器必须至少提供请求的文件（与静态网站托管服务完全相同的工作），而且可能还定义了为你网站需求定制的端点功能。

例如，也许你定义了一个端点，给定用户 ID 和认证令牌，返回该用户的个人资料信息。

### 为你的项目选择合适的网站布局

当 Kobweb 最初编写时，它只提供全栈解决方案，因为能够编写自己的服务器逻辑可以提供最大的功能和灵活性。在这个早期阶段，使用 Kobweb 的思维模型简单明了。

然而，在实践中，大多数项目并不需要全栈设置提供的强大功能。仅仅通过编写响应式前端逻辑，一个网站就可以给用户提供非常清晰、动态的体验，例如通过动画和令人愉悦的用户交互。

此外，多年来出现了许多"*功能*即服务"解决方案，它们可以提供大量便利的功能，这些功能过去需要自定义服务器。如今，你可以轻松集成身份验证、数据库和分析解决方案，而无需编写一行后端代码。

以静态网站托管提供商可以使用的方式导出文件的过程往往比使用全栈解决方案*快得多*而且*便宜得多*。因此，除非你有特定需求需要全栈方法，否则应该优先选择静态网站布局。

使用自定义服务器（因此，使用全栈方法）的一些可能原因是：
* 需要与公司内部的其他私有后端服务通信。
* 作为某个第三方服务的中间人拦截请求，其中你拥有不想泄露的非常敏感的 API 密钥（比如委托给 ChatGPT 的服务）。
* 作为连接多个客户端的中心（比如聊天服务器）。

如果你不确定属于哪个类别，那么你可能应该创建一个静态布局网站。从静态布局网站迁移到全栈网站比反过来要容易得多。

## 导出和运行

静态和全栈两种类型的网站都需要导出。

要以静态布局导出你的网站，使用 `kobweb export --layout static` 命令，而对于全栈，命令是 `kobweb export --layout fullstack`（或者只用 `kobweb export`，因为 `fullstack` 是默认布局，因为它最初是唯一的方式）。

导出后，你可以在上传之前通过本地运行来测试你的网站。使用 `kobweb run --env prod --layout static` 运行静态网站，使用 `kobweb run --env prod --layout fullstack`（或者只用 `kobweb run --env prod`）运行全栈网站。

### `PageContext.isExporting`

有时，你有一些行为应该在实际用户浏览你的网站时运行，但在导出时*不*运行。例如，也许你为登录用户提供认证体验，但在导出时永远不会有登录用户。

你可以通过检查 `PageContext.isExporting` 属性来判断你的页面是否正在作为导出的一部分被渲染。这让你有机会操作导出的 HTML 或避免与页面加载相关的副作用。

```kotlin
@Composable
fun AuthenticatedLayout(content: @Composable () -> Unit) {
    var loggedInUser by remember { mutableStateOf<User?>(null) }

    val ctx = rememberPageContext()
    if (!ctx.isExporting) {
        LaunchedEffect(Unit) {
            loggedInUser = checkForLoggedInUser() // <- 一个慢速、昂贵的方法
        }
    }

    if (loggedInUser == null) {
        LoggedOutScaffold { content() }
    } else {
        LoggedInScaffold(user) { content() }
    }
}
```

## 动态路由和导出

导出过程会跳过动态路由。毕竟，无法知道可能传递给动态路由的所有可能值。

但是，如果你有一个想要导出的特定动态路由实例，你可以按如下方式配置你网站的构建脚本：

```kotlin
kobweb {
  app {
    export {
      // "/users/{user}/posts/{post}" 对 "default" / "0" 情况有特殊处理
      addExtraRoute("/users/default/posts/0", exportPath = "users/index.html")
    }
  }
}
```

## 部署

静态网站默认导出到 `.kobweb/site`（如果你愿意，可以在 `.kobweb/conf.yaml` 文件中配置这个位置）。然后你可以将该文件夹的内容上传到你选择的静态网站托管提供商。

部署全栈网站稍微复杂一些，因为不同的提供商有着截然不同的设置，有些用户甚至可能决定自己运行 Web 服务器。不过，当你导出你的 Kobweb 网站时，会生成用于运行服务器的脚本，包括 *nix 平台（`.kobweb/server/start.sh`）和 Windows 平台（`.kobweb/server/start.bat`）。如果你使用的提供商支持 Dockerfile，你可以将 `ENTRYPOINT` 设置为这些脚本中的任何一个（取决于服务器的平台）。

更详细的内容超出了本 README 的范围。不过，你可以阅读我的博客文章了解更多信息和一些清晰、具体的示例：

* [使用 Kobweb 进行静态网站生成和部署](https://bitspittle.dev/blog/2022/static-deploy)
* [将 Kobweb 部署到云端](https://bitspittle.dev/blog/2023/cloud-deploy)

## 导出追踪

Kobweb 的导出功能建立在 [Microsoft Playwright](https://playwright.dev/) 之上，这是一个使以编程方式下载和运行浏览器变得容易的解决方案。

Playwright 提供的功能之一是生成追踪，这些追踪本质上是你可以用来了解网站加载过程中发生的事情的详细报告。Kobweb 通过 Kobweb 应用程序构建脚本中的 `export` 块公开了这个功能。

启用追踪很简单：

```kotlin
// build.gradle.kts
plugins {
  // ... 其他插件 ...
  alias(libs.plugins.kobweb.application)
}

kobweb {
  app {
    export {
      enableTraces()
    }
  }
}
```

你可以传入参数来配置 `enableTraces` 方法，但默认情况下，它会将追踪文件生成到你的 `.kobweb/export-traces/` 目录中。

启用后，你可以运行 `kobweb export`，然后一旦导出完成，使用操作系统的文件浏览器导航到生成的任何 `*.trace.zip` 文件，并将它们拖放到 [Playwright Trace Viewer](https://trace.playwright.dev/) 中。

> [!TIP]
> 你可以通过[官方文档](https://playwright.dev/docs/trace-viewer)了解更多关于如何使用 Trace Viewer 的信息。

预计不会有很多用户需要调试他们的网站导出，但这是一个很好的工具（特别是与 ${DocsLink("服务器日志记录", "/kobweb/concepts/server/fullstack#server-logs")} 结合使用），可以诊断如果你的某个页面导出时间比预期长的原因。

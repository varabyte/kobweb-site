---
description: 了解 Kobweb 项目的高层结构。
title: 项目结构
follows: Index
---

第一次尝试了解 Kobweb 项目？本页面将帮助你理解每个 Kobweb 项目的高层结构。

## `.kobweb` 文件夹

你的站点将包含一个 `.kobweb` 文件夹，它既是重要配置文件的存放位置，也是生成的输出文件的存放位置。

{{{ Folders

* my-project/site
  * .kobweb
    * conf.yaml
    * server
    * site

}}}

> [!NOTE]
> 在你构建/导出项目之前，`server` 和 `site` 文件夹是不存在的。

### conf.yaml

`conf.yaml` 文件，也称为 "Kobweb conf" 文件，是非常重要的。它包含了 Kobweb 服务器所需的配置，如果该文件不存在，Kobweb 服务器将无法运行。

客户端也会使用其中的一些值。但是，这些值也会被服务器引用。如果某个值只被客户端需要而服务器不需要，那么它将存在于 Gradle 构建脚本中。

具体的 `conf.yaml` 值将在这些文档的相关章节中讨论。

### server

`server` 文件夹包含了启动服务器的实用脚本（一个 `.sh` 文件和一个 `.bat` 文件），当你在 CI 环境中运行 Kobweb 项目时（例如在 Docker 容器内）这些脚本会非常有用。这个文件夹也是
${DocsLink("服务器日志", "../server/fullstack#服务器日志")} 会写入到这里，以便你需要查看它们时使用。

### site

`site` 文件夹包含了你的网站的最终输出，这些输出是在 ${DocsLink("导出", "exporting")} 之后生成的。

## Components and Pages

Kobweb 站点当然需要声明网页 ${DocsAside("Page", "routing#page")} -- 没有网页的话就不能称之为 Web 框架了！这些网页文件将位于项目的 `jsMain` 源代码集的 `pages` 文件夹下。

在 `pages` 文件夹之外，通常会创建可重用的、可组合的部件。虽然 Kobweb 在这里并没有强制执行任何特定规则，但我们建议遵循一个约定，这样可以让新接触你代码库的人更容易上手。

因此，作为 `pages` 的同级目录，你应该有一个名为 `components` 的文件夹。在其中：

* **layouts** - 提供完整页面布局的高级可组合组件。大多数（全部？）的`@Page`页面都会首先调用一个页面布局函数。你的整个网站可能只需要一个布局。
* **sections** - 中级可组合组件，代表页面内的复合区域，组织多个子可组合组件的集合。如果你有多个布局，sections很可能会在它们之间共享。例如，导航头部和页脚是这个子文件夹的理想候选。
* **widgets** - 低级可组合组件。专注于你可能想在整个网站中重用的UI部件。例如，一个样式化的访问计数器就是这个子文件夹的好选择。

换句话说，你的项目应该看起来像这样：

{{{ Folders

* my-project/site
  * src/jsMain/kotlin
    * components
      * layouts
      * sections
      * widgets
    * pages

}}}

## 公共资源

如果你有想要从网站提供的资源，你可以通过将其放在网站的`jsMain/resources/public`文件夹中来处理。

例如，如果你有一个logo想要在`mysite.com/assets/images/logo.png`上可用，你需要将它放在Kobweb项目的`jsMain/resources/public/assets/images/logo.png`中。

{{{ Folders

* my-project/site
  * src/jsMain/resources
      * public
        * assets/images
          * logo.png

}}}

换句话说，项目资源中`public/`目录下的任何内容都会自动复制到最终网站中（但不包括`public/`部分）。

## API 接口

如果你的项目还提供后端 ${DocsAside("全栈", "../server/fullstack")}，那么你的项目中应该有一个
`jvmMain` 文件夹。API 接口在 Kobweb 中由
${DocsLink("定义 API 路由", "../server/fullstack#定义-api-路由")}，将位于 `api` 文件夹下。

{{{ Folders

* my-project/site
  * src/jvmMain/kotlin
    * api

}}}

## 多模块项目

为了简单起见，大多数新项目会将所有页面和组件放在单个应用模块中，例如`site/`，在其构建脚本中应用`com.varabyte.kobweb.application`插件。

但是，你也可以在单独的模块中定义组件和/或页面。只需在它们的构建脚本中应用`com.varabyte.kobweb.library`插件即可。

换句话说，你可以像这样分割和组织你的项目：

{{{ Folders

* my-project
  * sitelib
    * build.gradle.kts
    * src/jsMain
      * kotlin/org/example/myproject/sitelib
        * components
        * pages
  * site
    * build.gradle.kts
    * .kobweb/conf.yaml
    * src/jsMain
      * kotlin/org/example/myproject/site
        * components
        * pages

}}}

如果你想探索多模块项目示例，可以通过运行以下命令：

```bash
$ kobweb create examples/chat
```

这演示了一个聊天应用程序，其认证和聊天功能分别组织在各自独立的模块中。

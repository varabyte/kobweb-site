---
title: 什么是 Kobweb 
#routeOverride: docs
---
Kobweb 是一个基于 [Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html) 构建的 Kotlin 网站和 Web 应用程序开发框架，其设计理念受到 [Next.js](https://nextjs.org) 和 [Chakra UI](https://chakra-ui.com) 的启发。

这意味着你可以使用 Compose（一个优雅的响应式库，对大多数 Android 开发者来说应该很熟悉）来声明 HTML 结构，同时依托 Kobweb 框架提供的功能，如页面路由、客户端/服务器通信、明暗主题支持以及现代化的组件集。

{{{ .components.sections.home.HeroCode }}}

{{{ .components.sections.home.HeroExample }}}

## 目标

我们开发 Kobweb 的目的是为了让使用 Compose HTML 变得更加愉悦。我们希望让更多开发者能够自信地选择 Kotlin 来创建现代化网站。

> [!NOTE]
> 要了解更多关于 Compose HTML 的信息，请访问[官方教程](https://github.com/JetBrains/compose-jb/tree/master/tutorials/HTML/Getting_Started)。

Compose HTML 是一个非常强大的库，但它将许多基本决策留给了开发者。表面上看这似乎是个不错的方式，但这些选择通常是我们大多数人并不太关心的，反而成了额外的负担。

例如，当我们最初评估 Kobweb 的愿景是否可行时，我们希望简化页面路由的设置，消除编写虚拟 `index.html` 文件的需求，支持使用 CSS 为 HTML 元素添加样式而无需创建全局样式表对象，并将热重载作为一等功能支持。

当你创建 Kobweb 项目时，这些功能（以及更多功能）都是开箱即用的。这些问题真的是你想自己解决的吗？

简而言之，我们希望解决所有繁琐的工作，让你能够专注于设计和开发网站中更有趣的部分！

## 高层结构

Kobweb 不仅是一个库集合，还提供了 Gradle 插件和 KSP 处理器，它们在编译时自动分析你的代码库，为项目生成所有必要的样板代码。

Kobweb 还是一个同名的 CLI 二进制文件，让你可以发出命令来处理构建和运行 Compose HTML 应用程序的繁琐部分（例如 `kobweb run`）。

## 目标

我们旨在提供：

* 一个用于组织 Kotlin 网站或 Web 应用程序的直观结构
* 自动处理页面间的路由
* 基于 Compose HTML 构建的实用组件集合
* 一个从头开始围绕热重载构建的环境
* 支持静态站点导出，以改善 SEO 和降低服务器成本
* 支持响应式（即移动端和桌面端）设计
* 客户端和服务器之间共享丰富的类型
* 开箱即用的 Markdown 支持
* 轻松定义服务器 API 路由的方式
* 一个社区可以扩展的开源基础
* 以及更多功能！

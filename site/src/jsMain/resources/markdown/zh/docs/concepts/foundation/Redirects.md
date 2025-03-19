---
description: 如何在 Kobweb 站点中定义重定向。
title: 重定向
follows: Workers
---

在网站的生命周期中，您可能会发现需要更改其结构。也许您需要将一些页面移动到新文件夹下，或者需要重命名页面等。

但是，如果您的网站已经运行一段时间，您可能会有大量指向这些页面的内部链接。更糟糕的是，整个网络（比如 Google 搜索结果，或博客和文章）可能充满了指向这些旧位置的链接，所以即使您能找到并修复您这边的所有内容，您也无法控制其他人的行为。

web 长期以来一直支持重定向的概念来处理这种情况。通过公开宣布您更改了哪些链接，搜索索引可以得到更新，即使有人访问旧位置的页面，您的服务器也可以自动告诉浏览器他们应该去的正确位置。

在 Kobweb 中，您可以在项目的 `.kobweb/conf.yaml` 文件中定义重定向。您只需在 `server.redirects` 块中定义一系列 `from` 和 `to` 值。

```yaml
server:
  redirects:
    - from: "/old-page"
      to: "/new-page"
```

Kobweb 服务器将从 `conf.yaml` 文件中获取这些重定向值，并拦截任何匹配的传入路由请求，向客户端发送 [301 状态码](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/301)。

因此，在上面的例子中，如果用户尝试访问 `https://example.com/old-page`，他们将自动被重定向到 `https://example.com/new-page`。您网站上任何引用旧页面的内部链接也会被处理——尝试导航到旧位置将自动到达新位置。

Kobweb 重定向功能还支持在 `from` 值中使用正则表达式，然后可以在 `to` 部分使用 `$1`、`$2` 等变量，这些变量将被替换为括号中的文本匹配。

如果您想将网站的整个部分重定向到新位置，分组匹配会非常有用。例如，如果您将所有页面从旧的父文件夹移动到新文件夹，以下重定向规则可以帮助您：

```yaml
server:
  redirects:
    - from: "/socials/facebook/([^/]+)"
      to: "/socials/meta/$1"
```

最后要注意的是，如果您有多个重定向，它们将按顺序处理并全部应用。在大多数情况下这应该不会有什么影响，但如果您需要同时更改文件夹名称和页面名称，您可以使用它：

```yaml
server:
  redirects:
    - from: "/socials/facebook/([^/]+)"
      to: "/socials/meta/$1"
    - from: "(/socials/meta)/about-facebook"
      to: "$1/about-meta"
```

> [!IMPORTANT]
> 如果您使用第三方静态托管提供商来托管您的网站，他们将无法识别 Kobweb 的 `conf.yaml` 文件，因此您需要阅读他们的文档以了解如何配置重定向。
>
> 在这种情况下，您可能可以跳过在自己的 Kobweb 配置文件中定义重定向，因为此时可能是多余的。但是，出于文档目的和确保您不会因为忘记更新的旧内部链接而导致 404 错误，这样做可能仍然有用。

---
title: "基础路径"
follows: PersistingState
---

通常情况下，网站都位于顶层。这意味着如果您有一个根文件 `index.html`，并且您的网站托管在域名 `https://mysite.com` 下，那么可以通过访问 `https://mysite.com/index.html` 来访问该HTML文件。

然而，在某些情况下，您的网站可能托管在一个子文件夹下，例如 `https://example.com/products/myproduct/`，这种情况下，您网站的根 `index.html` 文件将位于 `https://example.com/products/myproduct/index.html`。

Kobweb 需要知道这种子文件夹结构，以便在其路由逻辑中考虑到这一点。这可以在您项目的 `.kobweb/conf.yaml` 文件中通过 `site` 部分下的 `basePath` 值来指定：

```yaml
site:
  title: "..."
  basePath: "..."
```

其中 `basePath` 的值是URL的源部分和您网站根目录之间的部分。例如，如果您的网站根目录在 `https://example.com/products/myproduct/`，那么 `basePath` 的值应该是 `products/myproduct`。

> [!TIP]
> GitHub Pages 是开发者常用的网站托管解决方案。默认情况下，这种方式会将您的网站托管在一个子文件夹下（设置为项目名称）。
>
> 换句话说，如果您计划在 GitHub Pages 上托管您的 Kobweb 网站，您需要设置一个适当的 `basePath` 值。
>
> 对于设置 GitHub Pages 特定的 `basePath` 的具体示例，可以[查看我博客站点中的这个相关部分](https://bitspittle.dev/blog/2022/static-deploy#base-path)，其中详细介绍了这个过程。

一旦您在 `conf.yaml` 文件中设置了 `basePath`，通常可以在设计网站时不用显式提到它，因为 Kobweb 提供了能够处理基础路径的组件。例如，`Link("/docs/manuals/v123.pdf")` 会自动解析为 `https://example.com/products/myproduct/docs/manuals/v123.pdf`。

当然，您可能会发现自己在使用一些不支持基础路径的外部 Kobweb 代码。如果您需要在自己的代码中显式访问基础路径值，可以使用 `BasePath.value` 属性或调用 `BasePath.prepend` 伴生方法。

```kotlin
// Video 元素来自 Compose HTML，它不支持基础路径。
// 因此，我们需要手动将基础路径添加到视频源中。
Video(attrs = {
    width(320)
    height(240)
}) {
    Source(attrs = {
        attr("type", "video/mp4")
        attr("src", BasePath.prepend("/videos/demo.mp4"))
    })
 }
```

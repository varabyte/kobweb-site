---
title: 提供自定义字体
follows: Debugging
---

在网站上使用自定义字体是很常见的需求。设置起来相对简单，这里我们提供两种推荐的方法。

## 字体托管服务

使用自定义字体最简单的方式是使用已托管的字体服务。例如，Google Fonts 提供了一个 CDN，你可以直接用它来加载字体。

> [!CAUTION]
> 虽然这是最简单的方法，但请确保你不会遇到合规性问题！如果你在网站上使用 Google Fonts，在欧洲你可能会违反 GDPR，
> 因为欧盟公民的 IP 地址会被传输给 Google 并被记录。你可能需要寻找一个符合欧洲安全标准的托管服务，或者选择自托管方式，
> 这部分内容将在下一节中介绍。

字体服务会提供需要添加到网站 `<head>` 标签中的 HTML 代码。例如，当我选择 Roboto Regular 400 时，Google Fonts 建议添加以下代码：

```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
```

这些代码需要转换成 Kotlin 并添加到你网站的 `build.gradle.kts` 脚本的 `kobweb` 块中：

```kotlin
kobweb {
  app {
    index {
      head.add {
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
        link(
          href = "https://fonts.googleapis.com/css2?family=Roboto&display=swap",
          rel = "stylesheet"
        )
      }
    }
  }
}
```

完成后，你就可以在代码中引用这个新字体了：

```kotlin
Column(Modifier.fontFamily("Roboto")) {
    Text("Hello world!")
}
```

## 自托管字体

用户可以通过 CSS 的 [`@font-face` 规则](https://developer.mozilla.org/en-US/docs/Web/CSS/@font-face) 灵活地声明自定义字体。

在 Kobweb 中，你通常可以在 Kotlin 代码中声明 CSS 属性（在 `@InitSilk` 块中），但不幸的是，Firefox 不允许在代码中定义或修改
`@font-face` 条目（[相关 Bugzilla 问题](https://bugzilla.mozilla.org/show_bug.cgi?id=443978)）。因此，为了保证跨平台兼容性，
你应该创建一个 CSS 文件并在构建脚本中引用它。

让我们用一个具体的例子来说明，假设你从 Google Fonts 下载了开源字体 [Lobster](https://fonts.google.com/specimen/Lobster)
（当然也包括它的许可证）。

你需要将字体文件放在公共资源目录中，这样访问你网站的用户才能找到它。我们推荐以下文件组织方式：

{{{ Folders

* jsMain
  * resources
    * public
      * fonts
        * faces.css
        * lobster
          * OFL.txt
          * Lobster-Regular.ttf

}}}

其中 `faces.css` 包含所有 `@font-face` 规则定义（目前我们只有一个）：

```css
@font-face {
  font-family: 'Lobster';
  src: url('/fonts/lobster/Lobster-Regular.ttf');
}
```

> [!NOTE]
> 如果你确定将来只会使用一种字体，上述布局可能显得有点过于复杂。但是如果你决定在将来添加更多字体，这种结构具有足够的灵活性，
> 这就是为什么我们在这里推荐这种通用的方法。

现在，你需要在 `build.gradle.kts` 脚本中引用这个 CSS 文件：

```kotlin
kobweb {
  app {
    index {
      head.add {
        link(rel = "stylesheet", href = "/fonts/faces.css")
      }
    }
  }
}
```

此时，你就可以在代码中引用这个字体了：

```kotlin
Column(Modifier.fontFamily("Lobster")) {
    Text("Hello world!")
}
```

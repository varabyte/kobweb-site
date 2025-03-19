---
description: 如何声明一个将被每个页面调用的入口点。
title: 应用入口
follows: Routing
---

## `KobwebApp` 和 `SilkApp`

默认情况下，Kobweb 会自动将每个页面根设置为 [`KobwebApp` 可组合函数](https://github.com/varabyte/kobweb/blob/main/frontend/kobweb-core/src/jsMain/kotlin/com/varabyte/kobweb/core/App.kt)
（如果使用 Silk，则设置为 [`SilkApp` 可组合函数](https://github.com/varabyte/kobweb/blob/main/frontend/kobweb-silk/src/jsMain/kotlin/com/varabyte/kobweb/silk/SilkApp.kt)）。
这些函数会执行一些在整个网站中都应该存在的最小通用工作（例如应用 CSS 样式）。

这意味着如果你注册一个页面：

```kotlin
// jsMain/kotlin/com/mysite/pages/Index.kt

@Page
@Composable
fun HomePage() {
    /* ... */
}
```

那么实际运行在你网站上的最终结果将是：

```kotlin
// 在某处生成的 main.kt 中...

KobwebApp {
  HomePage()
}
```

## `@App`

你可能想要为自己的应用程序进一步配置这个根组件。也许你有一些想要在任何页面运行之前执行的初始化逻辑（比如将保存的设置更新到本地存储的逻辑）。对于许多应用程序来说，这是指定全屏 Silk `Surface` 的好地方，因为这样可以让其下的所有子组件在明暗模式之间平滑过渡。

在这种情况下，你可以创建自己的根可组合函数并用 `@App` 注解它。如果存在这样的根组件，Kobweb 将使用它而不是默认的根组件。当然，你应该委托给 `KobwebApp`（或者如果使用 Silk 则委托给 `SilkApp`），因为这些方法中的初始化逻辑仍然需要运行。

这是我在自己的许多项目中使用的应用程序根组件示例：

```kotlin
@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
  SilkApp {
    val colorMode = ColorMode.current
    LaunchedEffect(colorMode) { // 每当颜色模式改变时重新启动
      localStorage.setItem("color-mode", colorMode.name)
    }

    // 全屏 Silk surface。根据 Silk 的调色板设置背景并使颜色变化动画化。
    Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
      content()
    }
  }
}
```

你的网站最多只能定义一个 `@App`，否则 Kobweb Application 插件会在构建时报错。

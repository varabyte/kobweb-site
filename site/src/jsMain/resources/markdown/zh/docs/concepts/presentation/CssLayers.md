---
description: 理解和声明 Silk 中的 CSS 层。
title: CSS 层
follows: LearningCss
---

[CSS 层](https://developer.mozilla.org/en-US/docs/Web/CSS/@layer)是一个非常强大但相对较新的 CSS 特性，它允许将 CSS 样式规则包装在命名层中，以控制它们的优先级。简而言之，CSS 层是您指定顺序的任意名称。在处理相互冲突的 CSS 样式规则时，这是一个特别有用的工具。

Compose HTML 不支持 CSS 层，但 Silk 支持！即使您在自己的项目中从未直接使用过层，Silk 使用它们，因此用户仍然可以从这个特性中受益。

## 默认层

默认情况下，Silk 定义了六个层（从最低到最高优先级排序）：

1. reset
2. base
3. component-styles
4. component-variants
5. restricted-styles
6. general-styles

*reset* 层用于定义 CSS 规则，这些规则用于补偿相互不一致的浏览器默认值，或覆盖由于传统原因而存在但现代网页设计已经摒弃的值。

*base* 层实际上未被 Silk 使用（这可能会在未来改变），但它作为一个有用的位置提供给用户，用于定义应该容易被项目中其他地方定义的任何 CSS 规则覆盖的全局样式。

接下来的四种样式与各种 `CssStyle` 定义相关联：

```kotlin
interface SomeKind : ComponentKind
val SomeStyle = CssStyle<SomeKind> { /* ... */ } // "component-styles"
val SomeVariant = SomeStyle.addVariant { /* ... */ } // "component-variants"
class ButtonSize(/*...*/) : CssStyle.Base(/*...*/) // "restricted-styles"
val GeneralStyle = CssStyle { /* ... */ } // "general-styles"
```

我们选择这个顺序是为了确保 CSS 样式按照直觉的方式分层；例如，样式的变体总是会层叠在基本样式之上；同时，用户声明的 `CssStyle` 总是会层叠在 Silk 定义的组件样式之上。

## 注册层

您可以在 `@InitSilk` 方法中使用 `cssLayers` 属性注册自己的自定义层：

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.stylesheet.cssLayers.add("theme", "layout", "utilities")
}
```

在声明新层时，您可以相对于现有层来锚定它们。例如，如果您想在 Silk 的 *base* 层和它的 `CssStyle` 层之间插入层：

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.stylesheet.cssLayers.add("third-party", after = SilkLayer.BASE)
}
```

## `@CssLayer` 注解

如果您需要影响 `CssStyle` 块的层，可以使用 `@CssLayer` 注解标记它：

```kotlin
@CssLayer("important")
val ImportantStyle = CssStyle { /* ... */ }
```

> [!IMPORTANT]
> 您应该始终明确注册您的层。因此，对于上面的代码，您还应该在项目中的其他地方声明：
> ```kotlin
> @InitSilk
> fun initSilk(ctx: InitSilkContext) {
>   ctx.stylesheet.cssLayers.add("important")
> }
> ```
>
> 如果不这样做，浏览器会将任何未知层追加到 CSS 层列表的末尾（这是最高优先级位置）。在很多情况下这可能没问题，但明确表达您的意图既清晰，又减少了当未来开发者添加新层时，您的网站以微妙方式崩溃的可能性。
>
> 如果 Silk 检测到任何未注册的层，将在控制台打印警告。

## `layer` 块

`@InitSilk` 块允许您注册通用 CSS 样式。您可以使用 `layer` 块将它们包装在层中：

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        cssLayers.add("headers")
        layer("headers") {
            registerStyle("h1") { /* ... */ }
            registerStyle("h2") { /* ... */ }
        }
    }
}
```

当然，您可以将样式与现有层关联，比如我们前面提到的 *base* 层：

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        layer(SilkLayer.BASE) {
            registerStyle("div") { /* ... */ }
            registerStyle("span") { /* ... */ }
        }
    }
}
```

## 将第三方样式导入到层中

最后，如果您正在使用第三方 CSS 样式表，将它们包装在自己的层中可能是一个非常有用的技巧。

例如，假设您正在与一个第三方库发生冲突，该库的样式有点太激进，干扰了您自己的样式。

首先，在您的构建脚本中，使用 Kobweb 的 `importCss` 函数导入样式表，该函数内部使用 CSS [`@import` at-rule](https://developer.mozilla.org/en-US/docs/Web/CSS/@import)：

```kotlin
// 之前
kobweb.app.index.head.add {
  link(href = "/highlight.js/styles/dracula.css", rel = "stylesheet")
}

// 之后
kobweb.app.index.head.add {
  style {
    importCss("/highlight.js/styles/dracula.css", layerName = "highlightjs")
  }
}
```

然后，在 `@InitSilk` 块中注册您的新层。

```kotlin
@InitSilk
fun initBuildScriptLayers(ctx: InitSilkContext) {
    // 在 build.gradle.kts 中引用的层
    ctx.stylesheet.cssLayers.add("highlightjs", after = SilkLayer.BASE)
}
```

您已经驯服了一些狂野的 CSS 样式。恭喜！


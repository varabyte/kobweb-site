---
title: 学习 CSS
follows: Silk
imports:
  - com.varabyte.kobweb.site.components.widgets.docs.css.*
---

许多刚接触网页开发的开发者都听说过关于 CSS 的恐怖故事，他们可能希望 Kobweb 通过利用 Kotlin 和类似 Jetpack Compose 的 API，就意味着他们不需要学习 CSS。

值得澄清的是：CSS 是不可避免的！

话虽如此，CSS 的名声可能比实际糟糕得多。它的许多特性实际上相当简单直观，有些还非常强大。例如，你可以高效地声明你的元素应该有一个细边框，圆角，在下方投射阴影以营造深度感，使用渐变效果作为背景，并添加一个摆动的动画效果。

我们希望，当你通过 Kobweb 学习了一些 CSS 之后，你会发现自己实际上会享受它（至少有时候是这样）！

## Kobweb 如何帮助处理 CSS

Kobweb 提供了足够的抽象层，让你可以更加渐进地学习 CSS。

首先也是最重要的是，Kobweb 为你提供了一个 Kotlin 风格的类型安全的 CSS 属性 API。这比在文本文件中编写 CSS（在运行时静默失败）要好得多。

其次，像 `Box`、`Column` 和 `Row` 这样的布局组件可以让你在尚未理解"弹性布局"是什么之前就能快速构建丰富、复杂的布局。

同时，使用 `CssStyle` 可以帮助你将 CSS 分解成更小、更易管理的部分，这些部分与实际使用它们的代码紧密相关，使你的项目避免出现庞大的单一 CSS 文件。（这种庞大的 CSS 文件是 CSS 给人留下令人生畏印象的原因之一）。

例如，一个可能看起来像这样的 CSS 文件：

```css
/* Dozens of rules... */

.important {
  background-color: red;
  font-weight: bold;
}

.important:hover {
  background-color: pink;
}

/* Dozens of other rules... */

.post-title {
    font-size: 24px;
}

/* A dozen more more rules... */
```

可以在 Kobweb 中转变为：

```kotlin
//------------------ CriticalInformation.kt

val ImportantStyle = CssStyle {
  base {
    Modifier.backgroundColor(Colors.Red).fontWeight(FontWeight.Bold)
  }

  hover {
    Modifier.backgroundColor(Colors.Pink)
  }
}

//------------------ Post.kt

val PostTitleStyle = CssStyle.base { Modifier.fontSize(24.px) }
```

此外，Silk 提供了一个 `Deferred` 组件，它允许你声明在 DOM 完成渲染之前不会被渲染的代码，这意味着它将显示在其他所有内容之上。这是一种避免设置 CSS z-index 值的简洁方式（CSS 的另一个名声不佳的方面）。

最后，Silk 旨在提供适用于多数网站的默认样式组件。这意味着你应该能够在不接触 CSS 更复杂方面的情况下快速开发常见的 UI。

## 一个具体的例子

让我们通过一个在基本元素上添加 CSS 效果的例子来学习。

> [!TIP]
> CSS 属性的两个最佳学习资源是 `https://developer.mozilla.org` 和 `https://www.w3schools.com`。
> 当你进行网络搜索时要留意这些网站。

我们将创建之前讨论的带边框、浮动、摆动效果的元素。重新阅读一下，以下是我们需要弄清楚如何实现的概念：

* 创建边框
* 圆角处理
* 添加阴影
* 添加渐变背景
* 添加摆动动画

假设我们想在网站上创建一个引人注目的"欢迎"组件。我们可以从一个空的 box 开始，在里面放一些文字：

```kotlin
Box(Modifier.padding(topBottom = 5.px, leftRight = 30.px)) {
  Text("欢迎！！")
}
```

{{{ CssExample1 }}}

**创建边框**

接下来，搜索"CSS border"。顶部链接之一应该是：https://developer.mozilla.org/en-US/docs/Web/CSS/border

浏览文档并尝试交互示例。现在理解了边框属性后，让我们使用代码补全来发现 Kobweb 版本的 API：

```diff
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
+   .border(1.px, LineStyle.Solid, Colors.Black)
) {
  Text("欢迎！！")
}
```

{{{ CssExample2 }}}

**圆角处理**

搜索"CSS rounded corners"。事实证明，这里的 CSS 属性叫做"border radius"：https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius

```diff
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
+   .borderRadius(5.px)
) {
  Text("欢迎！！")
}
```

{{{ CssExample3 }}}

**添加阴影**

搜索"CSS shadow"。有几种类型的 CSS 阴影特性，但经过快速阅读，我们意识到我们想使用 box shadows：https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow

在尝试了模糊和扩展值后，我们得到了看起来不错的效果：

```diff
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
+   .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
) {
  Text("欢迎！！")
}
```

{{{ CssExample4 }}}

**添加渐变背景**

搜索"CSS gradient background"。这不像前面的例子那样是一个简单的 CSS 属性，所以我们得到的是一个更通用的文档页面，解释了这个特性：https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_images/Using_CSS_gradients

这个例子最终找到 Kotlin 类型安全等价物有点棘手，但如果你深入研究 CSS 文档，你会发现线性渐变是一种背景图像。

```diff
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
    .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
+   .backgroundImage(
+       linearGradient(
+          LinearGradient.Direction.ToRight, Colors.LightBlue, Colors.LightGreen
+       )
+    )
) {
  Text("欢迎！！")
}
```

{{{ CssExample5 }}}

**添加摆动动画**

最后，搜索"CSS animations"：https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_animations/Using_CSS_animations

你可以查看 ${DocsLink("animation section", "silk#animations")} 以了解 Kobweb 如何支持这个特性，这需要声明一个顶级的 `Keyframes` 块，然后在动画修饰符中引用它：

```diff
// 顶级属性
+val WobbleKeyframes = Keyframes {
+  from { Modifier.rotate((-5).deg) }
+  to { Modifier.rotate(5.deg) }
+}

// 在你的 @Page 组合函数中
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
    .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
    .backgroundImage(linearGradient(LinearGradient.Direction.ToRight, Colors.LightBlue, Colors.LightGreen))
+   .animation(
+     WobbleKeyframes.toAnimation(
+       duration = 1.s,
+       iterationCount = AnimationIterationCount.Infinite,
+       timingFunction = AnimationTimingFunction.EaseInOut,
+       direction = AnimationDirection.Alternate,
+     )
    )
) {
  Text("欢迎！！")
}
```

{{{ CssExample6 }}}

**完成了！**

上面的元素不会赢得任何风格奖项，但我希望这能展示出 CSS 在几行声明性代码中能给你带来多大的力量。感谢 CSS 的特性，加上 Kobweb 的实时重载体验，我们能够逐步尝试我们的想法。

## CSS 2 Kobweb

我们的主要项目贡献者之一创建了一个名为 [CSS 2 Kobweb](https://opletter.github.io/css2kobweb/) 的网站，旨在简化将 CSS 示例转换为等效的 Kobweb `CssStyle` 和/或 `Modifier` 声明的过程。

![CSS 2 Kobweb example](https://github.com/varabyte/media/raw/main/kobweb/images/css/css2kobweb.png)

> [!TIP]
> [CSS 2 Kobweb](https://opletter.github.io/css2kobweb/) 还支持指定类名选择器和关键帧。
> 例如，看看当你粘贴以下 CSS 代码时会发生什么：
> ```css
> .site-banner {
>   position: relative;
>   padding-left: 10px;
>   padding-top: 5%;
>   animation: slide-in 3s linear 1s infinite;
>   background-position: bottom 10px right;
>   background-image: linear-gradient(to bottom, #eeeeee, white 25px);
> }
> .site-banner:hover {
>   color: rgb(40, 40, 40);
> }
> @keyframes slide-in {
>   from {
>     transform: translateX(-2rem) scale(0.5);
>   }
>   to {
>     transform: translateX(0);
>     opacity: 1;
>   }
> }
> ```

网络上充满了有趣的 CSS 效果示例。几乎任何与 CSS 相关的搜索都会产生大量的 StackOverflow 答案、带有所见即所得编辑器的交互式游乐场和博客文章。许多这些介绍了一些非常新颖的 CSS 示例。这是学习更多关于网页开发的好方法！

然而，正如前一节所示，从 CSS 示例到等效的 Kobweb 代码有时可能会很麻烦。我们希望 *CSS 2 Kobweb* 能够帮助解决这个问题。

这个项目已经非常有用，但仍处于早期阶段。如果你发现 *CSS 2 Kobweb* 的某些情况不正确，请考虑在他们的仓库中 [提交问题](https://github.com/opLetter/css2kobweb/issues)。

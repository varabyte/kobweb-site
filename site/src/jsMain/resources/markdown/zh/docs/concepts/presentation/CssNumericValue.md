---
description: 关于 Kobweb 添加的 CSSNumericValue 类型别名的信息。
title: CSSNumericValue 类型别名
follows: CssLayers
---

Kobweb 引入了一些 CSS 单位值的类型别名，它们基于 `CSSNumericValue` 类，并扩展了 Compose HTML 定义的集合。它们是：

```kotlin
typealias CSSAngleNumericValue = CSSNumericValue<out CSSUnitAngle>
typealias CSSLengthOrPercentageNumericValue = CSSNumericValue<out CSSUnitLengthOrPercentage>
typealias CSSLengthNumericValue = CSSNumericValue<out CSSUnitLength>
typealias CSSPercentageNumericValue = CSSNumericValue<out CSSUnitPercentage>
typealias CSSFlexNumericValue = CSSNumericValue<out CSSUnitFlex>
typealias CSSTimeNumericValue = CSSNumericValue<out CSSUnitTime>
```

本节将解释为什么要添加这些类型别名，以及为什么你应该几乎总是优先使用它们。

### 背景

#### CSSSizeValue

当你在代码中编写像 `10.px`、`5.cssRem`、`45.deg` 或者 `30.s` 这样的 CSS 值时，通常你不需要过多考虑它们的类型。你只需创建它们并将它们传递给相应的 Kobweb / Compose HTML API。

让我们讨论一下当你这样做时实际发生了什么。Compose HTML 提供了一个 `CSSSizeValue` 类，它表示一个数值及其单位。

```kotlin
val lengthValue = 10.px // CSSSizeValue<CSSUnit.px> (值为 10，单位为 px)
val angleValue = 45.deg // CSSSizeValue<CSSUnit.deg> (值为 45，单位为 deg)
```

这是一个相当优雅的方法，但类型名称很冗长。在编写需要处理这些类型的代码时，这可能会造成困扰：

```kotlin
val lengths: List<CSSSizeValue<CSSUnit.px>>
fun drawArc(arc: CSSSizeValue<CSSUnit.deg>)
```

还要注意，上述情况过于严格，只支持单一的长度和角度类型。通常我们想要支持所有相关类型（例如，长度支持 `px`、`em`、`cssRem` 等；角度支持 `deg`、`rad`、`grad` 和 `turn`）。我们可以使用以下 `out` 语法来实现：

```kotlin
val lengths: List<CSSSizeValue<out CSSUnitLength>>
fun drawArc(arc: CSSSizeValue<out CSSUnitAngle>)
```

这太繁琐了！

因此，Compose HTML 团队为所有这些单位类型添加了类型别名，比如 `CSSLengthValue` 和 `CSSAngleValue`。现在，你可以这样编写上述代码：

```kotlin
val lengths: List<CSSLengthValue>
fun drawArc(arc: CSSAngleValue)
```

好多了！看起来不错。没有问题了，对吧？*对吧？！*

#### CSSNumericValue

从我的语气你可能已经猜到：是的，还有问题。

要解释这一点，我们首先需要讨论 `CSSNumericValue`。

在 CSS 中使用各种数学函数转换值是很常见的。也许你想要对两个不同单位求和（`10.px + 5.cssRem`）或调用其他数学函数（`clamp(1.cssRem, 3.vw)`）。这些操作返回的中间值不能像 `CSSSizeValue` 那样直接查询。

这由 Compose HTML 定义的 `CSSNumericValue` 类处理（实际上它是 `CSSSizeValue` 的基类）。

```kotlin
val lengthSum = 10.px + 2.cssRem // CSSNumericValue<CSSUnitLength>
val angleSum = 45.deg + 1.turn // CSSNumericValue<CSSAngleLength>
```

这些数值运算对浏览器来说当然很有用，浏览器可以将它们解析为绝对屏幕值，但对于我们用户空间来说，它们是不透明的计算。

然而，实际上这没问题！这些值的有限视图并不重要，因为我们在代码中很少需要查询它们。在几乎所有情况下，我们只是获取一些数值，可能通过做一些数学运算来调整它，然后将它传递给浏览器。

因为它是不透明的，`CSSNumericValue` 比 `CSSSizeValue` 更灵活，适用范围更广。如果你正在编写一个接受参数的函数，或声明一个与某些长度或时间相关的 `StyleVariable`，你几乎总是应该使用 `CSSNumericValue` 而不是 `CSSSizeValue`。

### 优先使用 Kobweb 的 `CSSNumericValue` 类型别名

如前所述，Compose HTML 团队创建了他们的单位相关类型别名，这些别名基于 `CSSSizeValue` 类。

这个决定使得编写代码在使用具体尺寸值测试时表现良好，但实际上比你预期的更具限制性。

Kobweb 确保其所有 API 都引用其 `CSSNumericValue` 类型别名：

```kotlin
// 旧版 Kobweb
fun Modifier.lineHeight(value: CSSLengthOrPercentageValue): Modifier = styleModifier {
  lineHeight(value)
}

// 现代 Kobweb
fun Modifier.lineHeight(value: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
  lineHeight(value)
}
```

如果你在代码中使用样式变量或编写自己的接受 CSS 单位作为参数的函数，你可能正在引用 Compose HTML 类型。你的代码仍然可以正常工作，但强烈建议迁移到 Kobweb 的新类型集，以使你的代码在接受参数方面更加灵活：

```kotlin
// 不推荐
val MyFontSize by StyleVariable<CSSLengthValue>
fun drawArc(arc: CSSAngleValue)

// 推荐
val MyFontSize by StyleVariable<CSSLengthNumericValue>
fun drawArc(arc: CSSAngleNumericValue)
```

> [!NOTE]
> 也许将来，Compose HTML 团队可能会考虑更新他们的类型别名，使用 `CSSNumericValue` 类型而不是 `CSSSizeValue` 类型。如果发生这种情况，我们可以回滚我们的更改并删除本节。但在那之前，理解为什么 Kobweb 引入自己的类型别名以及为什么建议使用它们而不是 Compose HTML 版本是很重要的。

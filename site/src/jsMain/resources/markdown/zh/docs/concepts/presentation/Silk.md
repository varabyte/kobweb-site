---
description: Silk(Kobweb的UI层)的全面概述。
follows: StylingHtmlElements
imports:
  - com.varabyte.kobweb.site.components.widgets.docs.silk.*
---

Silk 是 Kobweb 中包含的一个 UI 层,构建在 Compose HTML 之上。

虽然 Compose HTML 要求你理解底层的 HTML / CSS 概念,但 Silk 试图抽象出其中的一些内容，提供一个更接近你在 Android 或 Desktop 上开发 Compose 应用的 API。
减少"div、span、flexbox、attrs、styles、classes"这些概念,转而使用"Rows、Columns、Boxes 和 Modifiers"。

我们认为 Silk 是 Kobweb 体验中相当重要的一部分,但值得指出的是,它被设计为一个可选组件。你完全可以不使用 Silk 来使用 Kobweb。(你也可以在不使用 Kobweb 的情况下使用 Silk!)

你也可以轻松地在 Silk 和 Compose HTML 组件之间切换(因为 Silk 本身就是由它们组合而成的)。

## `@InitSilk` 方法

在进一步讨论之前,我们想快速提一下你可以使用 `@InitSilk` 注解一个方法，该方法会在你的网站启动时被调用。

这个方法必须接受一个 `InitSilkContext` 参数。上下文包含各种属性,允许调整 Silk 的默认值,这将在下面的章节中详细演示。

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
  // `ctx` 有一些属性允许你调整 Silk 的默认行为。
}
```

> [!TIP]
> 你的 `@InitSilk` 方法的名称并不重要,只要它们是公开的、接受一个 `InitSilkContext` 参数,并且不与其他同名方法冲突即可。出于可读性目的,建议你选择一个有意义的名称。
>
> 你可以定义任意数量的 `@InitSilk` 方法,所以可以根据需要将它们拆分成相关的、命名清晰的部分,而不是声明一个单一的、单体的、命名通用的 `fun initSilk(ctx)` 方法来做所有事情。
> 
> 只要你能接受它们按任意顺序被调用即可,因为不保证特定的调用顺序。

## CssStyle

使用 Silk, 你可以定义一个样式块。这让你可以以最终会被嵌入到 CSS 样式表中的方式声明修饰符
${DocsAside("Modifier", "styling-html-elements#modifier")} ${DocsAside("样式表优势", "styling-html-elements#stylesheet-advantages")}。

你可以使用 `CssStyle` 函数并将你的修饰符放入 `base` 块中来实现这一点:

```kotlin
val CustomStyle = CssStyle {
    base {
        Modifier.background(Colors.Red)
    }
}
```

我们稍后会讨论这个 `base` 块是什么,所以暂时不用担心它。

你可以使用 `toModifier()` 方法将任何这样的 `CssStyle` 转换为 `Modifier`(例如 `CustomStyle.toModifier()`)。此时,你可以将它传入任何接受 `Modifier` 参数的可组合函数:

```kotlin
// CssStyle.toModifier (成为样式表条目)
Box(CustomStyle.toModifier()) { /* ... */ }

// 直接创建修饰符 (成为内联样式)
Box(Modifier.backgroundColor(Colors.Red)) { /* ... */ }
```

> [!IMPORTANT]
> 当你声明一个 `CssStyle` 时,它必须是公开的。这是因为代码会被 Kobweb Gradle 插件生成在 `main.kt` 文件中,该代码需要能够访问你的样式以注册它。
>
> 一般来说,将样式视为全局的是个好主意,因为从技术上讲它们都存在于一个全局应用的样式表中,你必须确保样式名称在整个应用中是唯一的。
>
> 如果你添加一些样板代码来自己处理注册,你也可以使样式变成私有的:
>
> ```kotlin
> @Suppress("PRIVATE_COMPONENT_STYLE")
> private val ExampleCustomStyle = CssStyle { /* ... */ }
> // 或使用下划线前缀自动抑制警告
> private val _ExampleOtherCustomStyle = CssStyle { /* ... */ }
>
> @InitSilk
> fun registerPrivateStyle(ctx: InitSilkContext) {
>   // Kobweb 将无法检测属性名称,因此必须手动提供名称
>   ctx.theme.registerStyle("example-custom", ExampleCustomStyle)
>   ctx.theme.registerStyle("example-other-custom", _ExampleOtherCustomStyle)
> }
> ```
>
> 但是,我们鼓励你保持样式公开,让 Kobweb Gradle 插件为你处理所有事情。

### Additional selectors

那么,这个 `base` 块是怎么回事呢?

确实,单独使用时看起来有点啰嗦。但是,你可以定义其他有条件生效的选择器块。base 样式将始终首先应用,然后任何其他样式将基于特定选择器的规则应用。

> [!CAUTION]
> 定义附加选择器时顺序很重要,特别是当多个选择器同时适用时。

这里,我们创建一个默认为红色但鼠标悬停时为绿色的样式:

```kotlin
val CustomStyle = CssStyle {
    base {
        Modifier.color(Colors.Red)
    }

    hover {
        Modifier.color(Colors.Green)
    }
}
```

Kobweb 为你提供了很多标准选择器以方便使用,但对于那些精通 CSS 的人来说,你始终可以直接定义 CSS 规则以启用更复杂的组合或 Kobweb 尚未添加的选择器。

例如,这与上面的样式定义相同:

```kotlin
val CustomStyle = CssStyle {
    base {
        Modifier.color(Colors.Red)
    }

    cssRule(":hover") {
        Modifier.color(Colors.Green)
    }
}
```

### CssStyle name

Kobweb Gradle 插件会自动检测你的 `CssStyle` 属性并为你生成一个名称,该名称是从属性名称本身派生的但使用 [Kebab Case](https://www.freecodecamp.org/news/snake-case-vs-camel-case-vs-pascal-case-vs-kebab-case/#kebab-case)。

例如,如果你写 `val TitleTextStyle = CssStyle { ... }`,它的名称将是 "title-text"。

你通常不需要关心这个名称,但如果你使用浏览器开发工具检查 DOM,你会在那里看到它。

如果你需要手动设置名称,可以使用 `CssName` 注解覆盖默认名称:

```kotlin
@CssName("my-custom-name")
val CustomStyle = CssStyle {
    base {
        Modifier.background(Colors.Red)
    }
}
```

### `CssStyle.base`

大量的 `CssStyle` 块只包含 `base` 方法,所以 Kobweb 为这种常见情况提供了一个简便语法:

```kotlin
val CustomStyle = CssStyle.base {
    Modifier.background(Colors.Red)
}
```

如果你发现自己需要支持 ${DocsLink("additional selectors", "#additional-selectors")}, 你可以轻松地将 `base` 块分离出来。

### Breakpoints 

在响应式 HTML / CSS 设计世界中有一个叫做断点的功能,这与调试断点无关。相反,它们指定了你的网站在样式发生变化时的大小边界。这就是网站在移动设备、平板电脑和桌面设备上呈现不同内容的方式。

Kobweb 为你的项目提供了四个断点大小,加上不使用断点大小,总共给你五个可以在设计网站时使用的存储桶:

* 无断点 - 移动设备(及更大)  
* sm - 平板电脑(及更大)
* md - 桌面(及更大) 
* lg - 宽屏(及更大)
* xl - 超宽屏(及更大)

你可以通过在代码中添加一个 `@InitSilk` 方法并设置 `ctx.theme.breakpoints` 来更改你的网站的断点默认值:

```kotlin
@InitSilk
fun initializeBreakpoints(ctx: InitSilkContext) {
    ctx.theme.breakpoints = BreakpointSizes(
        sm = 30.cssRem,
        md = 48.cssRem,
        lg = 62.cssRem,
        xl = 80.cssRem,
    )
}
```

要在 `CssStyle` 中引用断点,只需调用它:

```kotlin
val CustomStyle = CssStyle {
    base {
        Modifier.fontSize(24.px)
    }

    Breakpoint.MD {
        Modifier.fontSize(32.px)
    }
}
```

> [!TIP]
> 在测试断点条件样式时,你应该知道浏览器开发工具允许你模拟窗口尺寸以查看你的网站在不同大小下的外观。例如,在 Chrome 上,你可以按照以下说明操作:
> https://developer.chrome.com/docs/devtools/device-mode 

你还可以使用 Kotlin 范围运算符指定样式应该只应用于特定范围的断点:

```kotlin
val CustomStyle = CssStyle {
    Breakpoint.MD { Modifier.fontSize(32.px) }

    // 以下三种方法效果相同, 
    // 确保它们的样式只在移动/平板模式下生效。

    // 选项 1: 独占上限
    (Breakpoint.ZERO ..< Breakpoint.MD) { Modifier.fontSize(24.px) }

    // 选项 2: 使用 `until` 代替 `..<`
    (Breakpoint.ZERO until Breakpoint.MD) { Modifier.fontSize(24.px)  }

    // 选项 3: 包含上限
    (Breakpoint.ZERO .. Breakpoint.SM) { Modifier.fontSize(24.px) }
}
```

如果你不喜欢需要用括号包裹断点范围表达式,也提供了 `between` 方法,它与 `..<` 范围运算符在其他方面是相同的:

```kotlin
val CustomStyle = CssStyle {
    // 在移动/平板模式下生效的样式
    between(Breakpoint.ZERO, Breakpoint.MD) { /* ... */ }
}
```

最后,如果你范围中的第一个断点是 `Breakpoint.ZERO`, 你可以使用 `until` 方法简化你的表达式:

```kotlin
val CustomStyle = CssStyle {
    // 在移动/平板模式下生效的样式 
    until(Breakpoint.MD) { /* ... */ }
}
```

事实上,你可以把 `until` 看作是声明普通断点的反向操作。换句话说,`until(Breakpoint.MD) { ... }` 意味着所有断点大小*直到*中等大小,而 `Breakpoint.MD { ... }` 意味着中等大小及以上。

### Color-mode aware

当你定义一个 `CssStyle` 时,可以使用一个叫做 `colorMode` 的属性:

```kotlin
val CustomStyle = CssStyle.base {
    Modifier.color(if (colorMode.isLight) Colors.Red else Colors.Pink)
}
```

Silk 为其所有部件定义了一堆浅色和深色颜色,如果你想在自己的部件中重用其中任何一个,你可以使用 `colorMode.toPalette()` 查询它们:

```kotlin
val CustomStyle = CssStyle.base {
    Modifier.color(colorMode.toPalette().link.default)
}
```

`SilkTheme` 包含非常简单(例如黑色和白色)的默认值,但你可以在 `@InitSilk` 方法中覆盖它们,也许可以改成更符合品牌的样式:

```kotlin
// 假设在某处定义了一堆颜色常量(例如 BRAND_LIGHT_COLOR)

@InitSilk
fun overrideSilkTheme(ctx: InitSilkContext) {
  ctx.theme.palettes.light.background = BRAND_LIGHT_BACKGROUND
  ctx.theme.palettes.light.color = BRAND_LIGHT_COLOR
  ctx.theme.palettes.dark.background = BRAND_DARK_BACKGROUND
  ctx.theme.palettes.dark.color = BRAND_DARK_COLOR
}
```

#### Initial color mode

默认情况下,Kobweb 会将你的网站的颜色模式初始化为 `ColorMode.LIGHT`。

但是,你可以通过在 `@InitSilk` 方法中设置 `initialColorMode` 属性来控制这一点:

```kotlin
@InitSilk
fun setInitialColorMode(ctx: InitSilkContext) {
    ctx.theme.initialColorMode = ColorMode.DARK
}
```

如果你想尊重用户的系统偏好,你可以将 `initialColorMode` 设置为 `ColorMode.systemPreference`:

```kotlin
@InitSilk
fun setInitialColorMode(ctx: InitSilkContext) {
    ctx.theme.initialColorMode = ColorMode.systemPreference
}
```

#### Persisting color-mode preference

如果你支持切换网站的颜色模式,建议你将用户的最后选择保存到本地存储中,然后在用户稍后重新访问你的网站时恢复它。

恢复将在你的 `@InitSilk` 块中进行,而保存颜色模式的代码应该发生在你的根 `@App` 可组合函数中 ${DocsAside("Application Root", "/kobweb/concepts/foundation/application-root")}:

```kotlin
@InitSilk
fun setInitialColorMode(ctx: InitSilkContext) {
    ctx.theme.initialColorMode =
      ColorMode.loadFromLocalStorage() ?: ColorMode.systemPreference
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
  SilkApp {
    val colorMode = ColorMode.current
    LaunchedEffect(colorMode) {
        colorMode.saveToLocalStorage()
    }

    /* ... */
  }
}
```

### Extending CSS styles

你可能偶尔会想要定义一个只应该与另一个样式一起应用/在其之后应用的样式。

实现这一点最简单的方法是使用 `extendedBy` 方法扩展基本 CSS 样式块:

```kotlin
val GeneralTextStyle = CssStyle {
    base { Modifier.fontSize(16.px).fontFamily("...") }
}
val EmphasizedTextStyle = GeneralTextStyle.extendedBy {
    base { Modifier.fontWeight(FontWeight.Bold) }
}
```

一旦扩展,你只需要在扩展的样式上调用 `toModifier` 就可以自动包含两个样式:

```kotlin
SpanText("WARNING", EmphasizedTextStyle.toModifier())
// 你不需要在这里提到 `GeneralTextStyle`。
// 它会被 `EmphasizedTextStyle` 自动引用。
```

### Component styles

到目前为止,我们已经讨论了定义各种 CSS 样式属性的基本 CSS 样式块。

然而,有一种方法可以定义*类型化*的 CSS 样式块。你可以从中生成类型化变体,基本上是调整或扩展它们的基本样式。你不能将从一个类型化 CSS 样式块生成的变体用于另一个不同类型的变体。

这种类型化 CSS 样式被称为*组件样式*,因为这种模式在定义小部件组件时很有效。事实上,这是 Silk 用于其每个小部件的标准模式。

要声明一个,你首先创建一个实现 `ComponentKind` 的标记接口,然后将其指定为你的 `CssStyle` 声明块的类型。按照惯例,它们的名称(减去后缀)应该匹配。

例如,如果 Silk 没有提供自己的按钮小部件,下面是你如何开始定义你自己的:

```kotlin
sealed interface ButtonKind : ComponentKind
val ButtonStyle = CssStyle<ButtonKind> { /* ... */ }
```

注意我们的接口声明的两点:

1. 它被标记为 `sealed`。技术上这不是必需的,但我们建议这样做,作为一种表达你的意图的方式,即没有其他人应该进一步子类化它。
2. 接口是空的。它只是一个标记接口,仅用于为变体强制执行类型。这在下一节中会详细讨论。

### Component variants

组件样式的强大之处在于它们可以使用 `addVariant` 方法生成*组件变体*:

```kotlin
val OutlinedButtonVariant: CssStyleVariant<ButtonKind> =
    ButtonStyle.addVariant { /* ... */ }
```

> [!NOTE]
> 变体的推荐命名约定是取其关联样式并使用其名称作为后缀加上单词"Variant",例如 `ButtonStyle` → `OutlinedButtonVariant` 和 `TextStyle` → `EmphasizedTextVariant`。

> [!IMPORTANT]
> 像任何 `CssStyle` 一样,你的 `CssStyleVariant` 必须是公开的。这是出于相同的原因:因为代码会被 Kobweb Gradle 插件生成在 `main.kt` 文件中,该代码需要能够访问你的变体以注册它。
>
> 如果你添加一些样板代码来自己处理注册,你也可以使变体变成私有的:
>
> ```kotlin
> @Suppress("PRIVATE_COMPONENT_VARIANT")
> private val ExampleCustomVariant = ButtonStyle.addVariant {
>   /* ... */
> }
> // 或者, `private val _ExampleCustomVariant`
>
> @InitSilk
> fun registerPrivateVariant(ctx: InitSilkContext) {
>   // 注册变体时,使用前导破折号将自动添加基本样式名称前缀。
>   // 这个例子将生成最终名称 "button-example"。
>   ctx.theme.registerVariant("-example", ExampleCustomVariant)
> }
> ```
>
> 但是,我们鼓励你保持变体公开,让 Kobweb Gradle 插件为你处理所有事情。

组件变体背后的想法是,它们让小部件作者有能力定义一个基本样式以及用户可能想要在其之上应用的一个或多个常见调整。(即使小部件作者没有为样式提供任何变体,任何用户也始终可以在他们自己的代码库中定义自己的变体。)

让我们重新访问按钮样式示例,把所有内容组合在一起。

```kotlin
sealed interface ButtonKind : ComponentKind

// 注意：创建一个名为 "button" 的 CSS 样式
val ButtonStyle = CssStyle<ButtonKind> { /* ... */ }

// 注意：创建一个名为 "button-outlined" 的 CSS 样式
val OutlinedButtonVariant = ButtonStyle.addVariant { /* ... */ }

// 注意：创建一个名为 "button-inverted" 的 CSS 样式
val InvertedButtonVariant = ButtonStyle.addVariant { /* ... */ }
```

当与组件样式一起使用时,`toModifier()` 方法可以选择接受一个变体参数。当传入一个变体时,两种样式都将被应用 -- 首先是基本样式,然后是变体样式。

例如,`ButtonStyle.toModifier(OutlinedButtonVariant)` 首先应用主按钮样式,然后是一些额外的轮廓样式。

你可以用 `@CssName` 注解标注样式变体,就像你可以对 `CssStyle` 做的那样。使用前导破折号会自动添加基本样式名称前缀。例如:

```kotlin
// 创建一个名为 "custom-name" 的 CSS 样式
@CssName("custom-name")
val OutlinedButtonVariant = ButtonStyle.addVariant { /* ... */ }

// 创建一个名为 "button-custom-name" 的 CSS 样式
@CssName("-custom-name")
val InvertedButtonVariant = ButtonStyle.addVariant { /* ... */ } 
```

#### `addVariantBase`

像 `CssStyle.base` 一样,不需要支持额外选择器的变体可以使用 `addVariantBase` 来稍微简化它们的声明:

```kotlin
// 之前
val HighlightedCustomVariant = CustomStyle.addVariant {
    base {
        Modifier.backgroundColor(Colors.Green)
    }
}

// 之后
val HighlightedCustomVariant = CustomStyle.addVariantBase {
    Modifier.backgroundColor(Colors.Green)
}
```

### Silk widget conventions

Silk 在定义其小部件时总是使用组件样式。完整的模式如下所示(如果你在自己的项目中定义自己的小部件,你可以模仿这个):

```kotlin
sealed interface CustomWidgetKind : ComponentKind

val CustomWidgetStyle = CssStyle<CustomWidgetKind> { /* ... */ }

@Composable
fun CustomWidget(
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<CustomWidgetKind>? = null,
    @Composable content: () -> Unit
) {
    val finalModifier = CustomWidgetStyle.toModifier(variant).then(modifier)
    /* ... */
}
```

换句话说:
* 我们定义一个可组合的小部件方法。
* 它接受一个 `Modifier` 作为第一个参数,该参数有一个默认值。
* 接下来是一个 `CssStyleVariant` 参数(类型化为你特定的 `ComponentKind` 实现)。
* 在你的小部件内部,我们按以下顺序应用修饰符:基本样式,然后是传入的变体,然后是传入的修饰符。
* 最后一个参数是一个 `@Composable` 内容 lambda 参数(除非这个小部件不支持自定义内容)。

调用者可以通过以下几种方式之一调用小部件:

```kotlin
// 方法 #1: 使用默认样式
CustomWidget { /* ... */ }

// 方法 #2: 用变体调整默认样式
CustomWidget(variant = TransparentWidgetVariant) { /* ... */ }

// 方法 #3: 用内联覆盖调整默认样式
CustomWidget(Modifier.backgroundColor(Colors.Blue)) { /* ... */ }

// 方法 #4: 同时用变体和内联覆盖调整默认样式。
// 内联覆盖优先。
CustomWidget(
  Modifier.backgroundColor(Colors.Blue),
  variant = TransparentWidgetVariant
) { /* ... */ }
```

## 动画

在CSS中，动画通过让你在样式表中定义关键帧来工作，然后通过名称在动画样式中引用这些关键帧。你可以在
[Mozilla文档网站](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations/Using_CSS_animations)了解更多信息。

例如，这里是一个滑动矩形的CSS动画示例
([来自本教程](https://www.w3schools.com/cssref/tryit.php?filename=trycss3_animation)):

```css
div {
  width: 100px;
  height: 100px;
  background: red;
  position: relative;
  animation: shift-right 5s infinite;
}

@keyframes shift-right {
  from {left: 0px;}
  to {left: 200px;}
}
```

Kobweb让你可以使用`Keyframes`代码块来定义关键帧：

```kotlin
val ShiftRightKeyframes = Keyframes {
    from { Modifier.left(0.px) }
    to { Modifier.left(200.px) }
}

// 后续使用
Div(
    Modifier
        .size(100.px)
        .backgroundColor(Colors.Red)
        .position(Position.Relative)
        .animation(ShiftRightKeyframes.toAnimation(
            duration = 5.s,
            iterationCount = AnimationIterationCount.Infinite
        ))
        .toAttrs()
)
```

> [!IMPORTANT]
> 当你声明一个`Keyframes`动画时，它必须是公开的。这是因为代码会在`main.kt`文件中生成，需要能够访问和注册它。

然后你可以使用`toAnimation`方法将关键帧集合转换为使用这些关键帧的动画，并将其传递给`Modifier.animation`修饰符。

关键帧块的名称会自动从属性名派生（这里，`ShiftRightKeyframes`被转换为`"shift-right"`）。

## `ElementRefScope`和原生HTML元素

有时，你可能需要访问Silk小部件背后的原生元素。所有Silk小部件都提供了一个可选的`ref`参数，该参数接受一个提供此信息的监听器。

```kotlin
Box(
    ref = /* ... */
) {
    /* ... */
}
```

所有`ref`回调将接收一个`org.w3c.dom.Element`子类。你可以查看
[Element](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-element/)类（及其通常更相关的
[HTMLElement](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-h-t-m-l-element/)继承者）以了解其可用的方法和属性。

原生HTML元素暴露了许多通过高级Compose HTML API无法获得的功能。

### `ref`

对于一个简单但常见的示例，我们可以使用原生元素来捕获焦点：

```kotlin
Box(
    ref = ref { element ->
        // 当此Box首次添加到DOM时触发
        element.focus()
    }
)
```

`ref { ... }`方法实际上可以接受一个或多个可选的任意值键。如果在后续重新组合中这些键中的任何一个发生变化，回调将重新运行：

```kotlin
val colorMode by ColorMode.currentState
Box(
    // 每次颜色模式变化时回调将被触发
    ref = ref(colorMode) { element -> /* ... */ }
)
```

### `disposableRef`

如果你需要知道元素何时进入*和*退出DOM，可以使用`disposableRef`。使用`disposableRef`时，代码块的最后一行必须是调用`onDispose`：

```kotlin
val activeElements: MutableSet<HTMLElement> = /* ... */

/* ... 后续 ... */

Box(
    ref = disposableRef { element ->
        activeElements.put(element)
        onDispose { activeElements.remove(element) }
    }
)
```

`disposableRef`方法也可以接受键，如果其中任何一个发生变化，监听器将重新运行。在这种情况下，`onDispose`回调也会被触发。

### `refScope`

最后，你可能希望有多个监听器，它们根据不同的键独立地重新创建。你可以使用`refScope`作为一种方式来组合两个或多个`ref`和/或`disposableRef`调用的任意组合：

```kotlin
var isFeature1Enabled: Boolean = /* ... */
var isFeature2Enabled: Boolean = /* ... */

Box(
    ref = refScope {
        ref(isFeature1Enabled) { element -> /* ... */ }
        disposableRef(isFeature2Enabled) { element -> /* ... */; onDispose { /* ... */ } }
    }
)
```

### Compose HTML refs

你可能偶尔会希望获取普通Compose HTML小部件的支持元素，例如`Div`或`Span`。然而，这些小部件没有`ref`回调，因为这是Silk提供的一个便利功能。

在这种情况下，你仍然有一些选择。

官方的方式是使用`attrs`块中的`ref`块来检索引用。这个版本的`ref`实际上更类似于Silk的`disposableRef`概念，而不是它的`ref`，因为它需要一个`onDispose`块：

```kotlin
Div(attrs = {
    ref { element -> /* ... */; onDispose { /* ... */ } }
})
```

> [!NOTE]
> 上面的代码片段改编自[官方教程](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/HTML/Using_Effects#ref-in-attrsbuilder)。

与Silk版本的`ref`不同，Compose HTML版本不接受键。如果你需要这种行为，并且Compose HTML小部件接受内容块（其中许多确实如此），你可以直接在其中调用Silk的`registerRefScope`方法：

```kotlin
Div {
  registerRefScope(
    disposableRef(featureEnabled) {
      element -> /* ... */
      onDispose { /* ... */ } 
    }
  )
}
```

## 样式变量

Kobweb支持CSS变量（也称为CSS自定义属性），这是一种功能，你可以在CSS样式中存储和检索变量声明的属性值。它通过一个名为`StyleVariable`的类来实现。

> [!NOTE]
> 你可以在[官方文档](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties)中找到CSS自定义属性的相关信息。

使用样式变量非常简单。首先声明一个没有值的变量（但将其锁定为一个类型），然后你可以在样式中使用`Modifier.setVariable(...)`进行初始化：

```kotlin
val dialogWidth by StyleVariable<CSSLengthNumericValue>()

// 这个样式将应用于一个位于根部的div，以便
// 这个变量值将被所有子元素使用。
val RootStyle = CssStyle.base {
  Modifier.setVariable(dialogWidth, 600.px)
}
```

一旦在父元素上设置了变量，它可以被该元素或其任何子元素查询。

> [!TIP]
> Compose HTML提供了一个`CSSLengthValue`，表示具体值，如`10.px`或`5.cssRem`。然而，Kobweb提供了一个`CSSLengthNumericValue`类型，表示更一般的概念，例如中间计算的结果。为所有相关单位提供了`CSS*NumericValue`类型，建议在声明样式变量时使用它们，因为它们更自然地支持在计算中使用。
>
> 我们稍后会更详细地讨论`CSSNumericValue` ${DocsAside("CSSNumericValue type-aliases", "css-numeric-value")}。

你可以稍后使用`value()`方法查询变量以提取其当前值：

```kotlin
val DialogStyle = CssStyle.base {
  Modifier.width(dialogWidth.value())
}
```

你还可以提供一个后备值，如果存在，该值将在变量之前未设置的情况下使用：

```kotlin
val DialogStyle = CssStyle.base {
  // 将是dialogWidth变量的值
  // 如果已设置；否则为500px。
  Modifier.width(dialogWidth.value(500.px))
}
```

你甚至可以在首次声明变量时提供一个默认的后备值！ （这是我们在Kobweb中支持的，尽管它不是CSS规范的一部分。）

以下代码示例显示了不同后备范围何时生效：

```kotlin
// 注意默认后备：100px
val dialogWidth by StyleVariable<CSSLengthNumericValue>(100.px)

val DialogStyle100 = CssStyle.base {
  // 使用默认后备。
  // 宽度=100px
  Modifier.width(dialogWidth.value())
}
val DialogStyle200 = CssStyle.base {
  // 使用特定后备。
  // 宽度=200px
  Modifier.width(dialogWidth.value(200.px))
}
val DialogStyle300 = CssStyle.base {
  // 忽略后备（400px），因为变量已显式设置。
  // 宽度=300px
  Modifier
      .setVariable(dialogWidth, 300.px)
      .width(dialogWidth.value(400.px))
}
```

> [!CAUTION]
> 在上面的示例中，在`DialogStyle300`样式中，我们设置了一个变量并在同一个修饰符中查询它，这纯粹是为了演示目的。在实践中，你不会出于任何我能想到的原因这样做——相反，变量会在其他地方单独设置，例如在内联样式或父容器上。

为了将这些概念结合在一起，下面我们声明一个背景颜色变量，创建一个设置它的根容器范围，一个使用它的子样式，最后是一个覆盖它的子样式变体：

```kotlin
// 默认调试颜色，所以如果我们看到它，
// 这表明我们忘记了稍后设置它。
val bgColor by StyleVariable<CSSColorValue>(Colors.Magenta)

val ContainerStyle = CssStyle.base {
    Modifier.setVariable(bgColor, Colors.Blue)
}
val SquareStyle = CssStyle.base {
    Modifier.size(100.px).backgroundColor(bgColor.value())
}
val RedSquareStyle = SquareStyle.extendedByBase {
    Modifier.setVariable(bgColor, Colors.Red)
}
```

以下代码将上述样式结合在一起（在某些情况下使用内联样式进一步覆盖背景颜色）：

```kotlin
@Composable
fun ColoredSquares() {
    Box(ContainerStyle.toModifier()) {
        Column {
            Row {
                // 1：来自ContainerStyle的颜色
                Box(SquareStyle.toModifier())
                // 2：来自RedSquareStyle的颜色
                Box(RedSquareStyle.toModifier())
            }
            Row {
                // 3：来自内联样式的颜色
                Box(SquareStyle.toModifier().setVariable(bgColor, Colors.Green))

                Span(Modifier.setVariable(bgColor, Colors.Yellow).toAttrs()) {
                    // 4：来自父级内联样式的颜色
                    Box(SquareStyle.toModifier())
                }
            }
        }
    }
}
```

上面的代码渲染了以下输出：

{{{ StyleVariablesDemo }}}

### 程序化设置值

如果你可以访问支持的HTML元素，你还可以直接从代码中设置CSS变量。

下面，我们使用`ref`回调获取全屏`Box`的支持元素，然后使用`Button`将其设置为彩虹颜色中的随机颜色：

```kotlin
// 我们在这里指定彩虹的初始颜色，因为变量
// 在用户点击按钮之前不会被设置。
val bgColor by StyleVariable<CSSColorValue>(Colors.Red)

val ScreenStyle = CssStyle.base {
    Modifier.fillMaxSize().backgroundColor(bgColor.value())
}

@Page
@Composable
fun RainbowBackground() {
    val roygbiv = remember { listOf(Colors.Red, /*...*/ Colors.Violet) }

    var screenElement: HTMLElement? by remember { mutableStateOf(null) }
    Box(ScreenStyle.toModifier(), ref = ref { screenElement = it }) {
        Button(onClick = {
            screenElement!!.setVariable(bgColor, roygbiv.random())
        }) {
            Text("Click me")
        }
    }
}
```

上面的代码生成了以下UI：

{{{ .components.widgets.docs.silk.RoygbivDemo }}}

### 优先使用纯Kotlin

大多数时候，你实际上可以不使用CSS变量！你的Kotlin代码通常是描述动态行为的更自然的地方，而不是HTML/CSS。

让我们重新审视上面的“彩色方块”示例。注意，如果我们不尝试使用变量，它会更容易阅读。

```kotlin
val SquareStyle = CssStyle.base {
    Modifier.size(100.px)
}

@Composable
fun ColoredSquares() {
    Column {
        Row {
            Box(SquareStyle.toModifier().backgroundColor(Colors.Blue))
            Box(SquareStyle.toModifier().backgroundColor(Colors.Red))
        }
        Row {
            Box(SquareStyle.toModifier().backgroundColor(Colors.Green))
            Box(SquareStyle.toModifier().backgroundColor(Colors.Yellow))
        }
    }
}
```

“彩虹背景”示例同样更容易通过使用Kotlin变量（即`var someValue by remember { mutableStateOf(...) }`）而不是CSS变量来阅读：

```kotlin
val ScreenStyle = CssStyle.base {
    Modifier.fillMaxSize()
}

@Page
@Composable
fun RainbowBackground() {
    val roygbiv = remember { listOf(Colors.Red, /*...*/ Colors.Violet) }

    var currColor by remember { mutableStateOf(Colors.Red) }
    Box(ScreenStyle.toModifier().backgroundColor(currColor)) {
        Button(onClick = { currColor = roygbiv.random() }) {
            Text("Click me")
        }
    }
}
```

即使你很少需要CSS变量，但在某些情况下，它们可能是你工具箱中的一个有用工具。上面的示例是用于展示CSS变量的相对独立环境中的人工场景。但这里有一些可能受益于CSS变量的情况：

* 你有一个允许用户从多个主题（例如主色和次色）中选择的站点。添加`themePrimary`和`themeSecondary`的CSS变量（应用于站点的根部）将非常简单，然后你可以在整个样式中引用它们。
* 你需要比简单的浅色/深色模式提供的更多的颜色控制。例如，Wordle有浅色/深色+正常/高对比度模式。
* 你想创建一个小部件，它根据添加的上下文动态更改其行为。例如，也许你的网站有一个黑暗区域和一个明亮区域，小部件应该在黑暗区域使用白色轮廓，在明亮区域使用黑色轮廓。这可以通过公开一个轮廓颜色变量来实现，每个站点区域负责设置它。
* 你想允许用户在伪类选择器（例如悬停、聚焦、活动）中调整某些小部件的值（例如颜色或边框大小），这比监听事件和设置内联样式要容易得多。
* 你有一个小部件，你最终为其创建了许多变体，但你意识到你可以用一个或两个CSS变量替换它们。

当不确定时，依靠Kotlin来处理动态行为，并偶尔考虑使用样式变量，如果你觉得这样做会清理代码。

### Calc

`StyleVariable`以一种微妙的方式工作，通常是可以的，直到它不行——这通常是当你尝试拦截和修改其值而不是仅仅传递它们时。

具体来说，像这样（将样式变量值乘以2）的代码会编译但在运行时无法工作：

```kotlin
val MyOpacityVar by StyleVariable<Number>()

// 后续...

// 边框不透明度应比小部件的其余部分更不透明
val borderOpacity = max(1.0, MyOpacityVar.value().toDouble() * 2)
```

要了解问题所在，让我们先退一步。以下代码：

```kotlin
val MyOpacityVar by StyleVariable<Number>()

// 后续...
Modifier.opacity(MyOpacityVar.value())
```

生成以下CSS：

```css
opacity: var(--my-opacity);
```

然而，`MyOpacityVar`在我们的代码中表现得像一个`Number`！如何生成类似于`var(--my-opacity)`的文本输出？

这是通过使用Kotlin/JS的`unsafeCast`实现的，你可以告诉编译器将一个值视为不同的类型。在这种情况下，`MyOpacityVar.value()`返回一个对象，Kotlin编译器将其视为`Number`，但实际上它是一个类实例，其`toString()`计算结果为`var(--my-opacity)`。

因此，`Modifier.opacity(MyOpacityVar.value())`看起来像是魔法！然而，如果你尝试进行一些算术运算，如`MyOpacityVar.value().toDouble() * 0.5`，编译器可能会满意，但在运行时，当JS引擎被要求对非数字进行数学运算时，事情会默默地中断。

在CSS中，使用变量进行数学运算是通过使用`calc`块来完成的，因此Kobweb提供了自己的`calc`方法来镜像这一点。当处理原始数值时，你必须将它们包装在`num`中，以便我们可以逃避上面导致运行时混淆的原始类型系统：

```kotlin
calc { num(MyOpacityVar.value()) * num(0.5) }
// 输出："calc(var(--my-opacity, 1) * 0.5)"
```

此时，你可以编写如下代码：

```kotlin
Modifier.opacity(calc { num(MyOpacityVar.value()) * num(0.5) })
```

记住将原始值包装在`num`中有点难，但如果你做错了，你会得到编译错误。

处理表示长度值的变量不需要calc块，因为Compose HTML支持对这些数值单位类型进行数学运算：

```kotlin
val MyFontSizeVar by StyleVariable<CSSLengthNumericValue>()

MyFontSizeVar.value() + 1.cssRem
// 输出："calc(var(--my-font-size) + 1rem)"
```

然而，如果你从一个原始数字开始并希望将其转换为大小，calc块仍然可能有用：

```kotlin
val MyFontSizeScaleFactorVar by StyleVariable<Number>()

calc { MyFontSizeScaleFactorVar.value() * 16.px }
// 输出："calc(var(--my-font-size-scale-factor) * 16px)"
```


## Font Awesome

Kobweb 提供了 `silk-icons-fa` 组件库，如果你希望在项目中使用所有免费的 Font Awesome (v6) 图标，可以使用它。

使用非常简单！请在 [Font Awesome 图标库](https://fontawesome.com/search?o=r&m=free) 中搜索、选择一个图标，然后使用对应的 Font Awesome 图标 Composable 调用它。

例如，如果我想添加 Kobweb 主题的 [蜘蛛图标](https://fontawesome.com/icons/spider?s=solid&f=classic)，可以这样在代码中调用：

```kotlin
FaSpider()
```

就是这么简单！

某些图标有实心和轮廓两种版本，例如 “Square”（轮廓版： [链接](https://fontawesome.com/icons/square?s=solid&f=classic) ；实心版：[链接](https://fontawesome.com/icons/square?s=regular&f=classic)）。此时默认展示轮廓版，但你可以传入样式枚举来控制：

```kotlin
FaSquare(style = IconStyle.FILLED)
```

所有 Font Awesome 的 Composable 都接受 modifier 参数，这样你就可以进一步调整样式：

```kotlin
FaSpider(Modifier.color(Colors.Red))
```

> [!NOTE]
> 使用我们的 `app` 模板创建项目时，Font Awesome 图标已经包含在内。

## Material Design Icons

Kobweb 提供了 `silk-icons-mdi` 组件库，供你在项目中使用所有免费的 Material Design 图标。

使用方法也十分简单！请在 [Material Icons 图标库](https://fonts.google.com/icons?icon.set=Material+Icons) 搜索、选择一个图标，然后调用对应的 Material Design Icon Composable。

例如，假如我搜索后找到并想使用他们的 [bug report 图标](https://fonts.google.com/icons?icon.set=Material+Icons&icon.query=bug+report)，在 Kobweb 代码中可以这样调用（将名称转换为小驼峰形式）：

```kotlin
MdiBugReport()
```

就是这么简单！

多数 Material Design 图标支持多种风格：轮廓、实心、圆角、锐角以及双色。请点击上方图标库链接确认你的图标支持哪些风格。你可以通过向方法的 `style` 参数传入相应的枚举来选择你想要的风格：

```kotlin
MdiLightMode(style = IconStyle.TWO_TONED)
```

所有 Material Design Icon 的 Composable 都接受 modifier 参数，这样你可以进一步调整样式：

```kotlin
MdiError(Modifier.color(Colors.Red))
```

## The Silk stylesheet

浏览器为许多 HTML 元素提供的默认样式通常无法满足大多数网站设计需求，你很可能需要调整其中至少部分样式。一个常见的例子是默认的网页字体，若不修改会让你的网站显得有些陈旧。

传统网站往往会通过创建 CSS 样式表并在 HTML 中引用来覆盖默认样式。但如果你在 Kobweb 应用中使用 Silk，你可以采用与 `CssStyle` 类似的方法为普通 HTML 元素定义样式。

为此，只需创建一个 `@InitSilk` 方法。其上下文参数中包含一个 `stylesheet` 属性，该属性代表你网站的 CSS 样式表，并提供一个风格化的 API 用于添加 CSS 规则。

下面是一个简单示例，将整个网站的字体调整为比浏览器默认更美观的样式，一个用于普通文本，一个用于代码：

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
  ctx.stylesheet.registerStyleBase("body") {
    Modifier.fontFamily("Ubuntu", "Roboto", "Arial", "Helvetica", "sans-serif")
      .fontSize(18.px)
      .lineHeight(1.5)
  }

  ctx.stylesheet.registerStyleBase("code") {
    Modifier.fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")
  }
}
```

> [!TIP]
> 方法 `registerStyleBase` 常用于以较少代码注册样式，但如果你需要添加对某个或某些伪类（例如 `hover`、`focus`、`active`） 的支持，也可以使用 `registerStyle`：
>
> ```kotlin
> ctx.stylesheet.registerStyle("code") {
>   base {
>     Modifier
>       .fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")
>       .userSelect(UserSelect.None) // 禁止复制代码！
>   }
>   hover {
>     Modifier.cursor(Cursor.NotAllowed)
>   }
> }
> ```

## Globally changing Silk widget styles

正如前面提到的，Silk 的所有组件都采用组件样式来构建其外观和风格。

通常，如果你只想在网站的某些位置调整某个样式，可以为该样式创建一个变体：

```kotlin
val TweakedButtonVariant = ButtonStyle.addVariantBase { /* ... */ }
  
// 稍后在使用时：
Button(variant = TweakedButtonVariant) { /* ... */ }
```

但如果你想全局改变某个组件在整个网站中的外观该怎么办？

你当然可以创建你自己的 Composable，将底层组件包裹在一个新样式中，比如自定义 `MyButton`，并定义专属的 `MyButtonStyle`，内部委托给原生的 `Button`。不过这样需要确保所有新加入的开发者都使用 `MyButton` 而非直接调用 `Button`。

Silk 提供了另一种方式，允许你直接修改它声明的样式或变体。

你可以通过 `@InitSilk` 方法来实现。上下文参数中提供了 `theme` 属性，该属性暴露了一系列方法，允许你重写所有样式和变体：

```kotlin
@InitSilk
fun replaceStylesAndOrVariants(ctx: InitSilkContext) {
  ctx.theme.replaceStyle(SomeStyle) { /* ... */ }
  ctx.theme.replaceVariant(SomeVariant) { /* ... */ }
  ctx.theme.modifyStyle(SomeStyle) { /* ... */ }
  ctx.theme.modifyVariant(SomeVariant) { /* ... */ }
}
```

> [!NOTE]
> 从技术上讲，你也可以将这些方法用于你自己网站声明的样式和变体，但这样做没有必要，因为你可以直接去源代码
> 修改这些值。不过，如果你正在使用提供自己样式和/或变体的第三方Kobweb库，这仍然可能很有用。

如果你想从头开始定义一整套新的CSS规则，请使用`replace`版本；如果你想在现有基础上添加额外的更改，
则使用`modify`版本。

> [!CAUTION]
> 对一些比较复杂的Silk样式使用`replace`可能会比较棘手，在尝试这样做之前，你可能需要先熟悉这些组件
> 实现的细节。此外，一旦你在你的网站中替换了某个样式，你将无法获得该样式在未来Silk版本中可能会有的
> 任何改进。

这是一个来自某个网站的真实示例，该网站希望其水平分隔线始终填充最大宽度。它使用了`modify`方法
（而不是`replace`方法），这通常是推荐的做法，因为这种方式在未来出现问题的可能性较小：

```kotlin
@InitSilk
fun makeHorizontalDividersFillWidth(ctx: InitSilkContext) {
  ctx.theme.modifyStyleBase(HorizontalDividerStyle) {
    Modifier.fillMaxWidth()
  }
}
```


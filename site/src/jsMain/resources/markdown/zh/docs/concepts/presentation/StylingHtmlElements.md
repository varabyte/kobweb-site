---
description: 在Kobweb中声明HTML元素CSS样式的各种方式。
title: HTML元素样式设置
follows: Index
---

## 内联样式与样式表

对于网页开发新手来说，了解设置HTML元素样式的两种方式很重要：内联样式和样式表。

内联样式直接定义在元素标签上。在原始HTML中，看起来是这样的：

```html
<div style="background-color:black">
```

同时，任何HTML页面都可以引用样式表列表，其中定义了大量样式，每个样式都绑定到一个选择器（用于选择应用这些样式的元素的规则）。

这里有一个简短的样式表示例：

```css
body {
  background-color: black;
  color: magenta
}
#title {
  color: yellow
}
```

你可以用这个样式表来设置以下文档的样式：

```html
<body>
  <!-- 标题从"body"获取背景色，从"#title"获取前景色 -->
  <div id="title">黄色文字黑色背景</div>
  品红色文字黑色背景
</body>
```

> [!NOTE]
> 当样式表和内联声明中存在冲突的样式时，内联样式优先。

我们稍后会介绍和讨论修饰符 ${DocsAside("Modifier", "#modifier")} 和 CSS 样式块 ${DocsAside("CssStyle", "silk#cssstyle")}。
但通常来说，当你直接将修饰符作为参数传递给可组合组件时，这些会变成内联样式，而如果你使用CSS样式块来定义样式，这些样式会被嵌入到网站的样式表中：

```kotlin
// 使用内联样式
Box(Modifier.color(Colors.Red)) { /* ... */ }

// 使用样式表
val BoxStyle = CssStyle {
    base { Modifier.color(Colors.Red) }
}
Box(BoxStyle.toModifier()) { /* ... */ }
```

### 样式表的优势

虽然没有绝对的规则，但通常在手写HTML/CSS时，样式表比内联样式更受欢迎，因为它更好地保持了关注点分离。也就是说，HTML应该表示网站的内容，而CSS控制外观和感觉。

但是！我们不是在手写HTML/CSS。我们在使用Compose HTML！在Kotlin中我们还需要关心这个吗？

事实证明，有时你必须使用样式表，因为没有它们，你就无法为高级行为定义样式（特别是[伪类](https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-classes)、
[伪元素](https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-elements)和
[媒体查询](https://developer.mozilla.org/en-US/docs/Web/CSS/Media_Queries/Using_media_queries)）。
例如，如果不使用样式表方法，你就无法覆盖已访问链接的颜色。所以值得意识到这些根本性的区别。

最后，当你倾向于使用样式表而不是内联样式时，使用浏览器工具调试页面也会更容易，因为这样可以使你的DOM树更容易阅读
（例如：`<div class="title">` vs. `<div style="color:yellow; background-color:black; font-size: 24px; ...">`）。

### 选择哪种方式？

作为初学者，甚至是在原型设计时的高级用户，可以尽可能多地使用内联修饰符，如果发现需要使用伪类、伪元素或媒体查询时，再转向CSS样式块。
在Kobweb中，将内联样式迁移到样式表是相当容易的。

在我自己的项目中，我倾向于对非常简单的布局声明使用内联样式（例如：`Row(Modifier.fillMaxWidth())`），
而对复杂和/或可重用的组件使用CSS样式块。

## 修饰符（Modifier）

Kobweb引入了`Modifier`类，以提供类似于Jetpack Compose中的体验。
（如果你不熟悉这个概念，可以在[这里阅读更多](https://developer.android.com/jetpack/compose/modifiers)）。

在Compose HTML世界中，你可以将`Modifier`视为CSS样式和*部分*属性的包装器。

> [!IMPORTANT]
> 如果你不熟悉HTML的[属性](https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes)和
> [样式](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/style)，
> 请参考官方文档。

所以这样的代码：

```kotlin
Modifier
    .backgroundColor(Colors.Red)
    .color(Colors.Green)
    .padding(200.px)
```

当传入Kobweb提供的组件（如`Box`）时：

```kotlin
Box(
  Modifier
      .backgroundColor(Colors.Red)
      .color(Colors.Green)
      .padding(200.px)
) {
    /* ... */
}
```

会生成等效的HTML：

```html
<div style="background:red;color:green;padding:200px">
   <!-- ... -->
</div>
```

### 链式调用

像在Jetpack Compose中一样，修饰符可以使用`then`方法链式调用：

```kotlin
val SIZE_MODIFIER = Modifier.size(50.px)
val SPACING_MODIFIER = Modifier.margin(10.px).padding(20.px)
val COLOR_MODIFIER = Modifier.backgroundColor(Colors.Magenta)

val SIZE_AND_SPACING_MODIFIER = SIZE_MODIFIER.then(SPACING_MODIFIER)
val SIZE_AND_COLOR_MODIFIER = SIZE_MODIFIER.then(COLOR_MODIFIER)
```

由于修饰符是不可变的，你可以放心地重用和组合它们。

### `toAttrs`

`Modifier`是Kobweb的概念，但Compose HTML对此一无所知。它使用一个叫做`AttrsScope`的概念来声明属性和样式。

因此，如果你有一个想要传递给Compose HTML元素的`Modifier`，你可以使用`toAttrs`方法将其转换为`AttrsScope`：

```kotlin
val SOME_STYLE_MODIFIER = Modifier.size(100.px).backgroundColor(Colors.Red)

Div(SOME_STYLE_MODIFIER.toAttrs()) {
    /* ... */
}
```

你还可以传入一个回调给`toAttrs`，让你修改最终的`AttrsScope`，类型会根据当前元素确定：

```kotlin
Div(SOME_STYLE_MODIFIER.toAttrs {
    // 这里是 AttrsScope<HTMLDivElement>
})
```

例如，当使用Compose HTML的`Input`可组合组件时，你可以用这个方法添加输入特定的属性：

```kotlin
private val LARGE_INPUT_MODIFIER = /* ... */

@Composable
fun LargeInput(name: String, placeholder: String) {
    var text by remember { mutableSetOf("") }
    Input(
        InputType.Text,
        attrs = LARGE_INPUT_MODIFIER
            .toAttrs {
                // 这里是 AttrsScope<HTMLInputElement>
                placeholder(placeholder)
                name(name)
                onChange { text = it.value }
            }
    )
}
```

### `attrsModifier`和`styleModifier`

Kobweb提供了大量的修饰符扩展（而且还在增长），比如上面的`background`、`color`和`padding`。
但当你遇到缺少的修饰符时，还有两个后备方案：`attrsModifier`和`styleModifier`。

在这一点上，你是在与Compose HTML交互，这是Kobweb的下一层。

使用它们看起来像这样：

```kotlin
// 修改元素标签的属性
// 例如：<tag a="..." b="..." c="..." /> 中的 "a"、"b" 和 "c"
Modifier.attrsModifier {
    id("example")
}

// 修改元素标签的样式
// 例如：<tag style="x:...;y:...;z:..." /> 中的 "x"、"y" 和 "z"
Modifier.styleModifier {
    width(100.percent)
    height(50.percent)
}
```

请注意，`style`本身就是一个属性，所以你甚至可以在`attrsModifier`中定义样式：

```kotlin
Modifier.attrsModifier {
    id("example")
    style {
        width(100.percent)
        height(50.percent)
    }
}
```
但在上述情况下，为了简单起见，建议使用`styleModifier`。

### `attr`和`property`

在偶尔（希望很少见！）的情况下，当Kobweb没有提供修饰符，而Compose HTML也没有提供你需要的属性或样式支持时，
你可以使用`attrsModifier`加`attr`方法或`styleModifier`加`property`方法。这个后备方案中的后备方案允许你提供任何自定义值。

上述情况可以重写为：

```kotlin
Modifier.attrsModifier {
    attr("id", "example")
}

Modifier.styleModifier {
    property("width", 100.percent)
    // 或者甚至是原始CSS：
    // property("width", "100%")
    property("height", 50.percent)
}
```

最后，请注意，根据CSS的设计，样式可以应用于任何元素，而属性通常与特定元素绑定。例如，`id`属性可以应用于任何元素，
但`href`只能应用于`a`标签。由于修饰符没有上下文知道它们被传递给哪个元素，Kobweb只提供
[全局属性](https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes)的属性修饰符
（例如`Modifier.id("example")`），而不提供其他属性。

如果你发现在自己的代码库中需要使用`styleModifier { property(key, value) }`，请考虑
${DocsLink("告诉我们", "/kobweb/community/submitting-issues-and-feedback")}，
这样我们就可以将缺失的样式修饰符添加到框架中。

至少，我们鼓励你定义自己的扩展方法来创建类型安全的样式修饰符：

```kotlin
fun Modifier.someMissingStyle() = styleModifier {
    property("some-missing-style", "value")
}
```

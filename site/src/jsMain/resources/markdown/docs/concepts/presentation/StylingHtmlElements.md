---
description: The various ways you can declare CSS styles on HTML elements in Kobweb.
title: Styling HTML Elements
---

## Inline vs. Stylesheet

For those new to web dev, it's worth understanding that there are two ways to set styles on your HTML elements: inline
and stylesheet.

Inline styles are defined on the element tag itself. In raw HTML, this might look like:

```html
<div style="background-color:black">
```

Meanwhile, any given HTML page can reference a list of stylesheets which can define a bunch of styles, where each style
is tied to a selector (a rule which _selects_ what elements those styles apply to).

A concrete example of a very short stylesheet can help here:

```css
body {
  background-color: black;
  color: magenta
}
#title {
  color: yellow
}
```

And you could use that stylesheet to style the following document:

```html
<body>
  <!-- Title gets background-color from "body" and foreground color from "#title" -->
  <div id="title">Yellow on black</div>
  Magenta on black
</body>
```

> [!NOTE]
> When conflicting styles are present both in a stylesheet and as an inline declaration, the inline styles take
> precedence.
 
We introduce and discuss both modifiers ${DocsAside("Modifier", "#modifier")} and CSS style
blocks ${DocsAside("CssStyle", "silk#cssstyle")} later. But in general, when you pass modifiers directly as an argument
into a composable widget, those will result in inline styles, whereas if you use a CSS style block to define your
styles, those will get embedded into the site's stylesheet:

```kotlin
// Uses inline styles
Box(Modifier.color(Colors.Red)) { /* ... */ }
```
```kotlin
// Uses a stylesheet
val BoxStyle = CssStyle {
    base { Modifier.color(Colors.Red) }
}
Box(BoxStyle.toModifier()) { /* ... */ }
```

### Stylesheet advantages

There's no hard and fast rule, but in general, when writing HTML / CSS by hand, stylesheets are often preferred over
inline styles as it better maintains a separation of concerns. That is, the HTML should represent the content of your
site, while the CSS controls the look and feel.

However! We're not writing HTML / CSS by hand. We're using Compose HTML! Should we even care about this in Kotlin?

As it turns out, there are times when you have to use stylesheets, because without them, you can't define styles for
advanced behaviors (particularly
[pseudo-classes](https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-classes), [pseudo-elements](https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-elements),
and [media queries](https://developer.mozilla.org/en-US/docs/Web/CSS/Media_Queries/Using_media_queries)). For example,
you can't override the color of visited links without using a stylesheet approach. So it's worth realizing there are
fundamental differences.

Finally, it can also be much easier debugging your page with browser tools when you lean on stylesheets over inline styles, as it
makes your DOM tree easier to read when your elements are simple (e.g. `<div class="title">`
vs. `<div style="color:yellow; background-color:black; font-size: 24px; ...">`).

### Which to use?

As a beginner, or even as an advanced user when prototyping, feel free to use inline modifiers as much as you can,
pivoting to CSS style blocks if you find yourself needing to use pseudo-classes, pseudo-elements, or media queries. It
is fairly easy to migrate inline styles over to stylesheets in Kobweb.

In my own projects, I tend to use inline styles for really simple layout declarations (e.g.
`Row(Modifier.fillMaxWidth())`) and CSS style blocks for complex and/or re-usable widgets.

## Modifier

Kobweb introduces the `Modifier` class, in order to provide an experience similar to what you find in Jetpack Compose.
(You can read [more about them here](https://developer.android.com/jetpack/compose/modifiers) if you're unfamiliar with
the concept).

In the world of Compose HTML, you can think of a `Modifier` as a wrapper on top of CSS styles and *some* attributes.

> [!IMPORTANT]
> Please refer to official documentation if you are not familiar with
> HTML [attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes)
> and/or [styles](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/style).

So this:

```kotlin
Modifier
    .backgroundColor(Colors.Red)
    .color(Colors.Green)
    .padding(200.px)
```

when passed into a widget provided by Kobweb, like `Box`:

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

would generate HTML equivalent to: 

```html
<div style="background:red;color:green;padding:200px">
   <!-- ... -->
</div>
```

### Chaining

Like in Jetpack Compose, modifiers can be chained together using the `then` method:

```kotlin
val SIZE_MODIFIER = Modifier.size(50.px)
val SPACING_MODIFIER = Modifier.margin(10.px).padding(20.px)
val COLOR_MODIFIER = Modifier.backgroundColor(Colors.Magenta)

val SIZE_AND_SPACING_MODIFIER = SIZE_MODIFIER.then(SPACING_MODIFIER)
val SIZE_AND_COLOR_MODIFIER = SIZE_MODIFIER.then(COLOR_MODIFIER)
```

Since modifiers are immutable, you can reuse and combine them fearlessly.

### `toAttrs`

`Modifier` is a Kobweb concept, but Compose HTML doesn't know anything about it. It works with a concept called
`AttrsScope` for declaring attributes and styles.

Therefore, if you have a `Modifier` that you want to pass into a Compose HTML element, you can convert it to an
`AttrsScope` using the `toAttrs` method:

```kotlin
val SOME_STYLE_MODIFIER = Modifier.size(100.px).backgroundColor(Colors.Red)

Div(SOME_STYLE_MODIFIER.toAttrs()) {
    /* ... */
}
```

You can additionally pass in a callback to `toAttrs` which lets you modify the final `AttrsScope`, typed to the current
element:

```kotlin
Div(SOME_STYLE_MODIFIER.toAttrs {
    // this is AttrsScope<HTMLDivElement>
})
```

For example, you could use this when working with the Compose HTML `Input` composable to add input-specific attributes:

```kotlin 9-14
private val LARGE_INPUT_MODIFIER = /* ... */

@Composable
fun LargeInput(name: String, placeholder: String) {
    var text by remember { mutableSetOf("") }
    Input(
        InputType.Text,
        attrs = LARGE_INPUT_MODIFIER
            .toAttrs {
                // this is AttrsScope<HTMLInputElement>
                placeholder(placeholder)
                name(name)
                onChange { text = it.value }
            }
    )
}
```

### `attrsModifier` and `styleModifier`

There are a bunch of modifier extensions (and they're growing) provided by Kobweb, like `background`, `color`, and
`padding` above. But there are also two escape hatches anytime you run into a modifier that's missing:
`attrsModifier` and `styleModifier`.

At this point, you are interacting with Compose HTML, one layer underneath Kobweb.

Using them looks like this:

```kotlin
// Modify attributes of an element tag
// e.g. the "a", "b", and "c" in <tag a="..." b="..." c="..." />
Modifier.attrsModifier {
    id("example")
}
```
```kotlin
// Modify styles of an element tag
// e.g. the "x", "y", and "z" in `<tag style="x:...;y:...;z:..." />
Modifier.styleModifier {
    width(100.percent)
    height(50.percent)
}
```

Note that `style` itself is an attribute, so you can even define styles in an `attrsModifier`:

```kotlin
Modifier.attrsModifier {
    id("example")
    style {
        width(100.percent)
        height(50.percent)
    }
}
```
but in the above case, you are encouraged to use a `styleModifier` instead for simplicity.

### `attr` and `property`

In the occasional (and hopefully rare!) case where Kobweb doesn't provide a modifier and Compose HTML doesn't provide
the attribute or style support you need, you can use `attrsModifier` plus the `attr` method or `styleModifier` plus the
`property` method. This escape hatch within an escape hatch allows you to provide any custom value you need.

The above cases can be rewritten as:

```kotlin
Modifier.attrsModifier {
    attr("id", "example")
}
```
```kotlin
Modifier.styleModifier {
    property("width", 100.percent)
    // Or even raw CSS:
    // property("width", "100%")
    property("height", 50.percent)
}
```

Finally, note that styles are, by the design of CSS, applicable to any element, while attributes are often tied to
specific ones. For example, the `id` attribute can be applied to any element, but `href` can only be applied to an `a`
tag. Since modifiers don't have context of which element they're being passed into, Kobweb only aims to provide
attribute modifiers for [global attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes)
(e.g. `Modifier.id("example")`) and no others.

If you ever end up needing to use `styleModifier { property(key, value) }` in your own codebase, consider
${DocsLink("letting us know", "/docs/community/submitting-issues-and-feedback")}
so that we can add the missing style modifier to the framework.

At the very least, you are encouraged to define your own extension method to create your own type-safe style modifier:

```kotlin
fun Modifier.someMissingStyle() = styleModifier {
    property("some-missing-style", "value")
}
```

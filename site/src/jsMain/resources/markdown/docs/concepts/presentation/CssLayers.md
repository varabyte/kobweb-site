---
title: CSS Layers
follows: LearningCss
---

[CSS Layers](https://developer.mozilla.org/en-US/docs/Web/CSS/@layer) are a very powerful but also relatively new CSS
feature, which allow wrapping CSS style rules inside named layers as a way to control their priorities. In short, CSS
layers are arbitrary names that you specify the order of. This can be an especially useful tool when dealing with CSS
style rules that are fighting with each other.

Compose HTML does *not* support CSS layers, but Silk does! Even if you never use layers directly in your own project,
Silk uses them, so users can still benefit from the feature.

## Default layers

By default, Silk defines seven layers (from lowest to highest ordering priority):

1. reset
2. kobweb-compose
3. base
4. component-styles
5. component-variants
6. restricted-styles
7. general-styles

The *reset* layer is useful for defining CSS rules that exist to compensate for browser defaults that are inconsistent
with each other or to override values that exist for legacy reasons that modern web design has moved away from.

The *kobweb-compose* layer contains styles we use to power the composable concepts we ported over from Jetpack Compose
(e.g. functions like `Box`, `Column`, and `Row`).

The *base* layer is actually not used by Silk (this may change someday), but it is provided as a useful place for users
to define global styles that should get easily overridden by any other CSS rule defined elsewhere in your project.

The next four styles are associated with the various flavors of `CssStyle` definitions:

```kotlin
interface SomeKind : ComponentKind
val SomeStyle = CssStyle<SomeKind> { /* ... */ } // "component-styles"
val SomeVariant = SomeStyle.addVariant { /* ... */ } // "component-variants"
class ButtonSize(/*...*/) : CssStyle.Base(/*...*/) // "restricted-styles"
val GeneralStyle = CssStyle { /* ... */ } // "general-styles"
```

We chose this order to ensure that CSS styles are layered in ways that match intuition; for example, a style's variant
will always layer on top of the base style itself; meanwhile, a user's declared `CssStyle` will always layer over a
component style defined by Silk.

## Registering layers

You can register your own custom layers inside an `@InitSilk` method, using the `cssLayers` property:

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.stylesheet.cssLayers.add("theme", "layout", "utilities")
}
```

When declaring new layers, you can anchor them relative to existing layers. This is useful, for example, if you want to
insert layers between Silk's *base* layer and its `CssStyle` layers:

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.stylesheet.cssLayers.add("third-party", after = SilkLayer.BASE)
}
```

## `@CssLayer` annotation

If you need to affect the layer for a `CssStyle` block, you can tag it with the `@CssLayer` annotation:

```kotlin
@CssLayer("important")
val ImportantStyle = CssStyle { /* ... */ }
```

> [!IMPORTANT]
> You should always explicitly register your layers. So, for the code above, you should also declare:
> ```kotlin
> @InitSilk
> fun initSilk(ctx: InitSilkContext) {
>   ctx.stylesheet.cssLayers.add("important")
> }
> ```
> elsewhere in your project.
>
> If you don't do this, the browser will append any unknown layer to the end of the CSS layer list (which is the highest
> priority spot). In many cases this will be fine, but being explicit both expresses your intention clearly and reduces
> the chance of your site breaking in subtle ways when a future developer adds a new layer.
>
> Silk will print out a warning to the console if it detects any unregistered layers.

## `layer` blocks

`@InitSilk` blocks let you register general CSS styles. You can wrap them insides layers using `layer` blocks:

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

Of course, you can associate styles with existing layers, such as the *base* layer we mentioned a few sections above:

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

## Importing third party styles into layers

Finally, if you are working with third party CSS stylesheets, it can be a very useful trick to wrap them in their own
layer.

For example, let's say you are fighting with a third party library whose styles are a bit too aggressive and are
interfering with your own styles.

First, inside your build script, import the stylesheet using Kobweb's `importCss` function, which internally uses the
CSS [`@import` at-rule](https://developer.mozilla.org/en-US/docs/Web/CSS/@import):

```kotlin
// BEFORE
kobweb.app.index.head.add {
  link(href = "/highlight.js/styles/dracula.css", rel = "stylesheet")
}

// AFTER
kobweb.app.index.head.add {
  style {
    importCss("/highlight.js/styles/dracula.css", layerName = "highlightjs")
  }
}
```

Then, register your new layer in an `@InitSilk` block.

```kotlin
@InitSilk
fun initBuildScriptLayers(ctx: InitSilkContext) {
    // Layer(s) referenced in build.gradle.kts
    ctx.stylesheet.cssLayers.add("highlightjs", after = SilkLayer.BASE)
}
```

You've just tamed some wild CSS styles. Congratulations!


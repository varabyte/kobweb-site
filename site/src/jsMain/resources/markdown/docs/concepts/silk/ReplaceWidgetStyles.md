---
follows: SilkStylesheet
---

As mentioned earlier, Silk widgets all use [component styles▲](#component-styles) to power their look and feel.

Normally, if you want to tweak a style in select locations within your site, you just create a variant from that style:

```kotlin
val TweakedButtonVariant = ButtonStyle.addVariantBase { /* ... */ }

// Later...
Button(variant = TweakedButtonVariant) { /* ... */ }
```

But what if you want to globally change the look and feel of a widget across your entire site?

You could of course create your own composable which wraps some underlying composable with its own new style, e.g.
`MyButton` which defines its own `MyButtonStyle` that internally delegates to `Button`. However, you'd have to be
careful to make sure all new developers who add code to your site know to use `MyButton` instead of `Button` directly.

Silk provides another way, allowing you to modify any of its declared styles and/or variants in place.

You can do this via an `@InitSilk` method, which takes an `InitSilkContext` parameter. This context provides the `theme`
property, which provides the following family of methods for rewriting styles and variants:

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
> Technically, you can use these methods with your own site's declared styles and variants as well, but there should be
> no reason to do so since you can just go to the source and change those values directly. However, this can still be
> useful if you're using a third-party Kobweb library that provides its own styles and/or variants.

Use the `replace` versions if you want to define a whole new set of CSS rules from scratch, or use the `modify` versions
to layer additional changes on top of what's already there.

> [!CAUTION]
> Using `replace` on some of the more complex Silk styles can be tricky, and you may want to familiarize yourself with
> the details of how those widgets are implemented before attempting to do so. Additionally, once you replace a style
> in your site, you will be opting-out of any future improvements to that style that may be made in future versions of
> Silk.

Here's an example of replacing `ImageStyle` on a site that wants to force all images to have rounded corners and
automatically scale down to fit their container:

```kotlin
@InitSilk
fun replaceSilkImageStyle(ctx: InitSilkContext) {
  ctx.theme.replaceStyleBase(ImageStyle) {
    Modifier
      .clip(Rect(cornerRadius = 8.px))
      .fillMaxWidth()
      .objectFit(ObjectFit.ScaleDown)
  }
}
```

and here's an example for a site that always wants its horizontal dividers to fill max width:

```kotlin
@InitSilk
fun makeHorizontalDividersFillWidth(ctx: InitSilkContext) {
  ctx.theme.modifyStyleBase(HorizontalDividerStyle) {
    Modifier.fillMaxWidth()
  }
}
```


---
description: A comprehensive overview of Silk, Kobweb's UI layer.
follows: StylingHtmlElements
imports:
  - com.varabyte.kobweb.site.components.widgets.docs.silk.*
---

Silk is a UI layer included with Kobweb and built upon Compose HTML.

While Compose HTML requires you to understand underlying HTML / CSS concepts, Silk attempts to abstract some of that
away, providing an API more akin to what you might experience developing a Compose app on Android or Desktop. Less
"div, span, flexbox, attrs, styles, classes" and more "Rows, Columns, Boxes, and Modifiers".

We consider Silk a pretty important part of the Kobweb experience, but it's worth pointing out that it's designed as an
optional component. You can absolutely use Kobweb without Silk. (You can also use Silk without Kobweb!)

You can also interleave Silk and Compose HTML components easily (as Silk is just composing them itself).

## `@InitSilk` methods

Before going further, we want to quickly mention you can annotate a method with `@InitSilk`, which will be called when
your site starts up.

This method must take a single `InitSilkContext` parameter. A context contains various properties that allow
adjusting Silk defaults, which will be demonstrated in more detail in sections below.

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
  // `ctx` has a handful of properties which allow you to
  // adjust Silk's default behavior.
}
```

> [!TIP]
> The names of your `@InitSilk` methods don't matter, as long as they're public, take a single `InitSilkContext`
> parameter, and don't collide with another method of the same name. You are encouraged to choose a name for readability
> purposes.
>
> You can define as many `@InitSilk` methods as you want, so feel free to break them up into relevant, clearly named
> pieces, instead of declaring a single, monolithic, generically named `fun initSilk(ctx)` method that does everything.
>
> Just be sure you're OK with them being called in any order, as no particular call order is guaranteed.

## CssStyle

With Silk, you can define a style block. This lets you declare modifiers
${DocsAside("Modifier", "styling-html-elements#modifier")} in a way that will ultimately get embedded into a CSS
stylesheet ${DocsAside("Stylesheet advantages", "styling-html-elements#stylesheet-advantages")}.

You do this using the `CssStyle` function and putting your modifier in the `base` block:

```kotlin
val CustomStyle = CssStyle {
    base {
        Modifier.background(Colors.Red)
    }
}
```

We'll discuss what this `base` block is in the next section, so don't worry about it for the moment.

You can convert any such `CssStyle` into a `Modifier` by using its `toModifier()` method (e.g.
`CustomStyle.toModifier()`). At this point, you can pass it into any composable which takes a `Modifier` parameter:

```kotlin
// CssStyle.toModifier (becomes a stylesheet entry)
Box(CustomStyle.toModifier()) { /* ... */ }
```
```kotlin
// Creating modifiers directly (becomes an inline style)
Box(Modifier.backgroundColor(Colors.Red)) { /* ... */ }
```

> [!IMPORTANT]
> When you declare a `CssStyle`, it must be public. This is because code gets generated inside a `main.kt` file by
> the Kobweb Gradle plugin, and that code needs to be able to access your style in order to register it.
>
> In general, it's a good idea to think of styles as global anyway, since technically they all live in a globally
> applied stylesheet, and you have to make sure that the style name is unique across your whole application.
>
> You can technically make a style private if you add a bit of boilerplate to handle the registration yourself:
>
> ```kotlin
> @Suppress("PRIVATE_COMPONENT_STYLE")
> private val ExampleCustomStyle = CssStyle { /* ... */ }
> // Or use a leading underscore to automatically suppress the warning
> private val _ExampleOtherCustomStyle = CssStyle { /* ... */ }
>
> @InitSilk
> fun registerPrivateStyle(ctx: InitSilkContext) {
>   // When registering directly, names must be provided manually
>   ctx.theme.registerStyle("example-custom", ExampleCustomStyle)
>   ctx.theme.registerStyle("example-other-custom", _ExampleOtherCustomStyle)
> }
> ```
>
> However, you are encouraged to keep your styles public and let the Kobweb Gradle plugin handle everything for you.

### Additional selectors

So, what's up with the `base` block?

True, it looks a bit verbose on its own. However, you can define additional selector blocks that take effect
conditionally. The base style will always apply first, but then any additional styles will be applied based on the
specific selector's rules.

> [!CAUTION]
> Order matters when defining additional selectors, especially if multiple selectors are applicable at the same time.

Here, we create a style which is red by default but green when the mouse hovers over it:

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

Kobweb provides a bunch of standard selectors for you for convenience, but for those who are CSS-savvy, you can always
define the CSS rule directly to enable more complex combinations or selectors that Kobweb hasn't added yet.

For example, this is identical to the above style definition:

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

The Kobweb Gradle plugin automatically detects your `CssStyle` properties and generates a name for it for you, derived
from the property name itself but
using [Kebab Case](https://www.freecodecamp.org/news/snake-case-vs-camel-case-vs-pascal-case-vs-kebab-case-whats-the-difference/#kebab-case).

For example, if you write `val TitleTextStyle = CssStyle { ... }`, its name will be "title-text".

You usually won't need to care about this name, but if you inspect the DOM using browser devtools, you'll see it there.

If you need to set a name manually, you can use the `CssName` annotation to override the default name:

```kotlin
@CssName("my-custom-name")
val CustomStyle = CssStyle {
    base {
        Modifier.background(Colors.Red)
    }
}
```

### `CssStyle.base`

A large number of `CssStyle` blocks only contain the `base` method, so Kobweb provides a convenience syntax for that
common case:

```kotlin
val CustomStyle = CssStyle.base {
    Modifier.background(Colors.Red)
}
```

You can easily break the `base` block out later if you find yourself needing to
support ${DocsLink("additional selectors", "#additional-selectors")}. 

### Breakpoints

There's a feature in the world of responsive HTML / CSS design called breakpoints, which confusingly have nothing to do
with debugging breakpoints. Rather, they specify size boundaries for your site when styles change. This is how sites
present content differently on mobile vs. tablet vs. desktop.

Kobweb provides four breakpoint sizes you can use for your project, which, including using no breakpoint size at all,
gives you five buckets you can work with when designing your site:

* no breakpoint - mobile (and larger)
* sm - tablets (and larger)
* md - desktops (and larger)
* lg - widescreen (and larger)
* xl - ultra widescreen (and larger)

You can change the default values of breakpoints for your site by adding
an `@InitSilk` method to your code and setting `ctx.theme.breakpoints`:

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

To reference a breakpoint in a `CssStyle`, just invoke it:

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
> When testing your breakpoint-conditional styles, you should be aware that browser dev tools let you simulate window
> dimensions to see how your site looks at different sizes. For example, on Chrome, you can follow these instructions:
> https://developer.chrome.com/docs/devtools/device-mode

You can also specify that a style should only apply to a specific range of breakpoints using Kotlin range operators:

```kotlin
val CustomStyle = CssStyle {
    Breakpoint.MD { Modifier.fontSize(32.px) }

    // The following three approaches have the same effect,
    // ensuring their style is only active in mobile / tablet modes.

    // Option 1: exclusive upper bound
    (Breakpoint.ZERO ..< Breakpoint.MD) { Modifier.fontSize(24.px) }

    // Option 2: using `until` for `..<`
    (Breakpoint.ZERO until Breakpoint.MD) { Modifier.fontSize(24.px)  }

    // Option 3: inclusive upper bound
    (Breakpoint.ZERO .. Breakpoint.SM) { Modifier.fontSize(24.px) }
}
```

If you aren't a fan of needing to wrap the breakpoint range expression with parentheses, the `between` method is
provided as well, which is otherwise identical to the `..<` range operator:

```kotlin
val CustomStyle = CssStyle {
    // Style active in mobile / tablet modes
    between(Breakpoint.ZERO, Breakpoint.MD) { /* ... */ }
}
```

Finally, if the first breakpoint in your range is `Breakpoint.ZERO`, you can shorten your expression by using the
`until` method instead:

```kotlin
val CustomStyle = CssStyle {
    // Style active in mobile / tablet modes
    until(Breakpoint.MD) { /* ... */ }
}
```

In fact, you can think of `until` as the inverse to declaring a normal breakpoint. In other words,
`until(Breakpoint.MD) { ... }` means all breakpoint sizes *up to* the medium size, while `Breakpoint.MD { ... }` means
medium size and above.

### Color-mode aware

When you define a `CssStyle`, a property called `colorMode` is available for you to use:

```kotlin
val CustomStyle = CssStyle.base {
    Modifier.color(if (colorMode.isLight) Colors.Red else Colors.Pink)
}
```

Silk defines a bunch of light and dark colors for all of its widgets, and if you'd like to re-use any of them in your
own widget, you can query them using `colorMode.toPalette()`:

```kotlin
val CustomStyle = CssStyle.base {
    Modifier.color(colorMode.toPalette().link.default)
}
```

`SilkTheme` contains very simple (e.g. black and white) defaults, but you can override them in
an `@InitSilk` method, perhaps to something that is more brand-aware:

```kotlin
// Assume a bunch of color constants (e.g. BRAND_LIGHT_COLOR) are defined somewhere

@InitSilk
fun overrideSilkTheme(ctx: InitSilkContext) {
  ctx.theme.palettes.light.background = BRAND_LIGHT_BACKGROUND
  ctx.theme.palettes.light.color = BRAND_LIGHT_COLOR
  ctx.theme.palettes.dark.background = BRAND_DARK_BACKGROUND
  ctx.theme.palettes.dark.color = BRAND_DARK_COLOR
}
```

#### Initial color mode

By default, Kobweb will initialize your site's color mode to `ColorMode.LIGHT`.

However, you can control this by setting the `initialColorMode` property in an `@InitSilk` method:

```kotlin
@InitSilk
fun setInitialColorMode(ctx: InitSilkContext) {
    ctx.theme.initialColorMode = ColorMode.DARK
}
```

If you'd like to respect the user's system preferences, you can set `initialColorMode` to `ColorMode.systemPreference`:

```kotlin
@InitSilk
fun setInitialColorMode(ctx: InitSilkContext) {
    ctx.theme.initialColorMode = ColorMode.systemPreference
}
```

#### Persisting color-mode preference

If you support toggling the site's color mode, you are encouraged to save the user's last chosen setting into local
storage and then restore it if the user revisits your site later.

The restoration will happen in your `@InitSilk` block, while the code to save the color mode should happen in your root
`@App` composable ${DocsAside("Application Root", "/docs/concepts/foundation/application-root")}:

```kotlin 3-4,11-14
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

You may find yourself occasionally wanting to define a style that should only be applied along with / after another
style.

The easiest way to accomplish this is by extending the base CSS style block, using the `extendedBy` method:

```kotlin 4
val GeneralTextStyle = CssStyle {
    base { Modifier.fontSize(16.px).fontFamily("...") }
}
val EmphasizedTextStyle = GeneralTextStyle.extendedBy {
    base { Modifier.fontWeight(FontWeight.Bold) }
}
```

Once extended, you only need to call `toModifier` on the extended style to include both styles automatically:

```kotlin
SpanText("WARNING", EmphasizedTextStyle.toModifier())
// You do NOT need to use `GeneralTextStyle` here.
// It is automatically referenced by `EmphasizedTextStyle`.
```

### Component styles

So far, we've discussed basic CSS style blocks that define a miscellaneous assortment of CSS style properties.

However, there is a way to define *typed* CSS style blocks. You can generate typed variants from them, which tweak or
extend their base styles, essentially. You cannot use a variant generated from one typed CSS style block with a
different one of another type.

This typed CSS style is called a *component style* because the pattern is effective when defining widget components. In
fact, it is the standard pattern that Silk uses for every single one of its widgets.

To declare one, you first create a marker interface that implements `ComponentKind` and then specify that as a type for
your `CssStyle` declaration block. By convention, their names (minus their suffixes) should match.

For example, if Silk didn't provide its own button widget, here's how you would start to define your own:

```kotlin
sealed interface ButtonKind : ComponentKind
val ButtonStyle = CssStyle<ButtonKind> { /* ... */ }
```

Notice two points about our interface declaration:

1. It is marked `sealed`. This is technically not necessary to do, but we recommend it as a way to express your
   intention that no one else is ever supposed to subclass it further.
2. The interface is empty. It is just a marker interface, useful only in enforcing typing for variants. This is
   discussed more in the next section.

### Component variants

The power of component styles is they can generate *component variants*, using the `addVariant` method:

```kotlin
val OutlinedButtonVariant: CssStyleVariant<ButtonKind> =
    ButtonStyle.addVariant { /* ... */ }
```

> [!NOTE]
> The recommended naming convention for variants is to take their associated style and use its name as a suffix plus the
> word "Variant", e.g. `ButtonStyle` → `OutlinedButtonVariant` and `TextStyle` → `EmphasizedTextVariant`.

> [!IMPORTANT]
> Like any `CssStyle`, your `CssStyleVariant` must be public. This is for the same reason: because code gets generated
> inside a `main.kt` file by the Kobweb Gradle plugin, and that code needs to be able to access your variant in order to
> register it.
>
> You can technically make a variant private if you add a bit of boilerplate to handle the registration yourself:
>
> ```kotlin
> @Suppress("PRIVATE_COMPONENT_VARIANT")
> private val ExampleCustomVariant = ButtonStyle.addVariant { /*...*/ }
> // Or use a leading underscore to automatically suppress the warning
> private val _ExampleCustomVariant = ButtonStyle.addVariant { /*...*/ }
>
> @InitSilk
> fun registerPrivateVariant(ctx: InitSilkContext) {
>   // When registering variants, using a leading dash will automatically prepend
>   // the base style name. Here, "button-example".
>   ctx.theme.registerVariant("-example", ExampleCustomVariant)
> }
> ```
>
> However, you are encouraged to keep your variants public and let the Kobweb Gradle plugin handle everything for you.

The idea behind component variants is that they give the widget author power to define a base style along with one or
more common tweaks that users might want to apply on top of it. (And even if a widget author doesn't provide any
variants for the style, any user can always define their own in their own codebase.)

Let's revisit the button style example, bringing everything together.

```kotlin
sealed interface ButtonKind : ComponentKind

// Note: Creates a CSS style called "button"
val ButtonStyle = CssStyle<ButtonKind> { /* ... */ }

// Note: Creates a CSS style called "button-outlined"
val OutlinedButtonVariant = ButtonStyle.addVariant { /* ... */ }

// Note: Creates a CSS style called "button-inverted"
val InvertedButtonVariant = ButtonStyle.addVariant { /* ... */ }
```

When used with a component style, the `toModifier()` method optionally takes a variant parameter. When a variant is
passed in, both styles will be applied -- the base style followed by the variant style.

For example, `ButtonStyle.toModifier(OutlinedButtonVariant)` applies the main button style first followed by some
additional outline styling.

You can annotate style variants with the `@CssName` annotation, exactly like you can with `CssStyle`. Using a leading
dash will automatically prepend the base style name. For example:

```kotlin
// Creates a CSS style called "custom-name"
@CssName("custom-name")
val OutlinedButtonVariant = ButtonStyle.addVariant { /* ... */ }

// Creates a CSS style called "button-custom-name"
@CssName("-custom-name")
val InvertedButtonVariant = ButtonStyle.addVariant { /* ... */ } 
```

#### `addVariantBase`

Like `CssStyle.base`, variants that don't need to support additional selectors can use `addVariantBase` instead to
slightly simplify their declaration:

```kotlin
// Before
val HighlightedCustomVariant = CustomStyle.addVariant {
    base {
        Modifier.backgroundColor(Colors.Green)
    }
}
```
```kotlin
// After
val HighlightedCustomVariant = CustomStyle.addVariantBase {
    Modifier.backgroundColor(Colors.Green)
}
```

### Silk widget conventions

Silk always uses component styles when defining its widgets. The full pattern looks like this (which you can imitate in
your own project if you define your own widgets):

```kotlin 1,3,8,11
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

In other words:
* we define a composable widget method.
* it takes a `Modifier` as the first parameter that takes a default value.
* this is followed by a `CssStyleVariant` parameter (typed to your specific `ComponentKind` implementation).
* inside your widget, we apply the modifiers in order of: base style, then passed in variant, then passed in modifier.
* the last parameter is a `@Composable` content lambda parameter (unless this widget doesn't support custom content).

A caller can call a widget one of several ways:

```kotlin
// Approach #1: Use default styling
CustomWidget { /* ... */ }
```
```kotlin
// Approach #2: Tweak default styling with a variant
CustomWidget(variant = TransparentWidgetVariant) { /* ... */ }
```
```kotlin
// Approach #3: Tweak default styling with inline overrides
CustomWidget(Modifier.backgroundColor(Colors.Blue)) { /* ... */ }
```
```kotlin
// Approach #4: Tweak default styling with both a variant and inline
// overrides. Inline overrides take precedence.
CustomWidget(
  Modifier.backgroundColor(Colors.Blue),
  variant = TransparentWidgetVariant
) { /* ... */ }
```

## Animations

In CSS, animations work by letting you define keyframes in a stylesheet which you then reference, by name, in an
animation style. You can read more about them
[on the Mozilla docs site](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations/Using_CSS_animations).

For example, here's the CSS for an animation of a sliding rectangle
([from this tutorial](https://www.w3schools.com/cssref/tryit.php?filename=trycss3_animation)):

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

Kobweb lets you define your keyframes in code by using a `Keyframes` block:

```kotlin 1,12
val ShiftRightKeyframes = Keyframes {
    from { Modifier.left(0.px) }
    to { Modifier.left(200.px) }
}

// Later
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
> When you declare a `Keyframes` animation, it must be public. This is because code gets generated inside a `main.kt`
> file that needs to be able to access and register it.

You can then use the `toAnimation` method to convert your collection of keyframes into an animation that uses them,
which you can pass into the `Modifier.animation` modifier.

The name of the keyframes block is automatically derived from the property name (here, `ShiftRightKeyframes` is
converted into `"shift-right"`).

## `ElementRefScope` and raw HTML elements

Occasionally, you may need access to the raw element backing the Silk widget you've just created. All Silk widgets
provide an optional `ref` parameter which takes a listener that provides this information.

```kotlin
Box(
    ref = /* ... */
) {
    /* ... */
}
```

All `ref` callbacks will receive an `org.w3c.dom.Element` subclass. You can check out
the [Element](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-element/) class (and its often more
relevant [HTMLElement](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-h-t-m-l-element/) inheritor) to see the
methods and properties that are available on it.

Raw HTML elements expose a lot of functionality not available through the higher-level Compose HTML APIs.

### `ref`

For a trivial but common example, we can use the raw element to capture focus:

```kotlin
Box(
    ref = ref { element ->
        // Triggered when this Box is first added into the DOM
        element.focus()
    }
)
```

The `ref { ... }` method can actually take one or more optional keys of any value. If any of these keys change on a
subsequent recomposition, the callback will be rerun:

```kotlin
val colorMode by ColorMode.currentState
Box(
    // Callback will get triggered each time the color mode changes
    ref = ref(colorMode) { element -> /* ... */ }
)
```

Finally, here is a pattern you can use to extract a raw backing element which has some role to play during composition:

```kotlin
var backingElement by remember { mutableStateOf<HTMLElement?>(null) }
SomeSilkWidget(ref = ref { backingElement = it }) {
    if (backingElement != null) {
        /* ... */
    }
}
```

> [!NOTE]
> Extracting a raw element as above will cause a composition to take two passes -- the first one where the content of
> your widget will be empty, and a second where it will be populated -- but in general this should be invisible to the
> user. 

### `disposableRef`

If you need to know both when the element enters *and* exits the DOM, you can use `disposableRef` instead. With
`disposableRef`, the very last line in your block must be a call to `onDispose`:

```kotlin
val activeElements: MutableSet<HTMLElement> = /* ... */

/* ... later ... */

Box(
    ref = disposableRef { element ->
        activeElements.put(element)
        onDispose { activeElements.remove(element) }
    }
)
```

The `disposableRef` method can also take keys that rerun the listener if any of them change. The `onDispose` callback
will also be triggered in that case.

### `refScope`

And, finally, you may want to have multiple listeners that are recreated independently of one another based on different
keys. You can use `refScope` as a way to combine two or more `ref` and/or `disposableRef` calls in any combination:

```kotlin
var isFeature1Enabled: Boolean = /* ... */
var isFeature2Enabled: Boolean = /* ... */

Box(
    ref = refScope {
        ref(isFeature1Enabled) { element -> /* ... */ }
        disposableRef(isFeature2Enabled) { element -> 
            /* ... */
            onDispose { /* ... */ } 
        }
    }
)
```

### Compose HTML refs

You may occasionally want the backing element of a normal Compose HTML widget, such as a `Div` or `Span`. However, these
widgets don't have a `ref` callback, as that's a convenience feature provided by Silk.

You still have a few options in this case.

The official way to retrieve a reference is by using a `ref` block inside an `attrs` block. This version of `ref` is
actually more similar to Silk's `disposableRef` concept than its `ref` one, as it requires an `onDispose` block:

```kotlin
Div(attrs = {
    ref { element -> /* ... */; onDispose { /* ... */ } }
})
```

> [!NOTE]
> The above snippet was adapted from [the official tutorials](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/HTML/Using_Effects#ref-in-attrsbuilder).

Unlike Silk's version of `ref`, Compose HTML's version does not accept keys. If you need this behavior and if the
Compose HTML widget accepts a content block (many of them do), you can call Silk's `registerRefScope` method directly
within it:

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

## Style variables

Kobweb supports CSS variables (also called CSS custom properties), which is a feature where you can store and retrieve
property values from variables declared within your CSS styles. It does this through a class called `StyleVariable`.

> [!NOTE]
> You can find [official documentation for CSS custom properties here](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties).

Using style variables is fairly simple. You first declare one without a value (but lock it down to a type) and later you
can initialize it within a style using `Modifier.setVariable(...)`:

```kotlin
val dialogWidth by StyleVariable<CSSLengthNumericValue>()

// This style will be applied to a div that lives at the root, so that
// this variable value will be made available to all children.
val RootStyle = CssStyle.base {
  Modifier.setVariable(dialogWidth, 600.px)
}
```

Once a variable is set on a parent element, it can be queried by that element or any of its children.

> [!TIP]
> Compose HTML provides a `CSSLengthValue`, which represents concrete values like `10.px` or `5.cssRem`. However, Kobweb
> provides a `CSSLengthNumericValue` type which represents the concept more generally, e.g. as the result of
> intermediate calculations. There are `CSS*NumericValue` types provided for all relevant units, and it is recommended
> to use them when declaring style variables as they more naturally support being used in calculations.
>
> We discuss `CSSNumericValue` in more detail later ${DocsAside("CSSNumericValue type-aliases", "css-numeric-value")}.

You can later query variables using the `value()` method to extract their current value:

```kotlin
val DialogStyle = CssStyle.base {
  Modifier.width(dialogWidth.value())
}
```

You can also provide a fallback value, which, if present, would be used in the case that a variable hadn't already been
set previously:

```kotlin
val DialogStyle = CssStyle.base {
  // Will be the value of the dialogWidth variable
  // if it was set; otherwise, 500px.
  Modifier.width(dialogWidth.value(500.px))
}
```

You can even provide a default fallback value when first declaring the variable! (This is something we support in
Kobweb even though it's not part of the CSS spec.)

The following code example shows when different fallback scopes take effect:

```kotlin
// Note the default fallback: 100px
val dialogWidth by StyleVariable<CSSLengthNumericValue>(100.px)

val DialogStyle100 = CssStyle.base {
  // Uses default fallback.
  // width = 100px
  Modifier.width(dialogWidth.value())
}
val DialogStyle200 = CssStyle.base {
  // Uses specific fallback.
  // width = 200px
  Modifier.width(dialogWidth.value(200.px))
}
val DialogStyle300 = CssStyle.base {
  // Fallback (400px) ignored because variable is set explicitly.
  // width = 300px
  Modifier
      .setVariable(dialogWidth, 300.px)
      .width(dialogWidth.value(400.px))
}
```

> [!CAUTION]
> In the above example in the `DialogStyle300` style, we set a variable and query it in the same modifier, which we did
> purely for demonstration purposes. In practice, you would never do this for any reason I can think of -- instead, the
> variable would have been set separately elsewhere, e.g. in an inline style or on a parent container.

To demonstrate these concepts all together, below we declare a background color variable, create a root container scope
which sets it, a child style that uses it, and, finally, a child style variant that overrides it:

```kotlin
// Default to a debug color, so if we see it,
// that indicates we forgot to set it later.
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

The following code brings the above styles together (and in some cases uses inline styles to override the background
color further):

```kotlin
@Composable
fun ColoredSquares() {
    Box(ContainerStyle.toModifier()) {
        Column {
            Row {
                // 1: Color from ContainerStyle
                Box(SquareStyle.toModifier())
                // 2: Color from RedSquareStyle
                Box(RedSquareStyle.toModifier())
            }
            Row {
                // 3: Color from inline style
                Box(SquareStyle.toModifier().setVariable(bgColor, Colors.Green))

                Span(Modifier.setVariable(bgColor, Colors.Yellow).toAttrs()) {
                    // 4: Color from parent's inline style
                    Box(SquareStyle.toModifier())
                }
            }
        }
    }
}
```

The above renders the following output:

{{{ StyleVariablesDemo }}}

### Set values programmatically

You can also set CSS variables directly from code if you have access to the backing HTML element.

Below, we use the `ref` callback to get the backing element for a fullscreen `Box` and then use a `Button` to set it to
a random color from the colors of the rainbow:

```kotlin
// We specify the initial color of the rainbow here, since the variable
// won't otherwise be set until the user clicks a button.
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

The above results in the following UI:

{{{ .components.widgets.docs.silk.RoygbivDemo }}}

### Prefer pure Kotlin

Most of the time, you can actually get away with not using CSS Variables! Your Kotlin code is often a more natural place
to describe dynamic behavior than HTML / CSS is.

Let's revisit the "colored squares" example from above. Note it's much easier to read if we don't try to use variables
at all.

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

And the "rainbow background" example is similarly easier to read by using Kotlin variables
(i.e. `var someValue by remember { mutableStateOf(...) }`) instead of CSS variables:

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

Even though you should rarely need CSS variables, there may be occasions where they can be a useful tool in your
toolbox. The above examples were artificial scenarios used as a way to show off CSS variables in relatively isolated
environments. But here are some situations that might benefit from CSS variables:

* You have a site which allows users to choose from a list of several themes (e.g. primary and secondary colors). It
  would be trivial enough to add CSS variables for `themePrimary` and `themeSecondary` (applied at the site's root)
  which you can then reference throughout your styles.
* You need more control for colors in your theming than can be provided for by the simple light / dark color mode. For
  example, Wordle has light / dark + normal / high contrast modes.
* You want to create a widget which dynamically changes its behavior based on the context it is added within. For
  example, maybe your site has a dark area and a light area, and the widget should use white outlines in the dark area
  and black outlines in the light. This can be accomplished by exposing an outline color variable, which each area of
  your site is responsible for setting.
* You want to allow the user to tweak values within a pseudo-class selector (e.g. hover, focus, active) for some
  widget (e.g. color or border size), which is much easier to do using variables than listening to events and setting
  inline styles.
* You have a widget that you ended up creating a bunch of variants for, but instead you realize you could replace them
  all with one or two CSS variables.

When in doubt, lean on Kotlin for handling dynamic behavior, and occasionally consider using style variables if you feel
doing so would clean up the code.

### Calc

`StyleVariable`s work in a subtle way that is usually fine until it isn't -- which is often when you try to intercept
and modify their values instead of just passing them around as is.

Specifically, code like this (multiplying a style variable value by 2) would compile but fail to work at runtime:

```kotlin
val MyOpacityVar by StyleVariable<Number>()

// later...

// Border opacity should be more opaque than the rest of the widget
val borderOpacity = max(1.0, MyOpacityVar.value().toDouble() * 2)
```

To see what the problem is, let's first take a step back. The following code:

```kotlin
val MyOpacityVar by StyleVariable<Number>()

// later...
Modifier.opacity(MyOpacityVar.value())
```

generates the following CSS:

```css
opacity: var(--my-opacity);
```

However, `MyOpacityVar` acts like a `Number` in our code! How does something that effectively has a type of `Number`
generate text output like `var(--my-opacity)`?

This is accomplished through the use of Kotlin/JS's `unsafeCast`, where you can tell the compiler to treat a value as a
different type than it actually is. In this case, `MyOpacityVar.value()` returns some object which the Kotlin compiler
*treats* like a `Number` for compilation purposes, but it is really some class instance whose `toString()` evaluates to
`var(--my-opacity)`.

Therefore, `Modifier.opacity(MyOpacityVar.value())` works seemingly like magic! However, if you try to do some
arithmetic, like `MyOpacityVar.value().toDouble() * 0.5`, the compiler might be happy, but things will break silently at
runtime, when the JS engine is asked to do math on something that's not really a number.

In CSS, doing math with variables is accomplished by using `calc` blocks, so Kobweb offers its own `calc` method to
mirror this. When dealing with raw numerical values, you must wrap them in `num` so we can escape the raw type system
which was causing runtime confusion above:

```kotlin
calc { num(MyOpacityVar.value()) * num(0.5) }
// Output: "calc(var(--my-opacity, 1) * 0.5)"
```

At this point, you can write code like this:

```kotlin
Modifier.opacity(calc { num(MyOpacityVar.value()) * num(0.5) })
```

It's a little hard to remember to wrap raw values in `num`, but you will get compile errors if you do it wrong.

Working with variables representing length values don't require calc blocks because Compose HTML supports mathematical
operations on such numeric unit types:

```kotlin
val MyFontSizeVar by StyleVariable<CSSLengthNumericValue>()

MyFontSizeVar.value() + 1.cssRem
// Output: "calc(var(--my-font-size) + 1rem)"
```

However, a calc block could still be useful if you were starting with a raw number that you wanted to convert to a size:

```kotlin
val MyFontSizeScaleFactorVar by StyleVariable<Number>()

calc { MyFontSizeScaleFactorVar.value() * 16.px }
// Output: calc(var(--my-font-size-scale-factor) * 16px)
```

## Font Awesome

Kobweb provides the `silk-icons-fa` artifact which you can use in your project if you want access to all the free
Font Awesome (v6) icons.

Using it is easy! Search the [Font Awesome gallery](https://fontawesome.com/search?o=r&m=free), choose an
icon, and then call it using the associated Font Awesome icon composable.

For example, if I wanted to add the Kobweb-themed
[spider icon](https://fontawesome.com/icons/spider?s=solid&f=classic), I could call this in my Kobweb code:

```kotlin
FaSpider()
```

That's it!

Some icons have a choice between solid and outline versions, such as "Square"
([outline](https://fontawesome.com/icons/square?s=solid&f=classic) and
[filled](https://fontawesome.com/icons/square?s=regular&f=classic)). In that case, the default choice will be an outline
mode, but you can pass in a style enum to control this:

```kotlin
FaSquare(style = IconStyle.FILLED)
```

All Font Awesome composables accept a modifier parameter, so you can tweak it further:

```kotlin
FaSpider(Modifier.color(Colors.Red))
```

> [!NOTE]
> When you create a project using our `app` template, Font Awesome icons are included.

## Material Design Icons

Kobweb provides the `silk-icons-mdi` artifact which you can use in your project if you want access to all the
free Material Design icons.

Using it is easy! Search the [Material Icons gallery](https://fonts.google.com/icons?icon.set=Material+Icons), choose an
icon, and then call it using the associated Material Design Icon composable.

For example, let's say after a search I found and wanted to use their
[bug report icon](https://fonts.google.com/icons?icon.set=Material+Icons&icon.query=bug+report), I could call this in my
Kobweb code by converting the name to camel case:

```kotlin
MdiBugReport()
```

That's it!

Most material design icons support multiple styles: outlined, filled, rounded, sharp, and two-tone. Check the gallery
search link above to verify what styles are supported by your icon. You can identify the one you want to use by passing
it into the method's `style` parameter:

```kotlin
MdiLightMode(style = IconStyle.TWO_TONED)
```

All Material Design Icon composables accept a modifier parameter, so you can tweak it further:

```kotlin
MdiError(Modifier.color(Colors.Red))
```

## The Silk stylesheet

The default styles provided by browsers for many HTML elements rarely fit most site designs, and it's likely you'll want
to tweak at least some of them. A very common example of this is the default web font, which if left as is will make
your site look a bit archaic.

Most traditional sites overwrite styles by creating a CSS stylesheet and then linking to it in their HTML. However, if
you are using Silk in your Kobweb application, you can use an approach very similar to `CssStyle` but for general HTML
elements.

To do this, create an `@InitSilk` method. The context parameter includes a `stylesheet` property that represents the CSS
stylesheet for your site, providing a Silk-idiomatic API for adding CSS rules to it.

Below is a simple example that sets the whole site to more aesthetically pleasing fonts than the browser defaults, one
for regular text and one for code:

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
> The `registerStyleBase` method is commonly used for registering styles with minimal code, but you can also use
> `registerStyle`, especially if you want to add some support for one or more pseudo-classes (
> e.g. `hover`, `focus`, `active`):
>
> ```kotlin
> ctx.stylesheet.registerStyle("code") {
>   base {
>     Modifier
>       .fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")
>       .userSelect(UserSelect.None) // No copying code allowed!
>   }
>   hover {
>     Modifier.cursor(Cursor.NotAllowed)
>   }
> }
> ```

## Globally changing Silk widget styles

As mentioned earlier, Silk widgets all use component styling ${DocsAside("Component styles", "silk#component-styles")}
to power their look and feel.

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

You can do this via an `@InitSilk` method. The context parameter provides the `theme` property, which exposes the
following family of methods allowing you to rewrite all styles and variants:

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

Here's a real example taken from a site that always wants its horizontal dividers to fill max width. It uses the
`modify` method (and not the `replace` method), which is generally recommended as it is less likely to break in the
future:

```kotlin
@InitSilk
fun makeHorizontalDividersFillWidth(ctx: InitSilkContext) {
  ctx.theme.modifyStyleBase(HorizontalDividerStyle) {
    Modifier.fillMaxWidth()
  }
}
```

---
follows: Index
---

Silk is a UI layer included with Kobweb and built upon Compose HTML.

While Compose HTML requires you to understand underlying HTML / CSS concepts, Silk attempts to abstract some of that
away, providing an API more akin to what you might experience developing a Compose app on Android or Desktop. Less
"div, span, flexbox, attrs, styles, classes" and more "Rows, Columns, Boxes, and Modifiers".

We consider Silk a pretty important part of the Kobweb experience, but it's worth pointing out that it's designed as an
optional component. You can absolutely use Kobweb without Silk. (You can also use Silk without Kobweb!).

You can also interleave Silk and Compose HTML components easily (as Silk is just composing them itself).

### `@InitSilk` methods

Before going further, we want to quickly mention you can annotate a method with `@InitSilk`, which will be called when
your site starts up.

This method must take a single `InitSilkContext` parameter. A context contains various properties that allow
adjusting Silk defaults, which will be demonstrated in more detail in sections below.

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
  // `ctx` has a handful of properties which allow you to adjust Silk's default behavior.
}
```

> [!TIP]
> The names of your `@InitSilk` methods don't matter, as long as they're public, take a single `InitSilkContext`
> parameter, and don't collide with another method of the same name. You are encouraged to choose a name for readability
> purposes.
>
> You can define as many `@InitSilk` methods as you want, so feel free to break them up into relevant, clearly named
> pieces, instead of declaring a single, monolithic, generically named `fun initSilk(ctx)` method that does everything.

### CssStyle

With Silk, you can define a style like so, using the `CssStyle` function and the `base` block:

```kotlin
val CustomStyle = CssStyle {
    base {
        Modifier.background(Colors.Red)
    }
}
```

and convert it to a modifier by using `CustomStyle.toModifier()`. At this point, you can pass it into any composable
which takes a `Modifier` parameter:

```kotlin
// Approach #1 (uses inline styles)
Box(Modifier.backgroundColor(Colors.Red)) { /* ... */ }

// Approach #2 (uses stylesheets)
Box(CustomStyle.toModifier()) { /* ... */ }
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
>   // Kobweb will not be able to detect the property name, so a name must be provided manually
>   ctx.theme.registerStyle("example-custom", ExampleCustomStyle)
>   ctx.theme.registerStyle("example-other-custom", _ExampleOtherCustomStyle)
> }
> ```
>
> However, you are encouraged to keep your styles public and let the Kobweb Gradle plugin handle everything for you.

#### `CssStyle.base`

You can simplify the syntax of basic style blocks a bit further with the `CssStyle.base` declaration:

```kotlin
val CustomStyle = CssStyle.base {
    Modifier.background(Colors.Red)
}
```

Just be aware you may have to break this out again if you find yourself needing to
support [additional selectors▼](#additional-selectors).

#### CssStyle name

The Kobweb Gradle plugin automatically detects your `CssStyle` properties and generates a name for it for you, derived
from the property name itself but
using [Kebab Case](https://www.freecodecamp.org/news/snake-case-vs-camel-case-vs-pascal-case-vs-kebab-case-whats-the-difference/#kebab-case).

For example, if you write `val TitleTextStyle = CssStyle { ... }`, its name will be "title-text".

You usually won't need to care about this name, but there are niche cases where it can be useful to understand that is
what's going on.

If you need to set a name manually, you can use the `CssName` annotation to override the default name:

```kotlin
@CssName("my-custom-name")
val CustomStyle = CssStyle {
    base {
        Modifier.background(Colors.Red)
    }
}
```

#### Additional selectors

So, what's up with the `base` block?

True, it looks a bit verbose on its own. However, you can define additional selector blocks that take effect
conditionally. The base style will always apply first, but then any additional styles will be applied based on the
specific selector's rules.

> [!CAUTION]
> Order matters when defining additional selectors, particularly if more than one of them modify the same CSS property
> at the same time.

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

#### Breakpoints

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
    // The following three declarations are the same, and ensure their style is only active in mobile / tablet modes

    // Option 1: exclusive upper bound
    (Breakpoint.ZERO ..< Breakpoint.MD) { Modifier.fontSize(24.px) }

    // Option 2: using `until` for `..<`
    (Breakpoint.ZERO until Breakpoint.MD) { Modifier.fontSize(24.px)  }

    // Option 3: inclusive upper bound
    (Breakpoint.ZERO .. Breakpoint.SM) { Modifier.fontSize(24.px) }

    Breakpoint.MD { Modifier.fontSize(32.px) }
}
```

If you aren't a fan of needing to wrap the expression with parentheses, the `between` method is provided as well, which
is otherwise identical to the `..<` range operator:

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

#### Color-mode aware

When you define a `CssStyle`, a field called `colorMode` is available for you to use:

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

##### Initial color mode

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

##### Persisting color-mode preference

If you support toggling the site's color mode, you are encouraged to save the user's last chosen setting into local
storage and then restore it if the user revisits your site later.

The restoration will happen in your `@InitSilk` block, while the code to save the color mode should happen in your root
`@App` composable:

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


#### Extending CSS styles

You may find yourself occasionally wanting to define a style that should only be applied along with / after another
style.

The easiest way to accomplish this is by extending the base CSS style block, using the `extendedBy` method:

```kotlin
val GeneralTextStyle = CssStyle {
    base { Modifier.fontSize(16.px).fontFamily("...") }
}
val EmphasizedTextStyle = GeneralTextStyle.extendedBy {
    base { Modifier.fontWeight(FontWeight.Bold) }
}

// Or, using the `base` methods:
// val GeneralTextStyle = CssStyle.base {
//   Modifier.fontSize(16.px).fontFamily("...")
// }
// val EmphasizedTextStyle = GeneralTextStyle.extendedByBase {
//   Modifier.fontWeight(FontWeight.Bold)
// }
```

Once extended, you only need to call `toModifier` on the extended style to include both styles automatically:

```kotlin
SpanText("WARNING", EmphasizedTextStyle.toModifier())
// You do NOT need to reference the base style, i.e.
// GeneralTextStyle.toModifier().then(EmphasizedTextStyle.toModifier())
```

#### Component styles

So far, we've discussed CSS style blocks as defining a general assortment of CSS style properties. However, there is a
way to define typed CSS style blocks, which let you generate typed variants associated with, and only compatible
with, a specific base style.

The base style in this case is called a *component style* because the pattern is effective when defining widget
components. In fact, it is the standard pattern that Silk uses for every single one of its widgets.

We'll discuss this full pattern around building widgets using component styles later, but to start we'll demonstrate how
to declare one. You create a marker interface that implements `ComponentKind` and then pass that into your `CssStyle`
declaration block.

For example, if you were creating your own `Button` widget:

```kotlin
sealed interface ButtonKind : ComponentKind
val ButtonStyle = CssStyle<ButtonKind> { /* ... */ }
```

Notice two points about our interface declaration:

1. It is marked `sealed`. This is technically not necessary to do, but we recommend it as a way to express your
   intention that no one else is going to subclass it further.
2. The interface is empty. It is just a marker interface, useful only in enforcing typing for variants. This is
   discussed more in the next section.

Like normal `CssStyle` declarations, the associated name is derived from its property name. You can use a `@CssName`
annotation to override this behavior.

#### Component variants

The power of component styles is they can generate *component variants*, using the `addVariant` method:

```kotlin
val OutlinedButtonVariant: CssStyleVariant<ButtonKind> = ButtonStyle.addVariant { /* ... */ }
```

> [!NOTE]
> The recommended naming convention for variants is to take their associated style and use its name as a suffix plus the
> word "Variant", e.g. "ButtonStyle" -> "OutlinedButtonVariant" and "TextStyle" -> "EmphasizedTextVariant".

> [!IMPORTANT]
> Like a `CssStyle`, your `CssStyleVariant` must be public. This is for the same reason: because code gets generated
> inside a `main.kt` file by the Kobweb Gradle plugin, and that code needs to be able to access your variant in order to
> register it.
>
> You can technically make a variant private if you add a bit of boilerplate to handle the registration yourself:
>
> ```kotlin
> @Suppress("PRIVATE_COMPONENT_VARIANT")
> private val ExampleCustomVariant = ButtonStyle.addVariant {
>   /* ... */
> }
> // Or, `private val _ExampleCustomVariant`
>
> @InitSilk
> fun registerPrivateVariant(ctx: InitSilkContext) {
>   // When registering variants, using a leading dash will automatically prepend the bast style name.
>   // This example here will generate the final name "button-example".
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

val ButtonStyle = CssStyle<ButtonKind> { /* ... */ }

// Note: Creates a CSS style called "button-outlined"
val OutlinedButtonVariant = ButtonStyle.addVariant { /* ... */ }

// Note: Creates a CSS style called "button-inverted"
val InvertedButtonVariant = ButtonStyle.addVariant { /* ... */ }
```

When used with a component style, the `toModifier()` method optionally takes a variant parameter. When a variant is
passed in, both styles will be applied -- the base style followed by the variant style.

For example, `ButtonStyle.toModifier(OutlinedButtonVariant)` applies the main button style first layered on top with
some additional outline styling.

You can annotate style variants with the `@CssName` annotation, exactly like you can with `CssStyle`. Using a leading
dash will automatically prepend the base style name. For example:

```kotlin
@CssName("custom-name")
val OutlinedButtonVariant = ButtonStyle.addVariant { /* ... */ } // Creates a CSS style called "custom-name"

@CssName("-custom-name")
val InvertedButtonVariant = ButtonStyle.addVariant { /* ... */ } // Creates a CSS style called "button-custom-name"
```

##### `addVariantBase`

Like `CssStyle.base`, variants that don't need to support additional selectors can use `addVariantBase` instead to
slightly simplify their declaration:

```kotlin
val HighlightedCustomVariant by CustomStyle.addVariantBase {
    Modifier.backgroundColor(Colors.Green)
}

// Short for
// val HighlightedCustomVariant by CustomStyle.addVariant {
//   base { Modifier.backgroundColor(Colors.Green) }
// }
```

#### Structuring code around component styles

Silk uses component styles when defining its widgets, and you can too! The full pattern looks like this:

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

In other words:
* you define a composable widget method
* it should take in a `modifier` as its first optional parameter.
* it should take in an optional `CssStyleVariant` parameter (typed to your unique `ComponentKind` implementation)
* inside your widget, you should apply the modifiers in order of: base style, then variant, then user modifier.
* it should end with a `@Composable` context lambda parameter (unless this widget doesn't support custom content)

A caller might call your widget one of several ways:

```kotlin
// Approach #1: Use default styling
CustomWidget { /* ... */ }

// Approach #2: Tweak default styling with a variant
CustomWidget(variant = TransparentWidgetVariant) { /* ... */ }

// Approach #3: Tweak default styling with inline overrides
CustomWidget(Modifier.backgroundColor(Colors.Blue)) { /* ... */ }

// Approach #4: Tweak default styling with both a variant and inline overrides
CustomWidget(Modifier.backgroundColor(Colors.Blue), variant = TransparentWidgetVariant) { /* ... */ }
```

### Animations

In CSS, animations work by letting you define keyframes in a stylesheet which you then reference, by name, in an
animation style. You can read more about them
[on Mozilla's documentation site](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations/Using_CSS_animations).

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

```kotlin
val ShiftRightKeyframes = Keyframes {
    from { Modifier.left(0.px) }
    to { Modifier.left(200.px) }
}

// Later
Div(
    Modifier
        .size(100.px).backgroundColor(Colors.Red).position(Position.Relative)
        .animation(ShiftRightKeyframes.toAnimation(
            duration = 5.s,
            iterationCount = AnimationIterationCount.Infinite
        ))
        .toAttrs()
)
```

The name of the keyframes block is automatically derived from the property name (here, `ShiftRightKeyframes` is
converted into `"shift-right"`). You can then use the `toAnimation` method to convert your collection of keyframes into
an animation that uses them, which you can pass into the `Modifier.animation` modifier.

> [!IMPORTANT]
> When you declare a `Keyframes` animation, it must be public. This is because code gets generated inside a `main.kt`
> file by the Kobweb Gradle plugin, and that code needs to be able to access your variant in order to register it.
>
> In general, it's a good idea to think of animations as global anyway, since technically they all live in a globally
> applied stylesheet, and you have to make sure that the animation name is unique across your whole application.
>
> You can technically make an animation private if you add a bit of boilerplate to handle the registration yourself:
>
> ```kotlin
> @Suppress("PRIVATE_KEYFRAMES")
> private val ExampleKeyframes = Keyframes { /* ... */ }
> // Or, `private val _ExampleKeyframes`
>
> @InitSilk
> fun registerPrivateAnim(ctx: InitSilkContext) {
>     ctx.stylesheet.registerKeyframes("example", ExampleKeyframes)
> }
> ```
>
> However, you are encouraged to keep your keyframes public and let the Kobweb Gradle plugin handle everything for you.

### ElementRefScope and raw HTML elements

Occasionally, you may need access to the raw element backing the Silk widget you've just created. All Silk widgets
provide an optional `ref` parameter which takes a listener that provides this information.

```kotlin
Box(
    ref = /* ... */
) {
    /* ... */
}
```

All `ref` callbacks (discussed more below) will receive an `org.w3c.dom.Element` subclass. You can check out the
[Element](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-element/) class (and its often more
relevant [HTMLElement](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-h-t-m-l-element/) inheritor) to see the
methods and properties that are available on it.

Raw HTML elements expose a lot of functionality not available through the higher-level Compose HTML APIs.

#### `ref`

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

#### `disposableRef`

If you need to know both when the element enters AND exits the DOM, you can use `disposableRef` instead. With
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
will also be triggered in that case, as the old effect gets discarded.

#### `refScope`

And, finally, you may want to have multiple listeners that are recreated independently of one another based on different
keys. You can use `refScope` as a way to combine two or more `ref` and/or `disposableRef` calls in any combination:

```kotlin
val isFeature1Enabled: Boolean = /* ... */
val isFeature2Enabled: Boolean = /* ... */

Box(
    ref = refScope {
        ref(isFeature1Enabled) { element -> /* ... */ }
        disposableRef(isFeature2Enabled) { element -> /* ... */; onDispose { /* ... */ } }
    }
)
```

#### Compose HTML refs

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

*The above snippet was adapted from [the official tutorials](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/HTML/Using_Effects#ref-in-attrsbuilder).*

You could put that exact same logic inside the `Modifier.toAttrs` block if you're terminating some modifier chain:

```kotlin
Div(attrs = Modifier.toAttrs {
  ref { element -> /* ... */; onDispose { /* ... */ } }
})
```

Unlike Silk's version of `ref`, Compose HTML's version does not accept keys. If you need this behavior and if the
Compose HTML widget accepts a content block (many of them do), you can call Silk's `registerRefScope` method directly
within it:

```kotlin
Div {
  registerRefScope(
    disposableRef { element -> /* ... */; onDispose { /* ... */ } }
    // or ref { element -> /* ... */ }
  )
}
```

### Style Variables

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

> [!TIP]
> Compose HTML provides a `CSSLengthValue`, which represents concrete values like `10.px` or `5.cssRem`. However, Kobweb
> provides a `CSSLengthNumericValue` type which represents the concept more generally, e.g. as the result of
> intermediate calculations. There are `CSS*NumericValue` types provided for all relevant units, and it is recommended
> to use them when declaring style variables as they more naturally support being used in calculations.
>
> We discuss [CSSNumericValue types▼](#cssnumeric) in more detail later in this document.

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
  // Will be the value of the dialogWidth variable if it was set, otherwise 500px
  Modifier.width(dialogWidth.value(500.px))
}
```

Additionally, you can also provide a default fallback value when declaring the variable:

```kotlin
// Note the default fallback: 100px
val dialogWidth by StyleVariable<CSSLengthNumericValue>(100.px)

val DialogStyle100 = CssStyle.base {
  // Uses default fallback. width = 100px
  Modifier.width(dialogWidth.value())
}
val DialogStyle200 = CssStyle.base {
  // Uses specific fallback. width = 200px
  Modifier.width(dialogWidth.value(200.px))
}
val DialogStyle300 = CssStyle.base {
  // Fallback (400px) ignored because variable is set explicitly. width = 300px
  Modifier.setVariable(dialogWidth, 300.px).width(dialogWidth.value(400.px))
}
```

> [!CAUTION]
> In the above example in the `DialogStyle300` style, we set a variable and query it in the same line, which we did
> purely for demonstration purposes. In practice, you would probably never do this -- the variable would have been set
> separately elsewhere, e.g. in an inline style or on a parent container.

To demonstrate these concepts all together, below we declare a background color variable, create a root container scope
which sets it, a child style that uses it, and, finally, a child style variant that overrides it:

```kotlin
// Default to a debug color, so if we see it, it indicates we forgot to set it later
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
                // 1: Read color from ContainerStyle
                Box(SquareStyle.toModifier())
                // 2: Override color via RedSquareStyle
                Box(RedSquareStyle.toModifier())
            }
            Row {
                // 3: Override color via inline styles
                Box(SquareStyle.toModifier().setVariable(bgColor, Colors.Green))
                Span(Modifier.setVariable(bgColor, Colors.Yellow).toAttrs()) {
                    // 4: Read color from parent's inline style
                    Box(SquareStyle.toModifier())
                }
            }
        }
    }
}
```

The above renders the following output:

![Kobweb CSS Variables, Squares example](https://github.com/varabyte/media/raw/main/kobweb/images/kobweb-variable-squares-example.png)

---

You can also set CSS variables directly from code if you have access to the backing HTML element. Below, we use the
`ref` callback to get the backing element for a fullscreen `Box` and then use a `Button` to set it to a random color
from the colors of the rainbow:

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
    val roygbiv = listOf(Colors.Red, /*...*/ Colors.Violet)

    var screenElement: HTMLElement? by remember { mutableStateOf(null) }
    Box(ScreenStyle.toModifier(), ref = ref { screenElement = it }) {
        Button(onClick = {
            // You can call `setVariable` on the backing HTML element to set the variable value directly
            screenElement!!.setVariable(bgColor, roygbiv.random())
        }) {
            Text("Click me")
        }
    }
}
```

The above results in the following UI:

![Kobweb CSS Variables, Rainbow example](https://github.com/varabyte/media/raw/main/kobweb/screencasts/kobweb-variable-roygbiv-example.gif)

#### In many cases, don't use CSS Variables

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
    val roygbiv = listOf(Colors.Red, /*...*/ Colors.Violet)

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
  example, Wordle has light / dark + normal / contrast modes.
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

### Font Awesome

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

### Material Design Icons

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

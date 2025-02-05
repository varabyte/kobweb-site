---
follows: CssNumericValue
---

`StyleVariable`s work in a subtle way that is usually fine until it isn't -- which is often when you try to interact
with their values instead of just passing them around.

Specifically, this would compile but be a problem at runtime:

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


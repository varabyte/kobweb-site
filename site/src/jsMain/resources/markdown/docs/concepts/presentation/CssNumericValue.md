---
title: CSSNumericValue Type-aliases
follows: CssLayers
---

Kobweb introduces a handful of type-aliases for CSS unit values, basing them off of the `CSSNumericValue` class and
extending the set defined by Compose HTML. They are:

```kotlin
typealias CSSAngleNumericValue = CSSNumericValue<out CSSUnitAngle>
typealias CSSLengthOrPercentageNumericValue = CSSNumericValue<out CSSUnitLengthOrPercentage>
typealias CSSLengthNumericValue = CSSNumericValue<out CSSUnitLength>
typealias CSSPercentageNumericValue = CSSNumericValue<out CSSUnitPercentage>
typealias CSSFlexNumericValue = CSSNumericValue<out CSSUnitFlex>
typealias CSSTimeNumericValue = CSSNumericValue<out CSSUnitTime>
```

This section explains why they were added and why you should almost always prefer using them.

### Background

#### CSSSizeValue

When you write CSS values like `10.px`, `5.cssRem`, `45.deg`, or even `30.s` into your code, you normally don't have to
think too much about their types. You just create them and pass them into the appropriate Kobweb / Compose HTML APIs.

Let's discuss what is actually happening when you do this. Compose HTML provides a `CSSSizeValue` class which represents
a number value and its unit.

```kotlin
val lengthValue = 10.px // CSSSizeValue<CSSUnit.px> (value = 10 and unit = px)
val angleValue = 45.deg // CSSSizeValue<CSSUnit.deg> (value = 45 and unit = deg)
```

This is a pretty elegant approach, but the types are verbose. This can be troublesome when writing code that needs to
work with them:

```kotlin
val lengths: List<CSSSizeValue<CSSUnit.px>>
fun drawArc(arc: CSSSizeValue<CSSUnit.deg>)
```

Note also that the above cases are overly restrictive, only supporting a single length and angle type, respectively. We
usually want to support all relevant types (e.g. `px`, `em`, `cssRem`, etc. for lengths; `deg`, `rad`, `grad`, and
`turn` for angles). We can do this with the following `out` syntax:

```kotlin
val lengths: List<CSSSizeValue<out CSSUnitLength>>
fun drawArc(arc: CSSSizeValue<out CSSUnitAngle>)
```

What a mouthful!

As a result, the Compose HTML team added type-aliases for all these unit types, such as `CSSLengthValue`
and `CSSAngleValue`. Now, you can write the above code like:

```kotlin
val lengths: List<CSSLengthValue>
fun drawArc(arc: CSSAngleValue)
```

Much better! Seems great. No problems, right? *Right?!*

#### CSSNumericValue

You can probably tell by my tone: Yes problems.

To explain, we first need to talk about `CSSNumericValue`.

It is common to transform values in CSS using many of its various mathematical functions. Perhaps you want to take the
sum of two different units (`10.px + 5.cssRem`) or call some other math function (`clamp(1.cssRem, 3.vw)`). These
operations return intermediate values that cannot be directly queried like a `CSSSizeValue` can.

This is handled by the `CSSNumericValue` class, also defined by Compose HTML (and which is actually a base class
of `CSSSizeValue`).

```kotlin
val lengthSum = 10.px + 2.cssRem // CSSNumericValue<CSSUnitLength>
val angleSum = 45.deg + 1.turn // CSSNumericValue<CSSAngleLength>
```

These numeric operations are of course useful to the browser, which can resolve them into absolute screen values, but
for us in user space, they are opaque calculations.

In practice, however, that's fine! The limited view of these values does not matter because we rarely need to query them
in our code. In almost all cases, we just take some numeric value, optionally tweak it by doing some more math on it,
and then pass it onto the browser.

Because it is opaque, `CSSNumericValue` is far more flexible and widely applicable than `CSSSizeValue` is. If you are
writing a function that takes a parameter, or declaring a `StyleVariable` tied to some length or time, you almost always
want to use `CSSNumericValue` and not `CSSSizeValue`.

### Prefer using Kobweb's `CSSNumericValue` type-aliases

As mentioned above, the Compose HTML team created their unit-related type-aliases against the `CSSSizeValue` class.

This decision makes it really easy to write code that works well when you test it with concrete size values but is
actually more restrictive than you expected.

Kobweb ensures its APIs all reference its `CSSNumericValue` type-aliases:

```kotlin
// Legacy Kobweb
fun Modifier.lineHeight(value: CSSLengthOrPercentageValue): Modifier = styleModifier {
  lineHeight(value)
}

// Modern Kobweb
fun Modifier.lineHeight(value: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
  lineHeight(value)
}
```

If you are using style variables in your code or writing your own functions that take CSS units as arguments, you might
be referencing the Compose HTML types. Your code will still work fine, but you are strongly encouraged to migrate them
to Kobweb's newer set, in order to make your code more flexible about what it can accept:

```kotlin
// Not recommended
val MyFontSize by StyleVariable<CSSLengthValue>
fun drawArc(arc: CSSAngleValue)

// Recommended
val MyFontSize by StyleVariable<CSSLengthNumericValue>
fun drawArc(arc: CSSAngleNumericValue)
```

> [!NOTE]
> Perhaps in the future, the Compose HTML team might consider updating their type-aliases to use the `CSSNumericValue`
> type and not the `CSSSizeValue` type. If that happens, we can revert our changes and delete this section. But until
> then, it's worth understanding why Kobweb introduces its own type-aliases and why you are encouraged to use them
> instead of the Compose HTML versions.

---
title: Learning CSS
follows: Silk
imports:
  - com.varabyte.kobweb.site.components.widgets.docs.css.*
---

Many developers new to web development have heard horror stories about CSS, and they might hope that Kobweb, by
leveraging Kotlin and a Jetpack Compose-inspired API, means they won't have to learn it.

It's worth dispelling that illusion! CSS is inevitable.

That said, CSS's reputation is probably worse than it deserves to be. Many of its features are actually fairly
straightforward and some are quite powerful. For example, you can efficiently declare that your element should be
wrapped with a thin border, with round corners, casting a drop shadow beneath it to give it a feeling of depth,
painted with a gradient effect for its background, and animated with an oscillating, tilting effect.

It's hoped that, once you've learned a bit of CSS through Kobweb, you'll find yourself actually enjoying it (sometimes)!

## Ways Kobweb helps with CSS

Kobweb offers enough of a layer of abstraction that you can learn CSS in a more incremental way.

First and most importantly, Kobweb gives you a Kotlin-idiomatic type-safe API to CSS properties. This is a major
improvement over writing CSS in text files which fail silently at runtime.

Next, layout widgets like `Box`, `Column`, and `Row` can get you up and running quickly with rich, complex layouts
before ever having to understand what a "flex layout" is.

Meanwhile, using `CssStyle` can help you break your CSS up into smaller, more manageable
pieces that live close to the code that actually uses them, allowing your project to avoid a giant, monolithic CSS file.
(Such giant CSS files are one of the reasons CSS has an intimidating reputation).

For example, a CSS file that could easily look like this:

```css
/* Dozens of rules... */

.important {
  background-color: red;
  font-weight: bold;
}

.important:hover {
  background-color: pink;
}

/* Dozens of other rules... */

.post-title {
    font-size: 24px;
}

/* A dozen more more rules... */
```

can migrate to this in Kobweb:

```kotlin
//------------------ CriticalInformation.kt

val ImportantStyle = CssStyle {
  base {
    Modifier.backgroundColor(Colors.Red).fontWeight(FontWeight.Bold)
  }

  hover {
    Modifier.backgroundColor(Colors.Pink)
  }
}

//------------------ Post.kt

val PostTitleStyle = CssStyle.base { Modifier.fontSize(24.px) }
```

Next, Silk provides a `Deferred` composable which lets you declare code that won't get rendered until the rest of the
DOM finishes first, meaning it will appear on top of everything else. This is a clean way to avoid setting CSS z-index
values (another aspect of CSS that has a bad reputation).

And finally, Silk aims to provide widgets with default styles that look good for many sites. This means you should be
able to rapidly develop common UIs without running into some of the more complex aspects of CSS.

## A concrete example

Let's walk through an example of layering CSS effects on top of a basic element.

> [!TIP]
> Two of the best learning resources for CSS properties are `https://developer.mozilla.org`
> and `https://www.w3schools.com`. Keep an eye out for these when you do a web search.

We'll create the bordered, floating, oscillating element we discussed earlier. Rereading it now, here are the concepts
we need to figure out how to do:

* Create a border
* Round out the corners
* Add a drop shadow
* Add a gradient background
* Add a wobble animation

Let's say we want to create an attention grabbing "welcome" widget
on our site. You can always start with an empty box, which we'll put some text in:

```kotlin
Box(Modifier.padding(topBottom = 5.px, leftRight = 30.px)) {
  Text("WELCOME!!")
}
```

{{{ CssExample1 }}}

**Create a border**

Next, search the internet for "CSS border". One of the top links should be: https://developer.mozilla.org/en-US/docs/Web/CSS/border

Skim the docs and play around with the interactive examples. With an understanding of the border property now, let's use
code completion to discover the Kobweb version of the API:

```diff-kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
+   .border(1.px, LineStyle.Solid, Colors.Black)
) {
  Text("WELCOME!!")
}
```

{{{ CssExample2 }}}

**Round out the corners**

Search for "CSS rounded corners". It turns out the CSS property in this case is called a "border
radius": https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius

```diff-kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
+   .borderRadius(5.px)
) {
  Text("WELCOME!!")
}
```

{{{ CssExample3 }}}

**Add a drop shadow**

Search for "CSS shadow". There are a few types of CSS shadow features, but after some quick reading, we
realize we want to use box shadows: https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow

After playing around with blur and spread values, we get something that looks decent:

```diff-kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
+   .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
) {
  Text("WELCOME!!")
}
```

{{{ CssExample4 }}}

**Add a gradient background**

Search for "CSS gradient background". This isn't a straightforward CSS property like the previous cases, so we instead
get a more general documentation page explaining the feature: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_images/Using_CSS_gradients

This case turns out to be a little trickier to ultimately find the Kotlin, type-safe equivalent, but if you dig a bit
more into the CSS docs, you'll learn that a linear gradient is a type of background image.

```diff-kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
    .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
+   .backgroundImage(
+       linearGradient(
+          LinearGradient.Direction.ToRight, Colors.LightBlue, Colors.LightGreen
+       )
+    )
) {
  Text("WELCOME!!")
}
```

{{{ CssExample5 }}}

**Add a wobble animation**

And finally, search for "CSS
animations": https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_animations/Using_CSS_animations

You can review the ${DocsLink("animation section", "silk#animations")} for a refresher on how Kobweb supports this
feature, which requires declaring a top-level `Keyframes` block which then gets referenced inside an animation modifier:

```diff-kotlin
// Top level property
+val WobbleKeyframes = Keyframes {
+  from { Modifier.rotate((-5).deg) }
+  to { Modifier.rotate(5.deg) }
+}

// Inside your @Page composable
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
    .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
    .backgroundImage(linearGradient(LinearGradient.Direction.ToRight, Colors.LightBlue, Colors.LightGreen))
+   .animation(
+     WobbleKeyframes.toAnimation(
+       duration = 1.s,
+       iterationCount = AnimationIterationCount.Infinite,
+       timingFunction = AnimationTimingFunction.EaseInOut,
+       direction = AnimationDirection.Alternate,
+     )
    )
) {
  Text("WELCOME!!")
}
```

{{{ CssExample6 }}}

**And we're done!**

The above element isn't going to win any style awards, but I hope this demonstrates how much power CSS can give you in
just a few declarative lines of code. And thanks to the nature of CSS, combined with Kobweb's live reloading experience,
we were able to experiment with our idea incrementally.

## CSS 2 Kobweb

One of our main project contributors created a site called [CSS 2 Kobweb](https://opletter.github.io/css2kobweb/)
which aims to simplify the process of converting CSS examples to equivalent Kobweb `CssStyle` and/or `Modifier`
declarations.

![CSS 2 Kobweb example](https://github.com/varabyte/media/raw/main/kobweb/images/css/css2kobweb.png)

> [!TIP]
> [CSS 2 Kobweb](https://opletter.github.io/css2kobweb/) also supports specifying class name selectors and keyframes.
> For example, see what happens when you paste in the following CSS code:
> ```css
> .site-banner {
>   position: relative;
>   padding-left: 10px;
>   padding-top: 5%;
>   animation: slide-in 3s linear 1s infinite;
>   background-position: bottom 10px right;
>   background-image: linear-gradient(to bottom, #eeeeee, white 25px);
> }
> .site-banner:hover {
>   color: rgb(40, 40, 40);
> }
> @keyframes slide-in {
>   from {
>     transform: translateX(-2rem) scale(0.5);
>   }
>   to {
>     transform: translateX(0);
>     opacity: 1;
>   }
> }
> ```

The web is full of examples of interesting CSS effects. Almost any CSS-related search will result in tons of
StackOverflow answers, interactive playgrounds featuring WYSIWYG editors, and blog posts. Many of these introduce some
really novel CSS examples. This is a great way to learn more about web development!

However, as the previous section demonstrated, it can sometimes be a pain to go from a CSS example to the equivalent
Kobweb code. We hope that *CSS 2 Kobweb* can help with that.

This project is already very useful, but it's still early days. If you find cases of *CSS 2 Kobweb* that are
incorrect, please consider [filing an issue](https://github.com/opLetter/css2kobweb/issues) in their repository.

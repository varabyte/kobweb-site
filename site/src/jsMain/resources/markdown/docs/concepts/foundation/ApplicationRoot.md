---
description: How to declare an entry point that will be called for every page on your site.
follows: Routing
---

## `KobwebApp` and `SilkApp`

By default, Kobweb will automatically root every page to the [`KobwebApp` composable](https://github.com/varabyte/kobweb/blob/main/frontend/kobweb-core/src/jsMain/kotlin/com/varabyte/kobweb/core/App.kt)
(or, if using Silk, to a [`SilkApp` composable](https://github.com/varabyte/kobweb/blob/main/frontend/kobweb-silk/src/jsMain/kotlin/com/varabyte/kobweb/silk/SilkApp.kt)).
These perform some minimal common work (e.g. applying CSS styles) that should be present across your whole site.

This means if you register a page:

```kotlin "jsMain/kotlin/com/mysite/pages/Index.kt"
@Page
@Composable
fun HomePage() {
    /* ... */
}
```

then the final result that actually runs on your site will be:

```kotlin "build/generated/kobweb/app/src/jsMain/main.kt"
KobwebApp {
  HomePage()
}
```

## `@App`

It is likely you'll want to configure this further for your own application. Perhaps you have some initialization logic
that you'd like to run before any page gets run (like logic for updating saved settings into local storage). And for
many apps it's a great place to specify a full screen Silk `Surface` as that makes all children beneath it transition
between light and dark colors smoothly.

In this case, you can create your own root composable and annotate it with `@App`. If present, Kobweb will use that
instead of its own default. You should, of course, delegate to `KobwebApp` (or `SilkApp` if using Silk), as the
initialization logic from those methods should still be run.

Here's an example application composable override that I use in many of my own projects:

```kotlin "jsMain/kotlin/com/mysite/AppEntry.kt"
@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
  SilkApp {
    val colorMode = ColorMode.current
    LaunchedEffect(colorMode) { // Relaunched every time the color mode changes
      localStorage.setItem("color-mode", colorMode.name)
    }

    // A full screen Silk surface. Sets the background based on Silk's palette
    // and animates color changes.
    Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
      content()
    }
  }
}
```

You can define *at most* a single `@App` on your site, or else the Kobweb Application plugin will complain at build
time.

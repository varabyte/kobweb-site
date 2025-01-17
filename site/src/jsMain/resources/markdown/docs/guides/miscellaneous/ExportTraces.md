---
follows: Cors
---

The Kobweb export feature is built on top of [Microsoft Playwright](https://playwright.dev/), a solution for making it
easy to download and run browsers programmatically.

One of the features provided by Playwright is the ability to generate traces, which are essentially detailed reports
you can use to understand what is happening as your site loads. Kobweb exposes this feature through the `export` block
in your Kobweb application's build script.

Enabling traces is easy:

```kotlin
// build.gradle.kts
plugins {
  // ... other plugins ...
  alias(libs.plugins.kobweb.application)
}

kobweb {
  app {
    export {
      enableTraces()
    }
  }
}
```

You can pass in parameters to configure the `enableTraces` method, but by default, it will generate trace files into
your `.kobweb/export-traces/` directory.

Once enabled, you can run `kobweb export`, then once exported, open any of the generated `*.trace.zip` files by
navigating to them using your OS's file explorer and drag-and-dropping them into
the [Playwright Trace Viewer](https://trace.playwright.dev/).

> [!TIP]
> You can learn more about how to use the Trace
> Viewer [using the official documentation](https://playwright.dev/docs/trace-viewer).

It's not expected many users will need to debug their site exports, but it's a great tool to have (especially combined
with the [server logs feature](#kobweb-server-logs)) to diagnose if one of your pages is taking longer to export than
expected.

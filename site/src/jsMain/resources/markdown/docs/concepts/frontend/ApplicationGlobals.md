---
follows: Exporting
---

Occasionally you might find yourself with a value at build time that you want your site to know at runtime.

For example, maybe you want to specify a version based on the current UTC timestamp. Or maybe you want to read a system
environment variable's value and pass that into your Kobweb site as a way to configure its behavior.

This is supported via Kobweb's `AppGlobals` singleton, which is like a `Map<String, String>` whose values you can set
from your project's build script using the `kobweb.app.globals` property.

Let's demonstrate this with the UTC version example.

In your application's `build.gradle.kts`, add the following code:

```kotlin
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
  /* ... */
  alias(libs.plugins.kobweb.application)
}

kobweb {
  app {
    globals.put(
      "version",
      LocalDateTime
          .now(ZoneId.of("UTC"))
          .format(DateTimeFormatter.ofPattern("yyyyMMdd.kkmm"))
    )
  }
}
```

You can then access them via the `AppGlobals.get` or `AppGlobals.getValue` methods:

```kotlin
val version = AppGlobals.getValue("version")
```

In your Kotlin project somewhere, it is recommended that you either add some type-safe extension methods, or you can
create your own wrapper object (based on your preference):

```kotlin
// SiteGlobals.kt

import com.varabyte.kobweb.core.AppGlobals

// Extension method approach ---------------------

val AppGlobals.version: String
  get() = getValue("version")

// Wrapper object approach -----------------------

object SiteGlobals {
  val version: String = AppGlobals.getValue("version")
}
```

At this point, you can access this value in your site's code, say for a tiny label that would look good in a footer
perhaps:

```kotlin
// components/widgets/SiteVersion.kt

val VersionTextStyle = CssStyle.base {
  Modifier.fontSize(0.6.cssRem)
}

@Composable
fun SiteVersion(modifier: Modifier = Modifier) {
  // Extension method approach
  val versionLabel = "v" + AppGlobals.version
  // Wrapper object approach
  val versionLabel = "v" + SiteGlobals.version

  SpanText(versionLabel, VersionTextStyle.toModifier().then(modifier))
}
```

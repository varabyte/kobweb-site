---
follows: Exporting
---

Occasionally you might find yourself with a value at build time that you want your site to know at runtime.

For example, maybe you want to generate a useful version ID that is based on the current UTC timestamp captured at
build time. Or maybe you want to read a system environment variable's value and pass that into your Kobweb site as a way
to configure its behavior.

## Setting global values

This is supported via Kobweb's `AppGlobals` singleton, which is like a `Map<String, String>` whose values you can set
from your project's build script using the `kobweb.app.globals` property and then read later from your site.

Let's demonstrate this with the UTC timestamp version example.

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

## Reading global values

You can then access such globals via the `AppGlobals.get` (or `AppGlobals.getValue` if you are sure it is non-null)
methods:

```kotlin
val version = AppGlobals.getValue("version")
```

In your Kotlin project somewhere, we recommend you have one location where you can declare properties for accessing your
global values, instead of using `get` methods with string values all over the place.

We suggest two approaches here, one using extension methods and the other using a wrapper object. Either is fine! You
are encouraged to choose whatever you prefer:

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

At this point, you can access this value in your site's code, say for a label that would look good in a footer perhaps:

```kotlin
// components/widgets/SiteVersion.kt

@Composable
fun SiteVersion() {
  // Extension method approach
  val versionLabel = "v" + AppGlobals.version
  // Wrapper object approach
  val versionLabel = "v" + SiteGlobals.version

  Text(versionLabel)
}
```

---
title: "应用全局变量"
follows: Exporting
---

有时候你可能会遇到这样的情况：需要在构建时设置一个值，并在运行时让你的网站能够访问这个值。

例如，你可能想要生成一个基于当前 UTC 时间戳的有用版本 ID。或者，你可能想要读取系统环境变量的值，并将其传递到你的 Kobweb 网站中，作为配置其行为的一种方式。

## 设置全局值

Kobweb 通过 `AppGlobals` 单例支持这一功能，它类似于一个 `Map<String, String>`，你可以在项目的构建脚本中使用 `kobweb.app.globals` 属性设置其值，然后在网站中读取这些值。

让我们用 UTC 时间戳版本的例子来演示这个功能。

在你的应用程序的 `build.gradle.kts` 中，添加以下代码：

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

## 读取全局值

你可以通过 `AppGlobals.get`（如果确定值非空，则使用 `AppGlobals.getValue`）方法访问这些全局值：

```kotlin
val version = AppGlobals.getValue("version")
```

在你的 Kotlin 项目中，我们建议你在一个地方声明用于访问全局值的属性，而不是在代码的各个地方使用带有字符串值的 `get` 方法。

这里我们提供两种方法，一种使用扩展方法，另一种使用包装对象。两种方法都可以！我们鼓励你选择你喜欢的方式：

```kotlin
// SiteGlobals.kt

import com.varabyte.kobweb.core.AppGlobals

// 扩展方法方式 ---------------------

val AppGlobals.version: String
  get() = getValue("version")

// 包装对象方式 -----------------------

object SiteGlobals {
  val version: String = AppGlobals.getValue("version")
}
```

此时，你就可以在网站代码中访问这个值了，比如可以用在页脚的标签中：

```kotlin
// components/widgets/SiteVersion.kt

@Composable
fun SiteVersion() {
  // 扩展方法方式
  val versionLabel = "v" + AppGlobals.version
  // 包装对象方式
  val versionLabel = "v" + SiteGlobals.version

  Text(versionLabel)
}
```

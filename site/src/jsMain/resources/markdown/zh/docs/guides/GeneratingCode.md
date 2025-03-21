---
description: A guide for how to generate code and resources for your site from your build script.
title: 在构建时生成代码
follows: ExistingBackend
---

有时候，你可能会发现某些网站代码使用程序生成比手动编写更好。

推荐的最佳实践是创建一个与其独特输出目录关联的 Gradle 任务，使用该任务将代码写入该目录下的磁盘，然后将该任务添加为项目的源目录。

> [!NOTE]
> 鼓励使用具有独特输出目录的任务的原因是这种方法对 Gradle 缓存非常友好。你可以[在这里阅读更多内容](https://docs.gradle.org/current/userguide/build_cache_concepts.html#concepts_overlapping_outputs)
> 以了解更详细的信息。
>
> 将任务添加为源目录可确保在运行负责处理项目的 Kobweb 任务之前自动触发它。

即使你只计划生成单个文件，也应该这样做。这是因为将任务与输出目录关联是使其能够代替源目录的关键。

这种方法的结构通常如下所示：

```kotlin
// 例如 site/build.gradle.kts

val generateCodeTask = tasks.register("generateCode") {
  // 你的任务可能不需要输入文件或目录，如果是这样，
  // 可以排除下一行。如果需要的话，这里假设
  // 它是一个资源文件，但你可以根据需要更改。
  val resInputDir = layout.projectDirectory.dir("src/jsMain/resources")
  // 这里使用 $name 为此任务创建唯一的输出目录
  val genOutputDir = layout.buildDirectory.dir("generated/$name/src/jsMain/kotlin")

  inputs.dir(resInputDir).withPathSensitivity(PathSensitivity.RELATIVE)
  outputs.dir(genOutputDir)

  doLast {
    // 在 resInputDir 中查找并解析文件
    val inputFile = resInputDir.file("path/to/some/resource.txt").asFile
    // 假设你在此构建脚本的其他地方编写了 `parseDataFrom`
    val parsedData = parseDataFrom(inputFile) 

    genOutputDir.get().file("org/example/pages/SomeCode.kt").asFile.apply {
      parentFile.mkdirs()

      // 使用 parsedData，将输出写入此文件
      writeText("...")

      // 记录日志以通知用户！
      println("Generated $absolutePath")
    }
  }
}

kotlin {
  configAsKobwebApplication()
  commonMain.dependencies { /* ... */ }
  jsMain { 
    // ↓↓↓↓ 在此处引用你的任务 ↓↓↓↓ 
    kotlin.srcDir(generateCodeTask) 
    dependencies { /* ... */ }
  }
}
```

### 生成资源

如果你想生成最终作为文件出现在你的网站中的*资源*（例如 `mysite.com/rss.xml`）而不是代码，
主要需要更改的是将 `kotlin.srcDir` 改为 `resources.srcDir`：

```kotlin
// 例如 site/build.gradle.kts

val generateResourceTask = tasks.register("generateResource") {
  // 这里使用 $name 为此任务创建唯一的输出目录
  val genOutputDir = layout.buildDirectory.dir("generated/$name/src/jsMain/resources")

  outputs.dir(genOutputDir)

  doLast {
    // 注意：这里使用 "public/" 以便导出过程能找到它并将其放入最终网站
    genOutputDir.get().file("public/rss.xml").asFile.apply {
      parentFile.mkdirs()
      writeText(/* ... */)

      println("Generated $absolutePath")
    }
  }
}

kotlin {
  configAsKobwebApplication()
  commonMain.dependencies { /* ... */ }
  jsMain {
    // ↓↓↓↓ 在此处引用你的任务 ↓↓↓↓ 
    resources.srcDir(generateResourceTask)
    dependencies { /* ... */ }
  }
}
```


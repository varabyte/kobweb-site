---
follows: ExistingBackend
---

Occasionally, you might find yourself wanting code for your site that is better generated programmatically than written
by hand.

The recommended best practice is to create a Gradle task that is associated with its own unique output directory, use
the task to write some code to disk under that directory, and then add that task as a source directory for your project.

> [!NOTE]
> The reason to encourage tasks with their own unique output directory is because this approach is very friendly with
> Gradle caching. You may [read more here](https://docs.gradle.org/current/userguide/build_cache_concepts.html#concepts_overlapping_outputs)
> to learn about this in more detail.
>
> Adding your task as a source directory ensures it will get triggered automatically before the Kobweb tasks responsible
> for processing your project are themselves run.

You want to do this even if you only plan to generate a single file. This is because associating your task with an
output directory is what enables it to be used in place of a source directory.

The structure for this approach generally looks like this:

```kotlin
// e.g. site/build.gradle.kts

val generateCodeTask = tasks.register("generateCode") {
  // You may not need an input file or dir for your task, and if so,
  // you can exclude the next line. If you do need one, I'm assuming
  // here it is a resource file, but you can change this to whatever
  // you need.
  val resInputDir = layout.projectDirectory.dir("src/jsMain/resources")
  // $name here to create a unique output directory just for this task
  val genOutputDir = layout.buildDirectory.dir("generated/$name/src/jsMain/kotlin")

  inputs.dir(resInputDir).withPathSensitivity(PathSensitivity.RELATIVE)
  outputs.dir(genOutputDir)

  doLast {
    // find and parse file found in resInputDir
    val inputFile = resInputDir.file("path/to/some/resource.txt").asFile
    // Assume you wrote `parseDataFrom` elsewhere in this build script
    val parsedData = parseDataFrom(inputFile) 

    genOutputDir.get().file("org/example/pages/SomeCode.kt").asFile.apply {
      parentFile.mkdirs()

      // using parsedData, write output to this file
      writeText("...")

      // log to inform the user!
      println("Generated $absolutePath")
    }
  }
}

kotlin {
  configAsKobwebApplication()
  commonMain.dependencies { /* ... */ }
  jsMain { 
    // ↓↓↓↓ Reference your task here ↓↓↓↓ 
    kotlin.srcDir(generateCodeTask) 
    dependencies { /* ... */ }
  }
}
```

### Generating resources

In case you want to generate *resources* that end up in your final site as files (e.g. `mysite.com/rss.xml`) and not
code, the main change you need to make is migrating the line `kotlin.srcDir` to `resources.srcDir`:

```kotlin
// e.g. site/build.gradle.kts

val generateResourceTask = tasks.register("generateResource") {
  // $name here to create a unique output directory just for this task
  val genOutputDir = layout.buildDirectory.dir("generated/$name/src/jsMain/resources")

  outputs.dir(genOutputDir)

  doLast {
    // NOTE: Use "public/" here so the export pass will find it and put it into the final site
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
    // ↓↓↓↓ Reference your task here ↓↓↓↓ 
    resources.srcDir(generateResourceTask)
    dependencies { /* ... */ }
  }
}
```


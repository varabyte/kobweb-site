---
follows: ProjectOrganization
---

If you create a markdown file under the `jsMain/resources/markdown` folder, a corresponding page will be created for you
at build time, using the filename as its path.

For example, if I create the following file:

```markdown
// jsMain/resources/markdown/docs/tutorial/Kobweb.md

# Kobweb Tutorial

...
```

this will create a page that I can then visit by going to `mysite.com/docs/tutorial/kobweb`

### Front Matter

Front Matter is metadata that you can specify at the beginning of your document, like so:

```markdown
---
title: Tutorial
author: bitspittle
---

...
```

In a following section, we'll discuss how to embed code in your markdown, but for now, know that these key / value pairs
can be queried in code using the page's context:

```kotlin
@Composable
fun AuthorWidget() {
  val ctx = rememberPageContext()
  // Note: You can use `markdown!!` only if you're sure that
  // this composable is called while inside a page generated
  // from Markdown.
  val author = ctx.markdown!!.frontMatter.getValue("author").single()
  Text("Article by $author")
}
```

> [!IMPORTANT]
> If you're not seeing `ctx.markdown` autocomplete, you need to make sure you depend on the
> `com.varabyte.kobwebx:kobwebx-markdown` artifact in your project's `build.gradle`.

#### Root

Within your front matter, there's a special value which, if set, will be used to render a root `@Composable` that adds
the rest of your markdown code as its content. This is useful for specifying a layout for example:

```markdown
---
root: .components.layout.DocsLayout
---

# Kobweb Tutorial
```

The above will generate code like the following:

```kotlin
import com.mysite.components.layout.DocsLayout

@Composable
@Page
fun KobwebPage() {
  DocsLayout {
    H1 {
      Text("Kobweb Tutorial")
    }
  }
}
```

If you have a default root that you'd like to use in most / all of your markdown files, you can specify it in the
markdown block in your build script:

```kotlin
// site/build.gradle.kts

kobweb {
  markdown {
    defaultRoot.set(".components.layout.MarkdownLayout")
  }
}
```

#### Route Override

Kobweb Markdown front matter supports a `routeOverride` key. If present, its value will be passed into the
generated `@Page` annotation (see the [Route Override section▲](#route-override) for valid values here).

This allows you to give your URL a name that normal Kotlin filename rules don't allow for, such as a hyphen:

`# AStarDemo.md`

```markdown
---
routeOverride: a*-demo
---
```

The above will generate code like the following:

```kotlin
@Composable
@Page("a*-demo")
fun AStarDemoPage() { /* ... */
}
```

### Kobweb Call

The power of Kotlin + Compose HTML is interactive components, not static text! Therefore, Kobweb Markdown support
enables special syntax that can be used to insert Kotlin code.

#### Block syntax

Usually, you will define widgets that belong in their own section. Just use three triple-curly braces to insert a
function that lives in its own block:

```markdown
# Kobweb Tutorial

...

{{{ .components.widgets.VisitorCounter }}}
```

which will generate code for you like the following:

```kotlin
@Composable
@Page
fun KobwebPage() {
  /* ... */
  com.mysite.components.widgets.VisitorCounter()
}
```

You may have noticed that the code path in the markdown file is prefixed with a `.`. When you do that, the final path
will automatically be prepended with your site's full package.

#### Inline syntax

Occasionally, you may want to insert a smaller widget into the flow of a single sentence. For this case, use the
`${...}` inline syntax:

```markdown
Press ${.components.widgets.ColorButton} to toggle the site's current color.
```

> [!CAUTION]
> Spaces are not allowed within the curly braces! If you have them there, Markdown skips over the whole thing and leaves
> it as text.

### Imports

You may wish to add imports to the code generated from your markdown. Kobweb Markdown supports registering both
*global* imports (imports that will be added to every generated file) and *local* imports (those that will only apply
to a single target file).

#### Global Imports

To register a global import, you configure the `markdown` block in your build script:

```kotlin
// site/build.gradle.kts

kobweb {
  markdown {
    imports.add(".components.widgets.*")
  }
}
```

Notice that you can begin your path with a "." to tell the Kobweb Markdown plugin to prepend your site's package to it.
The above would ensure that every markdown file generated would have the following import:

```kotlin
import com.mysite.components.widgets.*
```

Imports can help you simplify your Kobweb calls. Revisiting an example from just above:

```markdown
# Without imports

Press ${.components.widgets.ColorButton} to toggle the site's current color.

# With imports

Press ${ColorButton} to toggle the site's current color.
```

#### Local Imports

Local imports are specified in your markdown's front matter (and can even affect its root declaration!):

```markdown
---
root: DocsLayout
imports:
  - .components.sections.DocsLayout
  - .components.widgets.VisitorCounter
---

...

{{{ VisitorCounter }}}
```

### Callouts

Kobweb Markdown supports callouts, which are a way to highlight pieces of information in your document. For example, you
can use them to highlight notes, tips, warnings, or important messages.

To use a callout, set the first line of some blockquoted text to `[!TYPE]`, where *TYPE* is one of the following:

* CAUTION - Calls attention to something that the user should be extra careful about.
* IMPORTANT - Important context that the user should be aware of.
* NOTE - Neutral information that the user should notice, even when skimming.
* QUESTION - A question posed whose answer is left as an exercise to the reader.
* QUOTE - A direct quote.
* TIP - Advice that the user may find useful.
* WARNING - Information that a user should be aware of to prevent errors.

```markdown
> [!NOTE]
> Lorem ipsum...

> [!QUOTE]
> Lorem ipsum...
```

![All markdown callouts](https://github.com/varabyte/media/raw/main/kobweb/images/widgets/callouts.png)

If you'd like to change the value of the default title that shows up, you can specify it in quotes:

```markdown
> [!QUESTION "Something to ponder..."]
```

As another example, when using quotes, you can set this to the empty string, which looks clean:

```markdown
> [!QUOTE ""]
> ...
```

![Markdown quote callout](https://github.com/varabyte/media/raw/main/kobweb/images/widgets/callout-quote.png)

If you want to specify a label that should apply globally, you can do so by overriding the blockquote handler in your
project's build script, using the convenience method `SilkCalloutBlockquoteHandler` for it:

```kotlin
kobweb {
  markdown {
    handlers.blockquote.set(SilkCalloutBlockquoteHandler(labels = mapOf("QUOTE" to "")))
  }
}
```

> [!CAUTION]
> Callouts are provided by Silk. If your project does not use Silk and you override the blockquote handler like this,
> it will generate code that will cause a compile error.

#### Callout variants

Silk provides a handful of variants for callouts.

For example, an outlined variant:

![Markdown outlined callout](https://github.com/varabyte/media/raw/main/kobweb/images/widgets/callout-outlined.png)

and a filled variant:

![Markdown filled callout](https://github.com/varabyte/media/raw/main/kobweb/images/widgets/callout-filled.png)

You can also combine any of the standard variants with an additional matching link variant (e.g.
`LeftBorderedCalloutVariant.then(MatchingLinkCalloutVariant))`) to make it so that any hyperlinks inside the callout
will match the color of the callout itself:

![Markdown matching link callouts](https://github.com/varabyte/media/raw/main/kobweb/images/widgets/callouts-matching-link.png)

If you prefer any of these styles over the default, you can set the `variant` parameter in the
`SilkCalloutBlockquoteHandler`, for example here we set it to the outlined variant:

```kotlin
kobweb {
  markdown {
    handlers.blockquote.set(SilkCalloutBlockquoteHandler(
      variant = "com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant")
    )
  }
}
```

Of course, you can also define your own variant in your own codebase and pass that in here as well.

#### Custom callouts

If you'd like to register a custom callout, this is done in two parts.

First, declare your custom callout setup in your code somewhere:

```kotlin
package com.mysite.components.widgets.callouts

val CustomCallout = CalloutType(
    /* ... specify icon, label, and colors here ... */
)
```

and then register it in your build script, extending the default list of handlers (i.e. `SilkCalloutTypes`) with your
custom one:

```kotlin
kobweb {
  markdown {
    handlers.blockquote.set(
      SilkCalloutBlockquoteHandler(types =
        SilkCalloutTypes +
          mapOf("CUSTOM" to ".components.widgets.callouts.CustomCallout")
      )
    )
  }
}
```

> [!NOTE]
> As seen above, by using a leading `.`, you can omit your project's group (e.g. `com.mysite`). Kobweb will
> automatically prepend it for you.

That's it! At this point, you can use it in your markdown:

```markdown
> [!CUSTOM]
> Neat.
```

### Iterating over all markdown files

It can be really useful to process all markdown files during your site's build. A common example is to collect all
markdown articles and generate a listing page from them.

You can actually do this using pure Gradle code, but it's common enough that Kobweb provides a convenience API, via the
`markdown` block's `process` callback.

You can register a callback that will be triggered at build time with a list of all markdown files in your project.

```kotlin
kobweb {
  markdown {
    process.set { markdownEntries ->
      // `markdownEntries` is type `List<MarkdownEntry>`, where an entry includes the file's path, the route it will
      // be served at, and any parsed front matter.

      println("Processing markdown files:")
      markdownEntries.forEach { entry ->
        println("\t* ${entry.filePath} -> ${entry.route}")
      }
    }
  }
}
```

Inside the callback, you can also call `generateKotlin` and `generateMarkdown` utility methods. Here is a very rough
example of creating a listing page for all blog posts in a site (found under the `resources/markdown/blog` folder):

```kotlin
kobweb {
  markdown {
    process.set { markdownEntries ->
      generateMarkdown("blog/index.md", buildString {
        appendLine("# Blog Index")
        markdownEntries.forEach { entry ->
          if (entry.filePath.startsWith("blog/")) {
            val title = entry.frontMatter["title"] ?: "Untitled"
            appendLine("* [$title](${entry.route})")
          }
        }
      })
    }
  }
}
```

Refer to the build script of [this site](https://github.com/varabyte/kobweb-site/blob/main/site/build.gradle.kts)
and search for "process.set" to see this feature in action in a production environment.

---
follows: BasePath
imports:
  - com.varabyte.kobweb.silk.components.display.LeftBorderedCalloutVariant
  - com.varabyte.kobweb.silk.components.display.LeftBorderedFilledCalloutVariant
  - com.varabyte.kobweb.silk.components.display.MatchingLinkCalloutVariant
  - com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant
---

If you create a Markdown file under the `jsMain/resources/markdown` folder, a corresponding page will be created for you
at build time, using the filename as its path.

For example, if I create the following file:

```markdown
// jsMain/resources/markdown/docs/tutorial/Kobweb.md

# Kobweb Tutorial

...
```

this will create a page that I can then visit by going to `mysite.com/docs/tutorial/kobweb`

## Front Matter

Front Matter is metadata that you can specify at the beginning of your document, like so:

```text
---
title: Tutorial
author: bitspittle
---

...
```

You can then query these key / value pairs using the page's ${DocsLink("PageContext", "routing#page-context")}:

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
> `com.varabyte.kobwebx:kobwebx-markdown` artifact in your project's build script.

### Root

Within your front matter, there's a special value which, if set, will be used to render a root `@Composable` that wraps
the rest of your Markdown code as its content. This is useful for specifying a layout for example:

```text
---
root: .components.layout.DocsLayout
---

# Kobweb Tutorial
```

The above will generate code like the following:

```kotlin
@Composable
@Page
fun KobwebPage() {
    com.mysite.components.layout.DocsLayout {
    H1 {
      Text("Kobweb Tutorial")
    }
  }
}
```

> [!NOTE]
> You may have noticed that the code path above is prefixed with a `.` (here, `.components.layouts.DocsLayout`).
> Whenever you do that in Kobweb Markdown, the framework will detect it and convert it to your site's full package.

If you have a default root that you'd like to use in most / all of your Markdown files, you can specify it in the
`markdown` block in your build script:

```kotlin
// site/build.gradle.kts

kobweb {
  markdown {
    defaultRoot.set(".components.layout.MarkdownLayout")
  }
}
```

### Route Override

Kobweb Markdown front matter supports a `routeOverride` key. If present, its value will be passed into the
generated `@Page` annotation ${DocsAside("Route override", "routing#route-override")}.

This allows you to give your URL a name that normal Kotlin filename rules don't allow for, such as a hyphen:

`# AStarDemo.md`

```text
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

## Kobweb Call

The power of Kotlin + Compose HTML is interactive components, not static text! Therefore, Kobweb Markdown support
enables special syntax that can be used to insert live Kotlin code into your page.

### Block syntax

Usually, you will define widgets that stand alone, without text or other components crowding around them. For this case,
use three triple-curly braces (this of this like Markdown's triple \`\`\` tick syntax, but for code):

```markdown
# Kobweb Tutorial

...

{{{ .components.widgets.VisitorCounter }}}
```

This will generate code for you like the following:

```kotlin
@Composable
@Page
fun KobwebPage() {
  /* ... */
  com.mysite.components.widgets.VisitorCounter()
}
```

### Inline syntax

Occasionally, you may want to insert a smaller widget into the flow of a single sentence. For this case, use the
`${...}` inline syntax:

```markdown
Press ${.components.widgets.ColorButton} to toggle the site's current color.
```

> [!CAUTION]
> Spaces are not allowed within the curly braces! If you have them there, Markdown skips over the whole thing and leaves
> it as text.

## Imports

You may wish to add imports to the code generated from your Markdown. Kobweb Markdown supports registering both
*global* imports (imports that will be added to every generated file) and *local* imports (those that will only apply
to a single target file).

### Global Imports

To register a global import, configure the `markdown` block in your build script:

```kotlin
// site/build.gradle.kts

kobweb {
  markdown {
    imports.add(".components.widgets.*")
  }
}
```

The above would ensure that every Markdown file generated would have the following import:

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

### Local Imports

Local imports are specified in your Markdown's front matter (and can even be used by the root declaration!):

```text
---
root: DocsLayout
imports:
  - .components.layouts.DocsLayout
  - .components.widgets.VisitorCounter
---

...

{{{ VisitorCounter }}}
```

## Callouts

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

> [!NOTE]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!QUOTE]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!TIP]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!IMPORTANT]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!QUESTION]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!CAUTION]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

> [!WARNING]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

If you'd like to change the value of the default title that shows up, you can specify it in quotes:

```markdown
> [!QUESTION "Did you know..."]
```

> [!QUESTION "Did you know..."]
> *Interesting fact here!*

As another example, when using quotes, you can set the label to the empty string:

```markdown
> [!QUOTE ""]
> ...
```

which looks clean:

> [!QUOTE ""]
> The trouble with quotes on the internet is you never know if they are genuine.
>
> — Abraham Lincoln

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

### Callout variants

Silk provides a handful of variants for callouts.

For example, an outlined variant:

> [!NOTE {variant = OutlinedCalloutVariant}]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

and a filled variant:

> [!NOTE {variant = LeftBorderedFilledCalloutVariant}]
> Lorem ipsum dolor sit amet, consectetur adipiscing elit.

You can also combine any of the standard variants with an additional matching link variant (e.g.
`LeftBorderedCalloutVariant.then(MatchingLinkCalloutVariant))`) to make it so that any hyperlinks inside the callout
will match the color of the callout itself:

> [!TIP {variant = LeftBorderedCalloutVariant.then(MatchingLinkCalloutVariant)}]
> A simple callout with [an example link](https://example.com) in the callout body.

> [!TIP {variant = LeftBorderedFilledCalloutVariant.then(MatchingLinkCalloutVariant)}]
> A simple callout with [an example link](https://example.com) in the callout body.

> [!TIP {variant = OutlinedCalloutVariant.then(MatchingLinkCalloutVariant)}]
> A simple callout with [an example link](https://example.com) in the callout body.

If you prefer any of these styles over the default, you can set the `variant` parameter in the
`SilkCalloutBlockquoteHandler`. For example, here we set it to the outlined variant:

```kotlin
kobweb {
  markdown {
    handlers.blockquote.set(SilkCalloutBlockquoteHandler(
      variant = "com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant")
    )
  }
}
```

You can also specify the variant from within the Markdown syntax, passing it in as a parameter using a curly brace
syntax:

```markdown
> [!NOTE {variant = com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant}]
```

Of course, you can also define your own variant in your own codebase and use that here as well.

### Custom callouts

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

That's it! At this point, you can use it in your Markdown:

```markdown
> [!CUSTOM]
> Neat.
```

## Iterating over all markdown files

It can be really useful to process all Markdown files when your site is being built. A common example is to collect all
Markdown articles and generate a listing page from them.

You can actually do this using pure Gradle code, but it is common enough that Kobweb provides a convenience API, via the
`markdown` block's `process` callback.

You can register a callback that will be triggered at build time with a list of all Markdown files in your project.

```kotlin
kobweb {
  markdown {
    process.set { markdownEntries ->
      // `markdownEntries` is type `List<MarkdownEntry>`,
      // where an entry includes the file's path, the route
      // it will be served at, and any parsed front matter.

      println("Processing markdown files:")
      markdownEntries.forEach { entry ->
        println("\t* ${entry.filePath} -> ${entry.route}")
      }
    }
  }
}
```

Inside the callback, you can also call `generateKotlin` and `generateMarkdown` methods, to easily create files that
will be included in your final site.

Here is a very rough example of creating a listing page for all blog posts in a site (found under the
`resources/markdown/blog` folder):

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
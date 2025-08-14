---
description: How to specify metadata for your page.
---

Have you ever pasted a link to a page into some program which, after a brief fetch, ended up surfacing information about
that page in an infobox? This is possible because every HTML page supports having a `<head>` block, which is where you
can specify metadata about it.

However, Kobweb generates the HTML for your site automatically! So how can you specify this metadata?

There are two ways: via the build script (for metadata that should be present on every page), and through code using the
Kotlin/JS APIs. Most projects will use both approaches, so we'll explore both in this article.

## Site-wide metadata

For any build script that applies the Kobweb Application or Library Gradle plugins, you are given access to the `index`
block, which will either be found under the `kobweb.app` or `kobweb.library` blocks, respectively.

The `index` block then contains a `head` property, which is where you can specify the metadata for your page, using
the [Kotlinx.html DSL](https://github.com/Kotlin/kotlinx.html).

Let's demonstrate this with a concrete example. Say you ultimately want to generate an HTML page with a `<head>` block
that looks like the following:

```html
<head>
  <title>My Portfolio</title>
  <meta name="description" content="A portfolio site listing my resume, skills, and experiences." />
  <!-- Install instructions for Google Roboto font -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/..." rel="stylesheet">
</head>
```

In your build script:

```kotlin "site/build.gradle.kts"
plugins {
    alias(libs.plugins.kobweb.application)
}

kobweb {
    app {
        index {
            description.set("A portfolio site listing my resume, skills, and experiences.")
            head.add {
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "preconnect", href = "https://fonts.gstatic.com") {
                    attributes["crossorigin"] = ""
                }
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/...")
            }
        }
    }
}
```

> [!NOTE]
> For build scripts that apply the Kobweb application plugin, Kobweb provides the `description` property as a
> convenience, but it is ultimately just shorthand for adding a `<meta>` tag with the appropriate attributes -- here,
> `<meta name="description" content="..." />`.
>
> It is provided because it is so commonly used and every site should probably have one. We also want to encourage users
> setting their description inside the application module and not in some library module, as users who consume it might
> not expect that.
>
> In a similar fashion, Kobweb application build scripts also will add an `icon` link into the `<head>` block by default
> as well, which you can control using the `faviconPath` property (which defaults to `"/favicon.ico"`).

Our `<head>` block is most of the way there, but we're still missing the title! This is because the title is actually
specified inside the site's `.kobweb/conf.yaml` file. Here, it should look like:

```yaml ".kobweb/conf.yaml"
site:
  title: My Portfolio
```

And with that, all Kobweb pages for this site will now include the `<head>` block that we wanted.

### URL interception

Many popular web services are provided as bundles of resources (scripts, stylesheets, and data) which, together, provide
functionality and/or style. These often get hosted on content delivery networks (commonly abbreviated as CDNs).
Installation instructions often recommend using them as an easy option -- just add a script tag and stylesheet link to
your page's `<head>` block, and you're good to go!

Unfortunately, this easy solution may result in a site not being GDPR-compliant. In short, this is because requests made
to the CDN involve sharing the user's IP address as part of the transaction, and GDPR does not allow sharing personal
data without user consent with servers that don't follow European data protection laws.

> [!NOTE]
> Further discussion about GDPR is far outside the scope of this documentation, but if you're interested, you may want
> to review links like https://en.wikipedia.org/wiki/Content_delivery_network#Security_and_privacy and find blog posts
> discussing the topic in more detail.

A common way to avoid issues with GDPR compliance is by self-hosting resources yourself. Kobweb aims to make this as
easy as possible through URL interception, which you can configure in the `index` block.

#### Automatic self-hosting

The easiest approach is to just let Kobweb handle self-hosting itself, by opting into it:

```kotlin "site/build.gradle.kts"
kobweb {
    app {
        index {
            interceptUrls {
                enableSelfHosting()
            }
        }
    }
}
```

Let's use a real-example to demonstrate how this works. Suppose you want to use the popular
[glMatrix library](https://glmatrix.net/) for adding OpenGL support to your site (as `kobweb create examples/opengl`
does). After searching, you find it is hosted on Cloudflare:

```kotlin "site/build.gradle.kts"
kobweb {
    app {
        index {
            head.add {
                script {
                    src = "https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js"
                }
            }
        }
    }
}
```

If you navigate to your site and open up the network tab in your browser's dev tools, you'll see that it makes a request
to the expected URL.

However, if you opt in for self-hosting:

```kotlin "site/build.gradle.kts"
kobweb {
    app {
        index {
            head.add {
                script {
                    src = "https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js"
                }
            }
            interceptUrls { enableSelfHosting() }
        }
    }
}
```

and re-run your site, then you'll see that a copy of `gl-matrix-min.js` was downloaded locally at build time. If you
check the network tab now, you'll see that the file is hosted on your own server, at a URL like
`https://yoursite.com/_kobweb/self-host/cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js`.

A really nice feature of self-hosting is that it will inspect any target resource that is a CSS file (i.e. a stylesheet)
and recursively download any additional resources it references. Otherwise, if you had only downloaded such a stylesheet
yourself, you might think you were done without realizing that you were still breaking GDPR compliance as additional
requests were being made to fetch additional resources.

> [!CAUTION]
> In general, CDN links offer quite a few advantages, so you shouldn't replace them just because it is easy to do so --
> you'll increase your bandwidth bill, while at the same time likely serve those files much slower to your users than a
> CDN would have. You should carefully consider your situation and the tradeoffs before committing to self-hosting.

#### Manually intercepting URLs

If you want more control over which URLs get intercepted, you can specify your own `replace` rules in the URL
interception block.

Let's say instead of you specifying the glMatrix dependency yourself, it was done by a third-party Kobweb library. When
you go to build your site, Kobweb's Gradle output will inform you that the Cloudflare `gl-matrix-min.js` URL will be
added to your final page (or you might notice this after opening your page and inspecting its `<head>` block).

While you could just use `enableSelfHosting()` here, let's show how you could do a manual interception:

```kotlin "site/build.gradle.kts"
kobweb {
    app {
        index {
            interceptUrls {
                replace(
                    "https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js",
                    "/assets/scripts/gl-matrix-min.js"
                )
            }
        }
    }
}
```

At this point, you'd be on the hook for downloading the script file yourself and placing it in the correct location
(here, `site/src/jsMain/resources/assets/scripts/gl-matrix-min.js`). But after that, you'd be self-hosting again!

Checking the network tab again, you should see that `gl-matrix-min.js` is now being served from your domain and not from
a CDN.

## Page-specific metadata

Often, you will want to set metadata dynamically on a per-page basis.

For example, maybe you don't want the title of your site to be shown for every page, but instead you want to override it
with the page's title (especially as this is the name that appears in the browser tab). And while we're at it, let's
update the description as well, since the two should definitely be kept in sync.

To accomplish this, we'll use the JavaScript standard library, which provides functionality for reaching into the DOM
and modifying it. Using Kotlin/JS, you can write:

```kotlin
import kotlinx.browser.document

private fun Document.setPageMetadata(title: String, description: String) {
    title = title
    head!!
        .querySelector("meta[name='description']")!!
        .setAttribute("content", description)
}

// Later in the file...
document.setPageMetadata(
    "Understanding Metadata",
    "This is a blog post about understanding HTML metadata."
)
```

The above code is actually making a strong assumption that a `<meta>` tag with the name `description` already exists in
your page's `<head>` block (notice the `!!` operator after the `querySelector` call).

However, if you're potentially working with a head element that might not always exist, or if you want to write the
above code a bit more defensively, you can use a "query or create" pattern:

```kotlin
private fun Document.setDescription(description: String) {
    val head = document.head!!
    (head.querySelector("meta[name='description']") ?:
        document.createElement("meta").apply {
            setAttribute("name", "description")
            head.appendChild(this)
        }
    ).setAttribute("content", description)
}
```

A very natural place to put code like this is inside a layout composable, often inside a `LaunchedEffect`:

```kotlin
@Layout
@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    // Get title and description out of ctx.data
    LaunchedEffect(title, description) {
        document.setPageMetadata(title, description)
    }
}
```

> [!NOTE]
> You can find us using this
> pattern [in the Kobweb site itself](https://github.com/varabyte/kobweb-site/blob/f628baef2041379746b5b1347fe422ad1d1ec37f/site/src/jsMain/kotlin/com/varabyte/kobweb/site/components/layouts/PageLayout.kt#L39).

And that's really all there is to it. You may wish to familiarize yourself with
the [ `querySelector` method](https://developer.mozilla.org/en-US/docs/Web/API/Document/querySelector) and
the [`Document` interface](https://developer.mozilla.org/en-US/docs/Web/API/Document) exposed by Kotlin/JS.
---
description: How to specify metadata for your page.
follows: PersistingState
---

Certainly you've linked to a page in some program which, after a brief fetch, ended up surfacing information about that
page. This is possible because every HTML page has a `<head>` block, which is where you can specify metadata for it.

However, Kobweb generates the HTML for your site automatically - you don't write it yourself! So how can you specify
this metadata?

There are two ways: via the build script (for metadata that should be present on every page), and through code using the
Kotlin/JS APIs. Most projects will use both, and we'll explore both approaches in this article.

## The build script Kobweb index block

For your build script that applies the Kobweb Application or Library Gradle plugins, you are given access to the `index`
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

```kotlin
plugins {
    alias(libs.plugins.kobweb.application)
}

kobweb {
    app {
        index {
            head.addAll {
                description.set("A portfolio site listing my resume, skills, and experiences.")
                head.add {
                    title("My Portfolio")
                    link(rel = "preconnect", href = "https://fonts.googleapis.com")
                    link(rel = "preconnect", href = "https://fonts.gstatic.com") {
                        attributes["crossorigin"] = ""
                    }
                    link(rel = "stylesheet", href = "https://fonts.googleapis.com/...")
                }
            }
        }
    }
}
```

And with that, Kobweb will now include those elements in the `<head>` block of every page it generates.

The build script's head block is a great place to declare your site's default title and description, as well as links
to external resources like fonts, stylesheets, and scripts.

### URL interception

Many popular web products and services are serve as bundles of resources which, together, provide functionality and/or
style. These often get hosted on content delivery networks (commonly abbreviated as CDNs). Installation instructions
often recommend them as an option -- just add a script tag and stylesheet link to your page's `<head>` block, and you're
good to go!

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

The easiest approach is to just let Kobweb handle it by opting into self-hosting:

```kotlin
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

```kotlin
kobweb {
    app {
        index {
            script {
                src = "https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js"
            }
        }
    }
}
```

If you navigate to your site and open up the network tab in your browser's dev tools, you'll see that it makes a request
to the expected URL.

However, if you opt in for self-hosting:

```kotlin
kobweb {
    app {
        index {
            script {
                src = "https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js"
            }
            interceptUrls { enableSelfHosting() }
        }
    }
}
```

then a copy of `gl-matrix-min.js` was downloaded locally at build time. If you check the network tab now, you'll see
that the file is hosted on your own server, at a URL like
`https://yoursite.com/_kobweb/self-host/cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js`.

A really nice feature of self-hosting is that it will inspect the target resource (if it is a CSS file) and recursively
download any additional resources it references. Otherwise, if you had only downloaded such a CSS file yourself, you
might think you were done without realizing that you were still hitting GDPR as additional requests were being made to
fetch additional resources.

> [!CAUTION]
> In general, CDN links offer quite a few advantages, so you shouldn't replace them just because it is easy to do so --
> you'll increase your bandwidth bill, while at the same time likely serve those files much slower to your users than a
> CDN would have. You should carefully consider your situation and the tradeoffs before committing to self-hosting.

#### Manually intercepting URLs

If you want more control over which URLs get intercepted, you can specify your own `replace` rules in the URL
interception block.

Let's say instead of you specifying the glMatrix dependency yourself, it was done by a third-party Kobweb library. When
you go to build your site, Kobweb's Gradle output will inform you that the `gl-matrix-min.js` URL will be added to your
final page.

While you could just use `enableSelfHosting()` here, let's show how you could do a manual interception:

```kotlin
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

Checking the network tab again, you should see that `gl-matrix-min.js` is now being served from a URL like
`https://yoursite.com/assets/scripts/gl-matrix-min.js`.

## Modifying the head block in code

...
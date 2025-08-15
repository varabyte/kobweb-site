---
description: A guide for how to use Kobweb if you already have your own existing backend.
title: Using a Custom Backend
---

You may already have an existing and complex backend, perhaps written with Ktor or Spring Boot, and, if so, are
wondering if you can integrate Kobweb with it.

## Serving static files

The recommended solution for now is to export your site using a static layout
${DocsAside("Static layout vs. Full stack sites", "/docs/concepts/foundation/exporting#static-layout-vs-full-stack-sites")}
and then add code to your backend to serve the files yourself, as it is fairly trivial.

When you export a site statically, it will generate all files into your `.kobweb/site` folder. Then, if using Ktor, for
example, you can serve these files using their [`staticFiles` method](https://ktor.io/docs/server-static-content.html):

```kotlin
routing {
    staticFiles("/", File(".kobweb/site")) {
        enableAutoHeadResponse()
        extensions("html")
        default("index.html")
    }
}
```

> [!IMPORTANT]
> Adding `extensions("html")` is required to ensure the web server serves `index.html` when accessing a URL without an
> `.html` suffix. For example, `/about` should serve `/about.html` and not a 404. In other words, this supports a
> clean URL look.
> 
> Adding `enableAutoHeadResponse` is optional but is generally a good practice, as it will allow users to confirm the
> existence of various pages without having to pull down their entire contents.

> [!NOTE]
> In the above example, we specify `"index.html"` as a fallback file if no match is found. What this will do is serve
> your root Kobweb page. This may seem incorrect at first glance, but, after getting fetched, page logic will run and
> recognize that the route in the URL bar doesn't match any known Kobweb route. Kobweb will then clear the page contents
> and replace them with an ${DocsLink("error page", "/docs/concepts/routing#custom-error-page")}.

> [!TIP]
> You can optionally create an empty, top-page composable annotated inside `_404.kt` and use `default("404.html")`
> instead. This will work the same way as `index.html` does above, triggering Kobweb's error page logic, but
> `404.html`'s exported size will be smaller since you're not snapshotting page content you don't need. As a bonus, this
> approach is also compatible with GitHub pages.
> 
> ```kotlin "jsMain/kotlin/com/mysite/pages/_404.kt
> @Composable
> @Page
> fun ErrorPage() {
>    // Intentionally left empty for minimal export size. This will get
>    // served when Kobweb detects a missing route, and then the page
>    // logic will run, automatically replacing the current screen with
>    // the site's actual error page.
> }
> ```

## Querying API endpoints

Although Kobweb provides its own opinionated way to define and access API endpoints, of course you can query raw HTTP
endpoints exposed by your backend. You can
use [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch) directly, or you can use the
convenience `http` property that Kobweb adds to the `window` object which exposes all the HTTP methods (`get`, `post`,
`put`, etc.):

```kotlin
@Page
@Composable
fun CustomBackendDemoPage() {
  LaunchedEffect(Unit) {
    val endpointResponse = window.http.get("/my/endpoint?id=123").decodeToString()
    /* ... */
  }
}
```

Unfortunately, using your own backend does mean you're opting out of using Kobweb's full stack solution, which means you
won't have access to Kobweb's API routes, API streams, or live reloading support. This is a situation we'd like to
improve someday ([link to tracking issue](https://github.com/varabyte/kobweb/issues/22)), but we don't have enough
resources to be able to prioritize resolving this for a 1.0 release.
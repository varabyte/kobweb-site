---
description: A guide for how to use Kobweb if you already have your own existing backend.
title: Using a Custom Backend
follows: ExistingProject
---

You may already have an existing and complex backend, perhaps written with Ktor or Spring Boot, and, if so, are
wondering if you can integrate Kobweb with it.

The recommended solution for now is to export your site using a static layout
${DocsAside("Static layout vs. Full stack sites", "/docs/concepts/foundation/exporting#static-layout-vs-full-stack-sites")}
and then add code to your backend to serve the files yourself, as it is fairly trivial.

When you export a site statically, it will generate all files into your `.kobweb/site` folder. Then, if using Ktor, for
example, you can serve these files using their [`staticFiles` method](https://ktor.io/docs/server-static-content.html):

```kotlin
routing {
    staticFiles("/", File(".kobweb/site")) {
        // Support clean URLs (ktor auto-appends ".html")
        extensions("html")
        // Fallback if a file was not found.
        default("index.html")
    }
}
```

When using Ktor,
adding `html` to the extensions (as shown above)
is required to ensure the web server serves `index.html` when accessing a URL
without the suffix `.html` (e.g., `/docs` instead of `/docs.html`) and avoid 404.
For more information,
refer to the [Ktor documentation on file extension fallbacks](https://ktor.io/docs/server-static-content.html#extensions).


If you need to access HTTP endpoints exposed by your backend, you can use [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch)
directly, or you can use the convenience `http` property that Kobweb adds to the `window` object which exposes
all the HTTP methods (`get`, `post`, `put`, etc.):

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
---
follows: Cors
---

You may already have an existing and complex backend, perhaps written with Ktor or Spring Boot, and, if so, are
wondering if you can integrate Kobweb with it.

The recommended solution for now is to export your site using a static layout
([read more about static layout sites here▲](#static-layout-vs-full-stack-sites)) and then add code to your backend to
serve the files yourself, as it is fairly trivial.

When you export a site statically, it will generate all files into your `.kobweb/site` folder. Then, if using Ktor, for
example, serving these files is a one-liner:

```kotlin
routing {
    staticFiles("/", File(".kobweb/site"))
}
```

If using Ktor, you should also install
the [`IgnoreTrailingSlash` plugin](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.routing/-ignore-trailing-slash.html)
so that your web server will serve `index.html` when a user visits a directory (e.g. `/docs/`) instead of returning a 404:

```kotlin
embeddedServer(...) { // `this` is `Application` in this scope
  this.install(IgnoreTrailingSlash)
  // Remaining configuration
}
```

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


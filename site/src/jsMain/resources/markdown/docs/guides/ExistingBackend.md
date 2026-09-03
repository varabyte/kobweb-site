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
> and replace them with an ${DocsLink("error page", "/docs/concepts/foundation/routing#custom-error-page")}.

## Querying API endpoints

Although Kobweb provides its own opinionated way to define and access API endpoints, you can also query raw HTTP
endpoints exposed by your backend. For example, let's say you have a custom Ktor server where you exposed
`"/my/endpoint"` for querying data.

You can use [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch) directly, or you can use the
convenience `http` property that Kobweb adds to the `window` object which exposes all the HTTP methods (`get`, `post`,
`put`, etc.):

```kotlin
@Page
@Composable
fun CustomBackendDemoPage() {
  LaunchedEffect(Unit) {
    val endpointResponse = window.http.get("/my/endpoint?id=123")
        .bodyAsBytes()
        .decodeToString()
    /* ... */
  }
}
```

> [!NOTE]
> `window.http` and `window.api` are both nearly identical in functionality, but `window.api` adds a bit of extra logic
> to prefix your route with `"api/"`, essentially, respecting Kobweb's convention. But if you are using your own custom
> backend, we expect you will likely come up with your own approach.

## Limitations

Unfortunately, using your own backend does mean you're opting out of using Kobweb's full stack solution, which means you
won't have access to Kobweb's API routes, API streams, or live reloading support. This is a situation we'd like to
improve someday ([link to tracking issue](https://github.com/varabyte/kobweb/issues/22)), but we don't have enough
resources to be able to prioritize resolving this for a 1.0 release.

## Example configs

Some of our users have shared their custom hosting configurations with us. If you have your own that you would like to
see added here, consider ${DocsLink("reaching out to us", "../community/connecting-with-us")} to let us know more about
it (or [edit the page and file a PR](https://github.com/varabyte/kobweb-site/edit/main/site/src/jsMain/resources/markdown/docs/guides/ExistingBackend.md)).

### Apache

``` ".htaccess"
RewriteEngine on

RewriteCond %{THE_REQUEST} /([^.]+)\.html [NC]
RewriteRule ^ /%1 [NC,L,R]

RewriteCond %{REQUEST_FILENAME}.html -f
RewriteRule ^ %{REQUEST_URI}.html [NC,L]
```

### Caddy

``` "Caddyfile"
example.com {
  root * /var/www/htdocs/
  file_server
  encode gzip
  try_files {path}.html {path}
}
```

### Nginx

``` "nginx.conf"
location / {
    if ($request_uri ~ ^/(.*).html) {
        return 301 /$1$is_args$args;
    }
    try_files $uri $uri.html $uri/ =404;
}
```



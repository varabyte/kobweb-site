---
description: How to declare pages and their URL routes for your site.
follows: Index
---

## `@Page`

Creating a page is easy! It's just a normal `@Composable` method. To upgrade your composable to a page, all you need to
do is:

1. Define your composable in a file somewhere under the `pages` package in your `jsMain` source directory.
1. Annotate it with `@Page`

Just from that, Kobweb will create a site entry for you automatically.

For example, if I create the following file:

```kotlin
// jsMain/kotlin/com/mysite/pages/admin/Settings.kt

@Page
@Composable
fun SettingsPage() {
    /* ... */
}
```

this will create a page that I can then visit by going to `mysite.com/admin/settings`.

> [!IMPORTANT]
> The last part of a URL, here `settings`, is called a *slug*.

By default, the slug comes from the file name, which is converted into kebab-case. For example, the file name
`AboutUs.kt` would transform into `about-us`. However, this can be overridden on a case-by-case basis to whatever you
want (more on that shortly).

In other words, the name of your method is not used when generating the URL. You can call it whatever you want, but as
a convention, we suggest using a name that matches the name of the file, appended by the `Page` suffix.

The file name `Index.kt` is special. If a page is defined inside such a file, it will be treated as the default page
under that URL. For example, a page defined in `.../pages/admin/Index.kt` will be visited if the user visits
`mysite.com/admin/`.

## Route Override

If you ever need to change the route generated for a page, you can set the `Page` annotation's `routeOverride` field:

```kotlin
// jsMain/kotlin/com/mysite/pages/admin/Settings.kt

@Page(routeOverride = "config")
@Composable
fun SettingsPage() {
    /* ... */
}
```

The above would create a page you could visit by going to `mysite.com/admin/config`.

`routeOverride` can additionally contain slashes, and if the value begins and/or ends with a slash, that has a special
meaning.

* Begins with a slash - represent the whole route from the root
* Ends with a slash - a slug will still be generated from the filename and appended to the route override.

And if you set the override to "index", that behaves the same as setting the file to `Index.kt` as described above.

Some examples can clarify these rules (and how they behave when combined). Assuming we're defining a page for our site
`example.com` within the file `a/b/c/Slug.kt`:

| Annotation              | Resulting URL                   |
|-------------------------|---------------------------------|
| `@Page`                 | `example.com/a/b/c/slug`        |
| `@Page("other")`        | `example.com/a/b/c/other`       |
| `@Page("index")`        | `example.com/a/b/c/`            |
| `@Page("d/e/f/")`       | `example.com/a/b/c/d/e/f/slug`  |
| `@Page("d/e/f/other")`  | `example.com/a/b/c/d/e/f/other` |
| `@Page("d/e/f/index")`  | `example.com/a/b/c/d/e/f/`      |
| `@Page("/d/e/f/")`      | `example.com/d/e/f/slug`        |
| `@Page("/d/e/f/other")` | `example.com/d/e/f/other`       |
| `@Page("/d/e/f/index")` | `example.com/d/e/f/`            |
| `@Page("/")`            | `example.com/slug`              |
| `@Page("/other")`       | `example.com/other`             |
| `@Page("/index")`       | `example.com/`                  |

> [!CAUTION]
> Despite the flexibility allowed here, you should not be using this feature frequently, if at all. A Kobweb project
> benefits from the fact that a user can easily associate a URL on your site with a file in your codebase, but this
> feature allows you to break those assumptions. It is mainly provided to enable dynamic routing
> ${DocsAside("Dynamic routes", "#dynamic-routes")} or providing a valid URL name that uses characters which aren't
> allowed in Kotlin filenames.

## Package

While the slug is derived from the filename, earlier parts of the route, called URL route segments, are derived from the
file's package.

A package will be converted into a route segment by removing any leading or trailing underscores (as these are often
used to work around limitations into what values and keywords are allowed in a package name,
e.g. `site.pages.blog._2022` and `site.events.fun_`) and converting camelCase packages into hyphenated words
(so `site.pages.team.ourValues` generates the route `/team/our-values/`).

### `@PackageMapping`

If you'd like to override the route segment generated for a package, you can use the `PackageMapping` annotation.

For example, let's say your team prefers not to use camelCase packages for aesthetic reasons. Or perhaps you
intentionally want to add a leading underscore into your site's route segment for some emphasis (since earlier we
mentioned that leading underscores get removed automatically), such as in the route `/team/_internal/contact-numbers`.
You can use package mappings for this.

You apply the package mapping annotation to the current file. Using it looks like this:

```kotlin
// site/pages/team/values/PackageMapping.kt
@file:PackageMapping("our-values")

package site.pages.blog.values

import com.varabyte.kobweb.core.PackageMapping
```

With the above package mapping in place, a file that lives at `site/pages/team/values/Mission.kt` will be visitable at
`/team/our-values/mission`.

## Page context

Every page method provides access to its `PageContext` via the `rememberPageContext()` method.

Critically, a page's context provides it access to a router, allowing you to navigate to other pages.

It also provides dynamic information about the current page's URL (discussed in the next section).

```kotlin
@Page
@Composable
fun ExamplePage() {
    val ctx = rememberPageContext()
    Button(onClick = { ctx.router.navigateTo("/other/page") }) {
        Text("Click me")
    }
}
```

### Query parameters

You can use the page context to check the values of any query parameters passed into the current page's URL.

So if you visit `site.com/posts?id=12345&mode=edit`, you can query those values like so:

```kotlin
enum class Mode {
    EDIT, VIEW;

    companion object {
        fun from(value: String) {
           entries.find { it.name.equals(value, ignoreCase = true) }
               ?: error("Unknown mode: $value")
        }
    }
}

@Page
@Composable
fun Posts() {
    val ctx = rememberPageContext()
    // Here, I'm assuming these params are always present, but you can use
    // `get` instead of `getValue` to handle the nullable case. Care should
    // also be taken to parse invalid values without throwing an exception.
    val postId = ctx.route.params.getValue("id").toInt()
    val mode = Mode.from(ctx.route.params.getValue("mode"))
    /* ... */
}
```

## Dynamic routes

In addition to query parameters, Kobweb supports embedding arguments directly in the URL itself. For example, you might
want to register the path `users/{user}/posts/{post}` which would be visited if the site visitor typed in a URL like
`users/bitspittle/posts/20211231103156`.

How do we set it up? Thankfully, it's fairly easy.

But first, notice that in the example dynamic route `users/{user}/posts/{post}` there are actually two different dynamic
segments, one in the middle and one at the tail end. These can be handled by the `PackageMapping` and `Page`
annotations, respectively.

### `@PackageMapping`

Pay attention to the use of the curly braces in the mapping name! That lets Kobweb know that this is a dynamic package.

```kotlin
// pages/users/user/PackageMapping.kt
@file:PackageMapping("{user}") // or @file:PackageMapping("{}")

package site.pages.users.user

import com.varabyte.kobweb.core.PackageMapping
```

If you pass an empty `"{}"` into the `PackageMapping` annotation, it directs Kobweb to use the route segment name
normally generated from the package (i.e. `user` in this specific case).

### `@Page`

Like `PackageMapping`, the `Page` annotation can also take curly braces to indicate a dynamic value.

```kotlin
// pages/users/user/posts/Post.kt

@Page("{post}") // Or @Page("{}")
@Composable
fun PostPage() {
   /* ... */
}
```

An empty `"{}"` tells Kobweb to use the name of the slug normally generated from the file name (i.e. `post` in this
specific case).

Remember that the `Page` annotation allows you to rewrite the entire route. That value also accepts dynamic segments, so
you could even do something like:

```kotlin
// pages/users/user/posts/Post.kt

@Page("/users/{user}/posts/{post}") // Or @Page("/users/{user}/posts/{}")
@Composable
fun PostPage() {
    /* ... */
}
```

but with great power comes great responsibility. Tricks like this may be hard to find and/or update later, especially as
your project gets larger. While it works, you should only use this format in cases where you absolutely need to (perhaps
after a code refactor where you have to support legacy URL paths).

### Querying dynamic route values

You query dynamic route values exactly the same as if you were requesting query parameters. That is, use `ctx.params`:

```kotlin
@Page("{}")
@Composable
fun PostPage() {
    val ctx = rememberPageContext()
    val postId = ctx.route.params.getValue("post")
    /* ... */
}
```

> [!IMPORTANT]
> You should avoid creating URL paths where the dynamic path and the query parameters have the same name, as in
> `mysite.com/posts/{post}?post=...`, as this could be really tricky to debug in a complex project. If there is a
> conflict, then the dynamic route parameters will take precedence. (You can still access the query parameter value via
> `ctx.route.queryParams` in this case if necessary.)

### Catch-all dynamic routes

As seen above, dynamic routes so far capture a single part of the entire route, e.g. `"/users/{user}/profile"` capturing
`"bitspittle"` in the URL `"/users/bitspittle/profile"`.

Kobweb also supports catch-all dynamic routes, which capture the remainder of the URL, at which point the page can parse
it and handle it as it sees fit.

To create a catch-all route, prepend your dynamic route name with an ellipsis.

For example, the catch-all route `"/a/b/c/{...rest}"` would capture `"x/y/z"` in the URL `"/a/b/c/x/y/z"`.

In practice, it looks like this:

```kotlin
// pages/com/mysite/store/products/ProductDetails.kt

@Page("{...product-details}")
@Composable
fun ProductDetailsPage() {
    val ctx = rememberPageContext()
    val productDetails = remember(ctx.route.path) {
        ctx.route.params.getValue("product-details").split("/")
    }
    /* ... */
}
```

It's not expected that many sites will ever use a catch-all route, but in the above case, you could use the captured
value as a way to encode fluid details of a product, perhaps with sub-routes contextually depending on the product type.
For example, the above page could handle `/store/products/home-and-garden/hoses/19528`,
`/store/products/electronics/phones/google/pixel/4a`, and whatever other scheme each department demands.

Of course, it is better to provide a more structured solution if you can (e.g. declaring a page route like
`/store/products/{category}/{subcategory}/{product}`), but reality can be messy sometimes.

> [!IMPORTANT]
> Catch-all route segments MUST terminate the route. The following is not valid and will result in an exception being
> thrown: `"/a/b/c/{...middle}/x/y/z"`.

#### Optional catch-all routes

While `"a/b/c/{...rest}"` will match `"/a/b/c/d"`, it will NOT match `"/a/b/c/"`. If you want to additionally support
the empty case, you can add a question mark to the end of the name, e.g. `"/a/b/c/{...rest?}"`.

Using this feature, you could even discard Kobweb's routing logic entirely and handle everything yourself:

```kotlin
// com/mysite/pages/Index.kt

@Page("{...path?}")
@Composable
fun CatchAllPage() {
    val ctx = rememberPageContext()
    val url = ctx.route.params.getValue("path")
    /* ... */
}
```

### Static and dynamic siblings

If you have a site that registers both static and dynamic segments at the same level, then a static match will always
take precedence.

For example, if you register routes `/a` and `/{else}`, then visiting `/a` will trigger the `/a` handler, while visiting
anything else, such as `/b`, will trigger the `/{else}` handler.

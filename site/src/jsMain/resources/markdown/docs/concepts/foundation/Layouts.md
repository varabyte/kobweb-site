---
description: How to declare page layouts.
follows: ApplicationRoot
---

At this point in the documentation, we have discussed ${DocsLink("pages", "routing")} (the unique UI presented to the
user for a specific route) and ${DocsLink("the application root", "application-root")} (the
composable entry point for all pages).

There is one more useful layer in between: the page layout.

A page's layout owns the common UI and structure that lives across multiple pages on your site. For example, if you have
a nav header and footer in your site, they will be declared in your layout.

## `@Layout`

Layouts are composable methods which (optionally) take a single `PageContext` parameter and a (required) composable
content callback (i.e. `content: @Composable () -> Unit`). You must annotate them with the `@Layout` annotation so that
Kobweb can discover and register them:

```kotlin
// jsMain/kotlin/com/mysite/components/layouts/PageLayout.kt

@Layout
@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    /* ... */
    content()
}

// No page context is also OK:
// fun PageLayout(content: @Composable () -> Unit)
```

> [!NOTE]
> You can declare a layout anywhere, in any file. However, most Kobweb users will expect to find them under the
`components.layouts`
> package, so we recommend you put your own in there.

Once declared, you can direct a page to use the layout by adding the `@Layout` annotation there as well, this time
specifying a target path:

```kotlin
// jsMain/kotlin/com/mysite/pages/Index.kt

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    /* ... */
}
```
> [!NOTE]
> You may have noticed that the code path above is prefixed with a `.` (here, `.components.layouts.PageLayout`).
> Kobweb detects this and automatically resolves it to a qualified package (here,
> `com.mysite.components.layouts.PageLayout`).

At this point, when you visit the home page (from the above example), your site is composed as follows:

```kotlin
App {
    Layout {
        Page()
    }
}
```

When you navigate between pages that all use the same layout, many sections and widgets in the layout might not even
recompose, and any values you `remember` will survive across the pages.

If you explicitly want layout code that changes on new pages, pass the `ctx.route` value into the relevant Compose
method as a key (e.g. `LaunchedEffect(ctx.route)`, etc.)

### Default layouts

Since so many pages in your site (maybe even all of them?) will use the same `@Layout`, Kobweb lets you tag a file
(and, by extension, its associated package) with the layout annotation:

```kotlin
// jsMain/kotlin/com/mysite/pages/Layout.kt

@file:Layout(".components.layouts.PageLayout")

package com.mysite.pages

import com.varabyte.kobweb.core.layout.Layout
```

Once you've done this, then any page defined under that package will automatically apply that layout (unless it
explicitly declares its own layout).

If your site defines multiple default layouts, say `PageLayout` for `com.mysite.pages` and `BlogLayout` for
`com.mysite.pages.blog`, then the most specific one will apply. In other words, `BlogLayout` would take precedence
over `PageLayout` for all pages under the `blog` subpackage, while everything else would use `PageLayout`.

### Extending layouts

A layout can itself declare a parent layout. This is useful if you want to create a layout which extends another layout,
perhaps with some extra scaffolding for a subset of your site.

For example, imagine you have some general site UI that you'd like to supplement with additional sidebar content for
just the article pages on your site, allowing you to navigate through article section headers with a click.

To do this, tag the layout with the `@Layout` annotation (as you normally would), but include a path to the other
layout, exactly as you do when defining a `@Page`:

```kotlin
// jsMain/kotlin/com/mysite/components/layouts/PageLayout.kt
@Layout
@Composable
fun PageLayout(content: @Composable () -> Unit) { /*...*/ }
```
```kotlin
// jsMain/kotlin/com/mysite/components/layouts/ArticleLayout.kt
@Layout(".components.layouts.PageLayout")
@Composable
fun ArticleLayout(content: @Composable () -> Unit) { /*...*/ }
```

At this point, if you visit a page whose layout is set to `".components.layouts.ArticleLayout"`, the composition
hierarchy will look like this:

```kotlin
App {
    PageLayout {
        ArticleLayout {
            ArticlePage()
        }
    }
}
```

As you navigate around your site, even between article pages and non-article pages, the `PageLayout` composable will
always be in the same place in the call hierarchy, so it will avoid unnecessary recompositions and remember `remember`ed
values.

### `@NoLayout`

A page can indicate it explicitly does not want to use any layout at all, by using the `@NoLayout` annotation:

```kotlin
@Page
@NoLayout
@Composable
fun LayoutlessPage() { /* ... */ }
```

This can occasionally be useful for pages where you don't want to adorn them with any of your site's normal scaffolding,
such as some special page where you want to have full control of its appearance.

Without a layout, the composition hierarchy essentially skips the layout layer entirely:

```kotlin
App {
    Page()
}
```

This does mean when you navigate back to a page *with* a layout that Compose will treat it as a new composition.

## `@InitRoute` and page data

The API allowed for layouts is admittedly restrictive, and users will quickly find themselves asking a question: "How
can I configure it?"

For example, a common pattern used on many sites is setting a header title that gets updated per page:

```kotlin
@Layout
@Composable
fun PageLayout(content: @Composable () -> Unit) {
    val title = getTitleSomehow()
    H1 { Text(title) }
    content()
}
```

It can be tempting to want to define a layout like this:

```kotlin
@Layout
@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) { /*...*/ }
```

But remember, you don't call the layout method directly. Kobweb does it for you!

Therefore, declaring layout parameters would require informing Kobweb about those parameters as well. This would add too
much extra boilerplate code, making it an impractical approach in practice.

Instead, we decided to support communicating to layouts via page data.

Specifically, `PageContext.data` is a simple data store that lets you add any data values into it that you want and can
then later query by type.

So for the `title` example above, sidestepping momentarily exactly where we would add the data, using it looks like
this:

```kotlin
class PageLayoutData(val title: String)

// Somewhere...?
ctx.data.add(PageLayoutData("Home Page")) // Add

// Then...
@Layout
@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
   val title = ctx.data.getValue<PageLayoutData>().title // Query
   H1 { Text(title) }
   content()
}
```

So far so good, but now we need to figure out where in our codebase we can actually add the data.

We can't put it in our page method because by the time we are calling it, we are already mid-render and the layout
composition pass has already happened. That is too late!

### `@InitRoute`

This is where `@InitRoute` comes in.

Any file that defines a `@Layout` or `@Page` method can additionally define an
`@InitRoute` method that, if present, will get called before the page and its layouts begin their first render pass.

`@InitRoute` methods must take a single `InitRouteContext` parameter, which provides mutable access to the `data`
property. (When `data` is queried from inside a layout, it will only have a read-only view of the data.)

Bringing it all together, your final code should look something like this:

```kotlin
// jsMain/kotlin/com/mysite/components/layouts/PageLayout.kt

class PageLayoutData(val title: String)

@Layout
@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
   val title = ctx.data.getValue<PageLayoutData>().title
   H1 { Text(title) }
   /*...*/
   content()
}
```
```kotlin
// jsMain/kotlin/com/mysite/pages/HomePage.kt

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Home Page"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    /*...*/
}
```

At this point, every page that uses the `PageLayout` layout must also create an associated `@InitRoute` method to
initialize the data.

However, you can provide an `@InitRoute` method at the `PageLayout` method to prevent a crash if a page ever forgets:

```kotlin
// jsMain/kotlin/com/mysite/components/layouts/PageLayout.kt

class PageLayoutData(val title: String)

@InitRoute
fun initPageLayout(ctx: InitRouteContext) {
    ctx.data.addIfAbsent { PageLayoutData("(Missing title)")}
}

@Layout
@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) { /*...*/ }
```

> [!CAUTION]
> The `ctx.data` store is cleared every time you visit a new page, even if you are using the same layout across pages.
> If this is a limitation for your use-case, consider looking into local storage and session storage, discussed in more
> detail in ${DocsLink("Persisting State", "persisting-state")}.

### `@InitRoute` calling order

Finally, it's worth calling out the order that `@InitRoute` methods are called in. They are triggered child first and
then up through all ancestor layouts. Once rendering starts happening, that executes in the opposite order.

In other words, if you have `BaseLayout`, `ChildLayout`, and `Page`, each with their own corresponding init methods,
then the calling order will be:

* `initPage()`
* `initChildLayout()`
* `initBaseLayout()`
* `BaseLayout()`
* `ChildLayout()`
* `Page()`

What this allows is code where you keep appending / modifying data as you initialize up the layout chain, and then by
the time you start rendering, all data will be present. As for rendering going the other direction, from top-to-bottom,
well, that's just how rendering engines work!

## Are layouts necessary?

Before layouts existed, Kobweb simply recommended users create a composable method and just call it as the first method
inside each page.

For example:

```kotlin
// PageLayout.kt
@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) { /*...*/ }
```
```kotlin
// Page1.kt
@Page
@Composable
fun Page1() {
    PageLayout("page 1") { /*...*/ }
}
```
```kotlin
// Page2.kt
@Page
@Composable
fun Page2() {
    PageLayout("page 2") { /*...*/ }
}
```

Honestly, if this pattern works for your project, it's a legitimate approach. It is both type-safe and straightforward.

However, just be sure you understand that your composition hierarchy looks like this:

```kotlin
App {
    Page {
        Layout { content() }
    }
}
```

If you ever find yourself with some state getting dropped as you navigate across pages (e.g. sidebar content with
expanded / collapsed states that get reset), this is a sign it is probably time to migrate.

### A quick note about `movableContentOf`

Some users may be aware that the Compose API provides a feature called `movableContentOf` which, if you're familiar
with it, seems like it could be useful here.

However, our investigations found that, at the moment at least, its implementation makes some assumptions that don't
play nicely with Compose HTML ([relevant YouTrack](https://youtrack.jetbrains.com/issue/CMP-7969)). Therefore, for the
foreseeable future, using movable content is probably a non-starter, and we can't officially recommend it.

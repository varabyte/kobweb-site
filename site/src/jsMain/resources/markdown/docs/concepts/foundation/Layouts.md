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

```kotlin 3,5
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

```kotlin 3,4
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

At this point, when you visit the home page (from the above example), your site will be composed as follows:

```kotlin
App {
    Layout {
        Page()
    }
}
```

When you navigate between pages that all use the same layout, many sections and widgets in the layout might not even
recompose, and any values you `remember` will survive across the pages.

If you explicitly want layout code that changes on new pages, pass the `ctx.route.path` value into any relevant Compose
method that accepts a key:

```kotlin 4-6,8-10
@Layout
@Composable
fun PageLayout(content: @Composable () -> Unit) {
    LaunchedEffect(ctx.route.path) {
        // Rerun this logic per page
    }

    val perPageValue = remember(ctx.route.page) {
        // Create new value per page
    }
}
```

### Default layouts

Since so many pages in your site (maybe even all of them?) will use the same `@Layout`, Kobweb lets you tag a file
(and, by extension, its associated package) with the layout annotation:

```kotlin 3,5
// jsMain/kotlin/com/mysite/pages/Layout.kt

@file:Layout(".components.layouts.PageLayout")

package com.mysite.pages

import com.varabyte.kobweb.core.layout.Layout
```

Once you've done this, then any page defined under that package will automatically apply that layout (unless it
explicitly declares its own layout).

If your site defines multiple default layouts, say associating `PageLayout` with `com.mysite.pages` and `BlogLayout`
with `com.mysite.pages.blog`, then the most specific one will apply. In other words, `BlogLayout` would take precedence
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
```kotlin 2
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

```kotlin 2
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

## Communicating between layouts and pages

### `@InitRoute` and page data

When using layouts, users will quickly find themselves asking a question: "How can I configure part of the layout based
on the page that I'm on?"

For example, a common pattern used by many sites is setting a header title that gets updated per page:

```kotlin 4
@Layout
@Composable
fun PageLayout(content: @Composable () -> Unit) {
    val title = getTitleSomehow() // ???
    H1 { Text(title) }
    content()
}
```

It can be tempting to want to define a layout like this, accepting a `title` argument:

```kotlin 4
@Layout
@Composable
fun PageLayout(
    title: String,
    content: @Composable () -> Unit
) { /*...*/ }
```

But remember, you don't call the layout method directly. Kobweb does it for you!

So if you added several parameters to your layout function, you would also need an indirect way for each page to
indicate how it wants to set those parameters. In practice, many approaches would require a ton of noisy, fragile
boilerplate to accomplish this.

Instead, we decided to support communicating to layouts via *page data*.

Specifically, the `PageContext` instance provides a `data` property which is a simple data store that lets you add any
data values into it that you want and can then later query by type.

So for the `title` example above, let's sidestep momentarily exactly where from the page we'll add the data. Using it
looks like this:

```kotlin 1,4,9
class PageLayoutData(val title: String)

// Somewhere...?
ctx.data.add(PageLayoutData("Home Page"))

@Layout
@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
   val title = ctx.data.getValue<PageLayoutData>().title
   H1 { Text(title) }
   content()
}
```

So far so good, but now we need to figure out where in our codebase we can actually add the data.

We can't put it in our page method because by the time we are calling it, we are already mid-render and the layout
composition pass has already happened. That is too late!

#### `@InitRoute`

This is where `@InitRoute` comes in.

Any file that defines a `@Layout` or `@Page` method can additionally define an
`@InitRoute` method which, if present, will get called before the page and its layouts begin their first render pass.

`@InitRoute` methods must take a single `InitRouteContext` parameter, which provides *mutable* access to the `data`
property.

> [!IMPORTANT]
> When `data` is queried from inside a layout, it will have a read-only view of it. It will not be able to add or remove
> additional values.

Bringing it all together, your final code should look something like this:

```kotlin 3,8
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
```kotlin 3-6,9
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

Note in the example above that due to the way we wrote our code, `PageLayoutData` *must* be initialized or else the site
will crash.

For people who believe in the fail fast case, this may be what you want! However, you can use the
`ctx.data.get<PageLayoutData>()` call instead which will return null instead of crashing.

However, our recommended approach is to provide an `@InitRoute` method at the `PageLayout` level, using the
`addIfAbsent` helper method, as this gives you the benefit of knowing your data will always be set but in a way that you
can still inform the developer if a page was missed:

```kotlin 7,16-17
// jsMain/kotlin/com/mysite/components/layouts/PageLayout.kt

class PageLayoutData(val title: String)

@InitRoute
fun initPageLayout(ctx: InitRouteContext) {
    ctx.data.addIfAbsent {
        console.warn("${ctx.route.path} did not set PageLayoutData")
        PageLayoutData("(Missing title)")
    }
}

@Layout
@Composable
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    // Guaranteed to be present
    ctx.data.getValue<PageLayoutData>()
}
```

> [!CAUTION]
> The `ctx.data` store is cleared every time you visit a new page, even if you are using the same layout across pages.
> If this is a limitation for your use-case, consider looking into local storage and session storage, discussed in more
> detail in ${DocsLink("Persisting State", "persisting-state")}.

#### `@InitRoute` calling order

It's worth understanding the order that `@InitRoute` methods are called in. **They are triggered child-first and
then up through all ancestor layouts.** Once rendering starts happening, that executes in the opposite order.

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
well, that's just how rendering works!

#### Layout callbacks

Occasionally, users will implement layouts that want to expose a callback, especially as Compose recommends
state-hoisting as much as possible.

Let's work with a simple (silly) idea, just to provide a concrete example.

To start, we'll create a layout which has a button on it. This button, when clicked, gives any of its pages a chance to
respond to it.

```kotlin 1-3,9-11
class ButtonLayoutData(
    val onClick: () -> Unit
)

@Layout
@Composable
fun ButtonLayout(ctx: PageContext, content: @Composable () -> Unit) {
    Column {
        Button(onClick = {
            ctx.data.getValue<ButtonLayoutData>().onClick()
        }) {
            Text("Click Me")
        }
        content()
    }
} 
```

Now, let's say we want to create a page that keeps track of how many times that button is clicked, displaying the count.

If we were writing straightforward Compose code, we would create a layout composable that just accepted the callback
as one of its arguments. Such code might look like this:

```kotlin
var clickCount by remember { mutableStateOf(0) }
ButtonLayout(onClick = { clickCount++ }) {
    Text("You clicked $clickCount time(s)!")
}
```

However, in our world of separated pages and layouts, you need to register the callback handler in the `@InitRoute`
method, which is *not* `@Composable` code, meaning we don't have access to `remember`.

Instead, we recommend you declare mutable state as a private top-level property in your page file, at which point you
can set it in the `@InitRoute` call and reference it in your `@Composable` page:

```kotlin 1,5,12-13
private val clickCountState = mutableStateOf(0)

@InitRoute
fun initButtonCountPage(ctx: InitRouteContext) {
    ctx.data.add(ButtonLayoutData(onClick = { clickCountState.value++ }))
}

@Page
@Layout(".components.layouts.ButtonLayout")
@Composable
fun ButtonCountPage() {
    val clickCount by clickCountState
    Text("You clicked $clickCount time(s)!")
}
```

A little more verbose, sure, but it's the best way we've identified that gets the job done!

### Layout scopes

The previous sections make it clear how to communicate from the page to the layout, but what about the other direction?
How can we define values in the parent layout that we want to pass down to pages?

Kobweb supports this by allowing you to define a receiver scope on the `content` callback in your layout:

```kotlin 6
class PageLayoutScope { /*...*/ }

@Layout
@Composable
fun PageLayout(
    content: @Composable PageLayoutScope.() -> Unit
) {
    val scope = remember { PageLayoutScope() }
    scope.content()
}
```

At this point, if you additionally scope your page with the same receiver, Kobweb will hook things up behind the scenes
seamlessly so that your page will receive the data as expected:

```kotlin 3
@Page
@Layout(".components.layouts.PageLayout")
fun PageLayoutScope.ExamplePage() {
    /*...*/
}
```

A page without any receiver can still declare itself as a child of a layout that provides one.

However, once a page declares a receiver, it can *only* use layouts that provide that use that exact receiver in its
`content` callback. If not, the Kobweb KSP processor will issue an error at compile time.

Using layout scopes can be an effective way to pass down utility methods that any child page can call:

```kotlin
interface ShoppingPageScope {
    fun addItemToCart(id: Int)
    fun navigateToCart()
}

@Layout
@Composable
fun ShoppingPageLayout(
    ctx: PageContext,
    content: @Composable ShoppingPageScope.() -> Unit
) {
    val scope = remember {
        object : ShoppingPageScope {
            override fun addItemToCart(id: Int) { /*...*/ }
            override fun navigateToCart() {
                ctx.router.navigateTo("/cart")
            }
        }
    }
    scope.content()
}
```
```kotlin
@Page
@Layout(".components.layouts.ShoppingPageLayout")
fun ShoppingPageScope.BrowseItemPage(ctx: PageContext) {
    val itemId = ctx.route.params.getValue("item-id")
    /*...*/
    Button(onClick = {
        addItemToCart(itemId)
        navigateToCart() 
    }) {
        Text("BUY NOW!")
    }
}
```

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
    PageLayout("page 1") {
        /*...*/
    }
}
```
```kotlin
// Page2.kt
@Page
@Composable
fun Page2() {
    PageLayout("page 2") {
        /*...*/
    }
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
expanded / collapsed states that get reset), this is a sign it is probably time to migrate. In addition to more
consistent state behavior across pages, you can also reduce one level of indentation, which is nice.

### A quick note about `movableContentOf`

Some users may be aware that the Compose API provides a feature called `movableContentOf` which, if you're familiar
with it, seems like it could be useful here.

However, our investigations found that, at the moment at least, its implementation makes some assumptions that don't
play nicely with Compose HTML ([relevant YouTrack](https://youtrack.jetbrains.com/issue/CMP-7969)). Therefore, for the
foreseeable future, using movable content is probably a non-starter, and we can't officially recommend it.

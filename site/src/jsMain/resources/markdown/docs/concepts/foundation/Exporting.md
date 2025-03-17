---
description: How to export your Kobweb site into a final layout that is ready to be served by a webserver.
follows: ApplicationRoot
---

One of Kobweb's major additions on top of Compose HTML is the export process.

This feature elevates the framework from one that produces a single-page application to one that produces a whole,
navigable site. The export process takes snapshots of every page, resulting in better SEO support and a quicker initial
render.

A normal development workflow will have you using `kobweb run` to iterate on your site, and then when you're ready to
publish it, you'll `kobweb export` a production version.

## A concrete export example

Let's take a moment to walk through this process in more detail, in order to demystify it.

If you weren't using Kobweb and were just using Compose HTML directly, you'd be recommended to create an `index.html`
file that looks like this:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>My Site Title</title>
</head>
<body>
<div id="root"></div>
<script src="mysite.js"></script>
</body>
</html>
```

> [!NOTE]
> For example, you can find this exact structure
> recommended [in the official *Getting Started* instructions](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/HTML/Getting_Started#6-add-the-indexhtml-file-to-the-resources).

What this does is declare a root `<div>` element whose children will get populated dynamically at runtime. The
`mysite.js` script at the end of the file contains all the logic needed to generate every single page of your website.

This is very powerful, but when you build a website with this approach, you run into two major issues:

1. As your codebase grows larger, `mysite.js` gets bigger and bigger, meaning a larger download is required before the
   site gets rendered. The initial view will just be an empty page until the script runs, which is dependent on the
   script's size and the user's download speeds.
2. Search engines have a harder time indexing your site, because they can't see the content until the JavaScript
   executes. Any web crawler that doesn't execute JavaScript will never see anything more than a blank page.

OK, so let's add Kobweb into the mix. Here, we build a very minimal page and export our site (using `kobweb export`) to
see what happens.

```kotlin
@Page
@Composable
fun ExampleKobwebPage() {
    Text("This is a minimal example to demonstrate exporting.")
}
```

Exporting generates the following HTML under your `kobweb/.site` folder, which I've reproduced here with a bunch of
styles elided:

```html
<!doctype html>
<html lang="en">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>My Site Title</title>
  <meta content="Powered by Kobweb" name="description">
  <link href="/favicon.ico" rel="icon">
  <meta content="width=device-width, initial-scale=1" name="viewport">
 </head>
 <body>
  <div id="root" style="...">
   <style>...</style>
   <div class="..." style="min-height: 100vh;">
    This is a minimal example to demonstrate exporting.
   </div>
  </div>
  <script src="/mysite.js"></script>
 </body>
</html>
```

As you can see, Kobweb has filled out a bunch of extra information, although the site script it still linked to at the
bottom of the file. This is important since, as mentioned earlier in this section, it contains all information necessary
not just to render this page but the whole site.

In other words, you can download just this page and then continue to navigate around the site without needing to
download any more files.

In short, the export process will discover all `@Page`-annotated methods in your codebase and generate a snapshot of
each one. You can think of each snapshot as an SEO-friendly starting point from which you can access the rest of your
site.

## Exporting requires a browser

In order for Kobweb exporting to be able to take a snapshot of your site, it needs to spin up a browser in headless
mode. This browser is responsible for loading the simple Compose HTML version of an `index.html` page and running its
JavaScript to fill out the page. The browser will then get queried for the final html which Kobweb saves to disk.

Kobweb delegates much of this task to Microsoft's excellent [Playwright framework](https://playwright.dev/). Hopefully
this will be invisible to almost all users, but for advanced cases, it can be useful to know the technology that's
running under the hood.

For custom CI/CD setups, you will at the very least need to be aware that the Kobweb export process requires a browser.
For users who would like more information about this, ${DocsLink("we share a concrete example in a guide much later", "/docs/guides/git-hub-workflow-export")}.

## Static layout vs. Full stack sites

There are two flavors of Kobweb sites: *static* and *full stack*.

### Static layout sites

A *static* site (or, more completely, a *static layout* site) is one where you export a bunch of frontend files (e.g.
`html`, `js`, and public resources) into a single, organized folder that gets served directly by
a [static website hosting provider](https://en.wikipedia.org/wiki/Web_hosting_service#Static_page_hosting).

In other words, you don't write a single line of server code. The server is provided for you in this case and uses a
fairly straightforward algorithm - it hosts all the content you upload to it as raw, static assets.

The name *static* does not refer to the behavior of your site but rather that of your hosting provider solution. If
someone makes a request for a page, the same response bytes get served every time (even if that page is full of
custom code that allows it to behave in very interactive ways).

### Full stack sites

A *full stack* site is one where you write both the logic that runs on the frontend (i.e. on the user's machine) and the
logic that runs on the backend (i.e. on a server somewhere). This custom server must at least serve requested files
(exactly the same job that a static web hosting service does) plus it likely also defines endpoints providing custom
functionality tailored to your site's needs.

For example, maybe you define an endpoint which, given a user ID and an authentication token, returns that user's
profile information.

### Choosing the right site layout for your project

When Kobweb was first written, it only provided the full stack solution, as being able to write your own server logic
enabled a maximum amount of power and flexibility. The mental model for using Kobweb during this early time was simple
and clear.

However, in practice, most projects don't need the power afforded by a full stack setup. A website can give users a
very clean, dynamic experience simply by writing responsive frontend logic to make it look good, e.g. with animations
and delightful user interactions.

Additionally, many "*Feature* as a Service" solutions have popped up over the years, which can provide a ton of
convenient functionality that used to require a custom server. These days, you can easily integrate auth, database, and
analytics solutions all without writing a single line of backend code.

The process for exporting a bunch of files in a way that can be consumed by a static web hosting provider tends to be
*much* faster *and* cheaper than using a full stack solution. Therefore, you should prefer a static site layout unless
you have a specific need for a full stack approach.

Some possible reasons to use a custom server (and, therefore, a full stack approach) are:
* needing to communicate with other, private backend services in your company.
* intercepting requests as an intermediary for some third-party service where you own a very sensitive API key that you
  don't want to leak (such as a service that delegates to ChatGPT).
* acting as a hub to connect multiple clients together (such as a chat server).

If you aren't sure which category you fall into, then you should probably be creating a static layout site. It's much
easier to migrate from a static layout site to a full stack site later than the other way around.

## Exporting and running

Both site flavors, static and fullstack, require an export.

To export your site with a static layout, use the `kobweb export --layout static`
command, while for full stack the command is `kobweb export --layout fullstack` (or just `kobweb export` since
`fullstack` is the default layout as it originally was the only way).

Once exported, you can test your site by running it locally before uploading. You run a static site with
`kobweb run --env prod --layout static` and a full stack site with `kobweb run --env prod --layout fullstack` (or just
`kobweb run --env prod`).

### `PageContext.isExporting`

Sometimes, you have behavior that should run when an actual user is navigating your site but *not* at export time. For
example, maybe you offer logged-in users an authenticated experience, but you'll never have a logged-in user when
exporting.

You can determine if your page is being rendered as part of an export by checking the `PageContext.isExporting` property.
This gives you the opportunity to manipulate the exported HTML or avoid side effects associated with page loading.

```kotlin
@Composable
fun AuthenticatedLayout(content: @Composable () -> Unit) {
    var loggedInUser by remember { mutableStateOf<User?>(null) }

    val ctx = rememberPageContext()
    if (!ctx.isExporting) {
        LaunchedEffect(Unit) {
            loggedInUser = checkForLoggedInUser() // <- A slow, expensive method
        }
    }

    if (loggedInUser == null) {
        LoggedOutScaffold { content() }
    } else {
        LoggedInScaffold(user) { content() }
    }
}
```

## Dynamic routes and exporting

Dynamic routes are skipped over by the export process. After all, it's not possible to know all the possible values that
could be passed into a dynamic route.

However, if you have a specific instance of a dynamic route that you'd like to export, you can configure your site's
build script as follows:

```kotlin
kobweb {
  app {
    export {
      // "/users/{user}/posts/{post}" has special handling for the "default" / "0" case
      addExtraRoute("/users/default/posts/0", exportPath = "users/index.html")
    }
  }
}
```

## Deploying

A static site gets exported into `.kobweb/site` by default (you can configure this location in your `.kobweb/conf.yaml`
file if you'd like). You can then upload the contents of that folder to the static web hosting provider of your choice.

Deploying a full stack site is a bit more complex, as different providers have wildly varying setups, and some users may
even decide to run their own web server themselves. However, when you export your Kobweb site, scripts are generated for
running your server, both for *nix platforms (`.kobweb/server/start.sh`) and the Windows
platform (`.kobweb/server/start.bat`). If the provider you are using speaks Dockerfile, you can set `ENTRYPOINT` to
either of these scripts (depending on the server's platform).

Going in more detail than this is outside the scope of this README. However, you can read my blog posts for a lot more
information and some clear, concrete examples:

* [Static site generation and deployment with Kobweb](https://bitspittle.dev/blog/2022/static-deploy)
* [Deploying Kobweb into the cloud](https://bitspittle.dev/blog/2023/cloud-deploy)

## Exporting traces

The Kobweb export feature is built on top of [Microsoft Playwright](https://playwright.dev/), a solution for making it
easy to download and run browsers programmatically.

One of the features provided by Playwright is the ability to generate traces, which are essentially detailed reports
you can use to understand what is happening as your site loads. Kobweb exposes this feature through the `export` block
in your Kobweb application's build script.

Enabling traces is easy:

```kotlin
// build.gradle.kts
plugins {
  // ... other plugins ...
  alias(libs.plugins.kobweb.application)
}

kobweb {
  app {
    export {
      enableTraces()
    }
  }
}
```

You can pass in parameters to configure the `enableTraces` method, but by default, it will generate trace files into
your `.kobweb/export-traces/` directory.

Once enabled, you can run `kobweb export`, then once exported, open any of the generated `*.trace.zip` files by
navigating to them using your OS's file explorer and drag-and-dropping them into
the [Playwright Trace Viewer](https://trace.playwright.dev/).

> [!TIP]
> You can learn more about how to use the Trace
> Viewer [using the official documentation](https://playwright.dev/docs/trace-viewer).

It's not expected many users will need to debug their site exports, but it's a great tool to have (especially combined
with ${DocsLink("server logging", "/docs/concepts/server/fullstack#server-logs")}) to diagnose if one of your pages is
taking longer to export than expected.

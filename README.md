This is a [Kobweb](https://github.com/varabyte/kobweb) project bootstrapped with the `site` template.

## Getting Started

First, run the development server by typing the following command in a terminal at this project's root:

```bash
kobweb run
```

Open [http://localhost:8080](http://localhost:8080) with your browser to see the result.

You can use any editor you want for the project, but we recommend using **IntelliJ IDEA Community Edition** downloaded
using the [Toolbox App](https://www.jetbrains.com/toolbox-app/).

Press `Q` (or `CMD/CTRL-D`) in the terminal to gracefully stop the server.

## Navigating the Project

This simple project has a couple of example files you can learn from.

### jsMain

* `MyApp.kt`: This is the entry-point composable called for ALL pages. It's a useful place to specify global html/css
  styles as well as enable other features (like `Silk`). Note that the method is annotated with `@App` which is how
  `Kobweb` is aware of it.
* `.../components/layout/PageLayout.kt`: An example layout which, unlike `MyApp`, won't get called automatically.
  Instead, this is a recommended way to organize your high level, shared, layout composables. It is expected that most
  pages on your site will share the same layout, but you can create others if different pages have different
  requirements. You can see this project calling this method explicitly in all our pages.
* `.../components/sections/NavHeader.kt`: An example re-usable composable which represents a section inside a page. This
  particular example creates a header that makes it easy to navigate between this demo project.
* `.../pages/Index.kt`: The top level page, which will get rendered if the user visits `(yoursite.com)/` (the name
  `index` means it will be a special page that gets visited by default when no explicit page is specified). Note that
  the method is annotated with `@Page` which is how `Kobweb` is aware of it.
* `.../pages/About.kt`: An additional page, which will get rendered if the user visits `(yoursite.com)/about`. This page
  doesn't do much but exists as a way to demonstrate a multi-page layout.
* `.../resources/markdown/Markdown.md`: A markdown file which generates a reactive page for you automatically at compile
  time. This page will get rendered if the user visits `(yoursite.com)/markdown` If you are writing a blog, it can be
  very convenient to write many of your posts using markdown instead of Kotlin code. You can call out to components
  within your markdown using the `${...}` syntax, e.g. `${.components.widget.VisitorCounter}`
  (_Note: `${}` not yet implemented, but coming soon_)

### jvmMain

* `.../api/Hello.kt`: An example API endpoint that will be run on the server, triggered if the user visits
  `(yoursite.com)/api/hello`

### Live Reload

Feel free to edit / add / delete new components, pages, and API endpoints! When you make any changes, the site will
indicate the status of the build and automatically reload when ready.

## Exporting the Project

When you are ready to ship, you should shutdown the development server and then export the project using:

```bash
kobweb export
```

When finished, you can run a Kobweb server in production mode:

```bash
kobweb run --env prod
```

You should be able to run this command in the Cloud provider of your choice, at which point, once your Cloud environment
is configured, it will serve your site.
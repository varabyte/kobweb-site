---
follows: ApplicationRoot
---

## Organization conventions

Outside the `pages` folder, it is common to create reusable, composable parts. While Kobweb doesn't enforce any
particular rule here, we recommend a convention that, if followed, may make it easier to allow new readers of your
codebase to get around.

So, as a sibling to `pages`, create a folder called `components`. Within it, add:

* **layouts** - High-level composables that provide entire page layouts. Most (all?) of your `@Page` pages will start by
  calling a page layout function first. It's possible that you will only need a single layout for your entire site.
* **sections** - Medium-level composables that represent compound areas inside your pages, organizing a collection of
  many children composables. If you have multiple layouts, it's likely sections would be shared across them. For
  example, nav headers and footers are great candidates for this subfolder.
* **widgets** - Low-level composables. Focused UI pieces that you may want to reuse all around your site. For example,
  a stylized visitor counter would be a good candidate for this subfolder.

Your project should look something like this:

{{{ Folders

* my-project
  * src/jsMain/kotlin
    * components
      * layouts
      * sections
      * widgets
    * pages

}}}

## Public resources

If you have a resource that you'd like to serve from your site, you handle this by placing it in your site's
`jsMain/resources/public` folder.

For example, if you have a logo you'd like to be available at `mysite.com/assets/images/logo.png`, you would put it in
your Kobweb project at `jsMain/resources/public/assets/images/logo.png`.

{{{ Folders

* my-project
  * src/jsMain/resources
      * public
        * assets/images
          * logo.png

}}}


In other words, anything under your project resources' `public/` directory will be automatically copied over to your
final site (but not including the `public/` part).

## Multi-module projects

For simplicity, most new projects will put all their pages and widgets inside a single application module, e.g.
`site/`, which applies the `com.varabyte.kobweb.application` plugin in its build script.

However, you can additionally define components and/or pages in separate modules. Simply apply the
`com.varabyte.kobweb.library` plugin in their build scripts instead.

In other words, you can split up and organize your project like this:

{{{ Folders

* my-project
  * sitelib
    * build.gradle.kts
    * src/jsMain
      * kotlin/org/example/myproject/sitelib
        * components
        * pages
  * site
    * build.gradle.kts
    * .kobweb/conf.yaml
    * src/jsMain
      * kotlin/org/example/myproject/site
        * components
        * pages

}}}

If you'd like to explore a multimodule project example, you can do so by running:

```bash
$ kobweb create examples/chat
```

which demonstrates a chat application with its auth and chat functionality each organized into their own separate
modules.

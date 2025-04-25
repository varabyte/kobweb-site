---
description: Understanding the high levels structre of a Kobweb project.
follows: Index
---

Trying to find your way around a Kobweb project for the first time? This page will help you understand the high level
structure of every Kobweb project.

## The `.kobweb` folder

Your site will have a `.kobweb` folder in it, which is a home both for important configuration as well as generated
output files.

{{{ Folders

* my-project/site
  * .kobweb
    * conf.yaml
    * server
    * site

}}}

> [!NOTE]
> The `server` and `site` folders do not exist until after you build / export your project.

### conf.yaml

The file `conf.yaml`, also called the "Kobweb conf" file, is very important. It contains configuration needed by the
Kobweb server, and if the file doesn't exist, the Kobweb server won't run.

There are also some values that are used by the client as well. However, such values will also be referenced by the 
server. If a value is only needed by the client and not the server, it will live in the Gradle build script instead.

Specific `conf.yaml` values will be discussed through these docs in relevant sections.

### server

The `server` folder contains helpful scripts for starting your server (a `.sh` and `.bat` file), which can be really
handy if you are running your Kobweb project on a CI (e.g. inside a Docker container). It is also a place that
${DocsLink("server logs", "../server/fullstack#server-logs")} are written to, in case you need to review them.

### site

The `site` folder contains the final output of your site, generated after an ${DocsLink("export", "exporting")}.

## Components and Pages

Kobweb sites of course declare webpages ${DocsAside("Page", "routing#page")} -- it wouldn't be much of a web framework
without them! These will live under the `pages` folder in the `jsMain` source set of your project.

Outside the `pages` folder , it is common to create reusable, composable parts. While Kobweb doesn't enforce any
particular rule here, we recommend a convention that, if followed, may make it easier to allow new readers of your
codebase to get around.

So, as a sibling to `pages`, you should have a folder called `components`. Within it:

* **layouts** - High-level composables that provide entire ${DocsLink("page layouts", "layouts")}. Most (all?) of your
  `@Page` pages will declare itself as using one of these layouts. It is definitely possible (and fine!) that you may
  only need a single layout for your entire site.
* **sections** - Medium-level composables that represent compound areas inside your pages, organizing a collection of
  many children composables. If you have multiple layouts, it's likely sections would be shared across them. For
  example, nav headers and footers are great candidates for this subfolder.
* **widgets** - Low-level composables. Focused UI pieces that you may want to reuse all around your site. For example,
  a stylized visitor counter would be a good candidate for this subfolder.

In other words, we recommend a final layout that looks like this:

{{{ Folders

* my-project/site
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

* my-project/site
  * src/jsMain/resources
      * public
        * assets/images
          * logo.png

}}}

In other words, anything under your project resources' `public/` directory will be automatically copied over to your
final site (but not including the `public/` part).

## API endpoints

If your project also provides a backend ${DocsAside("Fullstack", "../server/fullstack")}, then you should have a
`jvmMain` folder in your project. API endpoints, which in Kobweb are handled by
${DocsLink("API routes", "../server/fullstack#api-routes")}, will live under an `api` folder. 

{{{ Folders

* my-project/site
  * src/jvmMain/kotlin
    * api

}}}

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

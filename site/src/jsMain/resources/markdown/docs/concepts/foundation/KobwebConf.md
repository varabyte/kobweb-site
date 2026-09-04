---
title: "Kobweb Configuration"
description: A quick introduction to the Kobweb configuration file.
toc-max: 6
---

Every Kobweb application is identified by a configuration file that lives under the `.kobweb` folder called `conf.yaml`
-- in other words, `.kobweb/conf.yaml`, often referred to as your project's "Kobweb Conf".

If no file is found there, then `kobweb run` will not consider the folder a Kobweb application, even if it applies the
Kobweb Application plugin.

This conf file contains values that are needed by the Kobweb server when running your application.

There are occasionally values in here that are used by the client as well. However, in such cases, the server will
also reference those values. When a value is only needed by the client and _not_ the server, you will instead find the
place to set it somewhere inside your project's build scripts.

Various `conf.yaml` values will be discussed throughout the rest of these docs in more relevant sections. And you can
always find the up-to-date list of all possible values by exploring
the [KobwebConf API docs](https://varabyte.github.io/kobweb/common/kobweb-common/com.varabyte.kobweb.project.conf/-kobweb-conf/index.html).


However, there are a handful of required values that the server _must_ know to run your application. We will discuss
those now.

```yaml
site:
  title: "Kobweb Conf Example"

server:
  files:
    dev:
      contentRoot: "build/processedResources/js/main/public"
      script: "build/kotlin-webpack/js/developmentExecutable/kobweb-conf-example.js"
      api: "build/libs/kobweb-conf-example.jar" # for sites with a custom Kobweb server 
    prod:
      script: "build/kotlin-webpack/js/productionExecutable/kobweb-conf-example.js"
      siteRoot: ".kobweb/site"

  port: 8080
```

## Site

### Title

The user-facing title for your site.

This value will be used in the `<html><head><title>` tag of your site.

## Server

### Port

This is the port that the server will listen on.

Most projects shouldn't care about this, but it could be useful if you are working on two related Kobweb sites that you
want servers to run for at the same time.

Note that in dev mode, the server will automatically find a free neighboring port if the requested port is already in
use. However, when trying to run in prod mode or when exporting your site, if the requested port is already in use, the
task will fail.

### Files

The following values are files external to the server that drive its core behavior.

#### Dev

The following values are only used by a dev server, i.e., the server that runs via `kobweb run`.

##### Content root

This is the location where a dev server will look for ${DocsLink("public resources", "project-structure#public-resources")},
such as fonts, images, and other files that the server might be requested to serve at runtime.

##### Script

This is the location where a dev server will look for the debug version of the compiled JS file that represents your
entire site.

##### Api

If your web project is configured as a ${DocsLink("fullstack site", "../server/fullstack")}, this is the location where
the server will look for the compiled API jar that contains the JVM logic for all your backend endpoints.

If your project is not fullstack (most projects are not), then you can leave this value out.

#### Prod

The following values are only used by a production server, i.e., the server that runs following an export via
`kobweb run --env prod` (or by using one of our convenient server startup scripts,
`.kobweb/server/start.sh` or `.kobweb/server/start.bat`).

##### Script

This is the location where a production server will look for the release version of the compiled JS file that
represents your site.

##### Site root

This is the root directory where all exported site files will be placed.

If you change this value, you are not simply specifying where your Kobweb server should look for them, but you are also
telling Kobweb where it should put them.

This can be useful if you want to tell the framework to put files somewhere convenient for an external service to find
them. For example, you could set this value to the docs folder at the root of your project (i.e., "../docs") and then
[configure your GitHub Pages settings to use it](https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site).
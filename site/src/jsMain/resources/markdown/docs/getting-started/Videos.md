---
description: Video resources to help you learn Kobweb that can supplement this guide (especially for visual learners).
imports:
  - .components.widgets.video.VimeoVideo
  - .components.widgets.video.YouTubeVideo
---

If you prefer to watch videos instead of (or in addition to) reading text, this section introduces a handful of
external resources that can also help you get started with Kobweb.

Otherwise, feel free to skip and proceed to the next section!

## Intro talk

You can [check out my talk at KotlinConf '25](https://kotlinconf.com/talks/774286/) for a high level overview of Kobweb.
The talk showcases what Kobweb can do, introduces Compose HTML (which it builds on top of), and covers foundational
frontend and backend functionality. It is light on code but heavy on understanding the structure and capabilities of the
framework.

{{{ YouTubeVideo("https://www.youtube.com/watch?v=vWIDRH6aQfI") }}}

## Community

> [!CAUTION]
> The following videos were created while Kobweb was still in its relative infancy, so they may occasionally introduce
> concepts that have since been updated.
>
> Most significantly, `ComponentStyle` has been renamed to
> ${DocsLink("CssStyle", "/docs/concepts/presentation/silk#cssstyle")}, because the feature grew beyond just applying to
> widgets and is more generally applicable now.
>
> So if you see:
> ```kotlin
> SomeStyle by ComponentStyle { /* ... */ }
> ```
> that should now be written as:
> ```kotlin
> SomeStyle = CssStyle { /* ... */ }
> ```
>
> Overall, the information provided in these videos will still be helpful for learning and understanding the structure
> of a Kobweb project.

### Stevdza-san

One of Kobweb's users, Stevdza-San, has created free starting tutorials that demonstrate how to build projects using
Kobweb.

#### Getting started with Kobweb

This video introduces basic Kobweb concepts and walks you through the process from creating a simple (static layout)
site to exporting it locally on your machine (with files you can then upload to a static hosting provider of your
choice).

{{{ YouTubeVideo("https://www.youtube.com/watch?v=F5B-CxJTKlg") }}}

#### Deploying a Kobweb site

This video builds upon the previous, showcasing some additional tips and tricks, and walks you all the way through to
deploying your site live on the internet using free hosting.

{{{ YouTubeVideo("https://www.youtube.com/watch?v=ciAqQPThXn0") }}}

#### Building a full stack multiplatform site

This video demonstrates how to write both frontend and backend logic. It also demonstrates how you can write a separate
Android frontend that can also work with your server. (This video is still useful to watch even if you never intend to
use Android).

{{{ YouTubeVideo("https://www.youtube.com/watch?v=zcrY0qayWF4") }}}

> [!TIP]
> It's easy to start with a static layout site first and migrate to a full stack site later, in case you're feeling
> paralyzed. ${DocsAside("Static layout vs. Full stack sites", "/docs/concepts/foundation/exporting#static-layout-vs-full-stack-sites")}

### Skyfish

A YouTube channel called SkyFish creates webdev tutorials.

#### Building a full stack site with a MongoDB server

This video demonstrates how to build a simple website with a frontend and a backend, using API routes and leveraging a
MongoDB instance for persistent data storage.

{{{ YouTubeVideo("https://www.youtube.com/watch?v=VVNq6yovU_0") }}}

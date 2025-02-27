Kobweb is an opinionated Kotlin framework for creating websites and web apps, built on top of
[Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html) and inspired by [Next.js](https://nextjs.org)
and [Chakra UI](https://chakra-ui.com).

This means you get to use Compose (an elegant, reactive library which should be familiar to most Android developers) to
declare your HTML structure while also leaning on the Kobweb framework for features like page routing, client/server
communication, light/dark color mode support, and a modern widget set.

{{{ .components.sections.home.HeroCode }}}

{{{ .components.sections.home.HeroExample }}}

## Objective

We wrote Kobweb in order to make using Compose HTML feel way more enjoyable. We want to enable a world where more
developers can confidently choose Kotlin as a way to create modern websites.

> [!NOTE]
> To learn more about Compose HTML, please visit [the official tutorials](https://github.com/JetBrains/compose-jb/tree/master/tutorials/HTML/Getting_Started).

Compose HTML is a very powerful library, but it leaves a lot of basic decisions up to the developer to navigate. While
this can seem on its face like a fine approach, these choices can often be the sort that most of us don't actually care
that much about and just amount to being chores.

For example, when we were first evaluating if our vision of Kobweb was even possible, we wanted to trivialize setting up
routing across pages, eliminate the need to write a dummy `index.html` file, support styling HTML elements with CSS
without having to create a global stylesheet object, and support live reloading as a first-class feature.

You get these features (and much more) out of the box when you create your Kobweb project. Are those really the sort of
problems you'd want to solve yourself?

In short, we want to get all the tedious work out of the way, so you can enjoy focusing on the more interesting parts
of designing and developing your website!

## High-level structure

Kobweb, in addition to being a collection of libraries, also provides Gradle plugins and KSP processors which
automatically analyze your codebase at compile time in order to generate all necessary boilerplate for your project.

Kobweb is also a CLI binary of the same name which lets you issue commands to handle the tedious parts of building
and/or running a Compose HTML app (e.g. `kobweb run`).

## Goals

We aim to provide:

* an intuitive structure for organizing your Kotlin website or web app
* automatic handling of routing between pages
* a collection of useful _batteries included_ widgets built on top of Compose HTML
* an environment built from the ground up around live reloading
* static site exports for improved SEO and potentially cheaper server setups
* support for responsive (i.e. mobile and desktop) design
* shared, rich types between client and server
* out-of-the-box Markdown support
* a way to easily define server API routes
* an open source foundation that the community can extend
* and much, much more!

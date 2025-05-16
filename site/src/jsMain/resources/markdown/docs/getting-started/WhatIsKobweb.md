---
description: A high level overview of what Kobweb is and what it can do.
---

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

Compose HTML is a powerful library, providing you with access to Compose-ified versions of all the HTML elements.

However, it also leaves many common pain points unaddressed -- this is where Kobweb comes in.

Kobweb handles routing, generates the necessary `index.html`, ensures SEO-friendly pages, embraces live-reloading,
supports dark and light modes, simplifies CSS style management, and much more.

In short, Kobweb's goal is to allow you to enjoy focusing on what matters -- *and* what's more fun: building and
designing your website!

## High-level structure

Kobweb, in addition to being a collection of libraries, also provides Gradle plugins and KSP processors which
automatically analyze your codebase at compile time to generate all necessary boilerplate for your project.

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

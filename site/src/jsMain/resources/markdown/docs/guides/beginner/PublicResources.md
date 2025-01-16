---
follows: CreateAPage
---

If you have a resource that you'd like to serve from your site, you handle this by placing it in your site's
`jsMain/resources/public` folder.

For example, if you have a logo you'd like to be available at `mysite.com/assets/images/logo.png`, you would put it in
your Kobweb project at `jsMain/resources/public/assets/images/logo.png`.

In other words, anything under your project resources' `public/` directory will be automatically copied over to your
final site (not including the `public/` part).

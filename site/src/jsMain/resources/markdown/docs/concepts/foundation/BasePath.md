---
follows: PersistingState
---

Typically, sites live at the top level. This means if you have a root file `index.html` and your site is hosted at
the domain `https://mysite.com` then that HTML file can be accessed by visiting `https://mysite.com/index.html`.

However, in some cases, your site may be hosted under a subfolder, such as `https://example.com/products/myproduct/`, in
which case your site's root `index.html` file would live at `https://example.com/products/myproduct/index.html`.

Kobweb needs to know about this subfolder structure so that it can take it into account in its routing logic. This can
be specified in your project's `.kobweb/conf.yaml` file with the `basePath` value under the `site` section:

```yaml
site:
  title: "..."
  basePath: "..."
```

where the value of `basePath` is the part between the origin part of the URL and your site's root. For example, if
your site is rooted at `https://example.com/products/myproduct/`, then the value of `basePath` would be `products/myproduct`.

> [!TIP]
> GitHub Pages is a common web hosting solution that developers use for their sites. By default, this approach hosts
> your site under a subfolder (set to the project's name).
>
> In other words, if you are planning to host your Kobweb site on GitHub Pages, you will need to set an appropriate
> `basePath` value.
>
> For a concrete example of setting `basePath` for GitHub Pages
> specifically, [check out this relevant section](https://bitspittle.dev/blog/2022/static-deploy#base-path) from my blog
> site that goes over it.

Once you've set your `basePath` in the `conf.yaml` file, you can generally design your site without explicitly
mentioning it, as Kobweb provides base-path aware widgets that handle it for you. For example,
`Link("/docs/manuals/v123.pdf")` will automatically resolve to
`https://example.com/products/myproduct/docs/manuals/v123.pdf`.

Of course, you may find yourself working with code external to Kobweb that is not base-path aware. If you find you need
to access the base path value explicitly in your own code, you can do so by using the `BasePath.value` property or by
calling the `BasePath.prepend` companion method.

```kotlin
// The Video element comes from Compose HTML and is NOT base-path aware.
// Therefore, we need to manually prepend the base path to the video source.
Video(attrs = {
    width(320)
    height(240)
}) {
    Source(attrs = {
        attr("type", "video/mp4")
        attr("src", BasePath.prepend("/videos/demo.mp4"))
    })
 }
```

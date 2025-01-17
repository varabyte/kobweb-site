---
follows: BasePath
---

Over the lifetime of a site, you may find yourself needing to change its structure. Perhaps you need to move a handful
of pages under a new folder, or you need to rename a page, etc.

However, if your site has been live for a while, you may have a ton of internal links to those pages. Worse, the rest of
the web (say, Google search results, or blogs and articles) may be full of links to those old locations, so even if you
can find and fix up everything on your end, you can't control what others have done.

The web has long supported the concept of redirects to handle this. By advertising what links you've changed publicly,
search indices can be updated and even if someone visits your page at the old location, your server can automatically
tell your browser where they should have gone instead.

In Kobweb, you can define redirects in your project's `.kobweb/conf.yaml` file. You simplify define a series of `from`
and `to` values in the `server.redirects`
block.

```yaml
server:
  redirects:
    - from: "/old-page"
      to: "/new-page"
```

Kobweb servers will pick up these redirect values from the `conf.yaml` file and will intercept any matching incoming
route requests, sending back a [301 status code](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/301) to the
client.

So, in the above example, if a user tries to visit `https://example.com/old-page`, they will be redirected to
`https://example.com/new-page` automatically. Any internal links on your site that reference the old page will also be
handled -- trying to navigate to the old location will automatically end up at the new one.

The Kobweb redirect feature also supports using regexes in the `from` value, which can then be referenced in the `to`
section using `$1`, `$2`, etc. variables which will be substituted with text matches in parentheses.

Group matching can be really useful if you want to redirect a whole section of your site to a new location. For example,
the following redirect rule can help if you've moved all pages from an old parent folder into a new one:

```yaml
server:
  redirects:
    - from: "/socials/facebook/([^/]+)"
      to: "/socials/meta/$1"
```

The last thing to note is that if you have multiple redirects, they will be processed in order and all applied. This
should rarely matter in most cases, but you can use it if you need to combine both changing a folder name AND a page
name:

```yaml
server:
  redirects:
    - from: "/socials/facebook/([^/]+)"
      to: "/socials/meta/$1"
    - from: "(/socials/meta)/about-facebook"
      to: "$1/about-meta"
```

> [!IMPORTANT]
> If you are using a third-party static hosting provider to host your site, they will be unaware of the Kobweb
> `conf.yaml` file, so you will need to read their documentation to learn how to configure your redirects with them.
>
> In this case, you may be able to skip defining redirects in your own Kobweb configuration file, since it may be
> redundant at that point. However, it may still be useful to do for documentation purposes and to ensure you won't 404
> due to an old, internal link that you forgot to update.

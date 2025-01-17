---
title: CORS
follows: ServerLogs
---

[CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS), or *Cross-Origin Resource Sharing*, is a security
feature built on the idea that a web page should not be able to make requests for resources from a server that is not
the same as the one that served the page *unless* it was served from a trusted domain.

To configure CORS for a Kobweb backend, Kobweb's `.kobweb/conf.yaml` file allows you to declare such trusted domains
using a `cors` block:

```yaml
server:
  cors:
    hosts:
      - name: "example.com"
        schemes:
          - "https"
```

> [!NOTE]
> Specifying the schemes is optional. If you don't specify them, Kobweb defaults to "http" and "https".

> [!NOTE]
> You can also specify subdomains, e.g.
> ```yaml
> - name: "example.com"
>   subdomains:
>     - "en"
>     - "de"
>     - "es"
> ```
> which would add CORS support for `en.example.com`, `de.example.com`, and `es.example.com`, as well as `example.com`
> itself.

Once configured, your Kobweb server will be able to respond to data requests from any of the specified hosts.

> [!TIP]
> If you find that your full-stack site, which was working locally during development, rejects requests in the
> production version, check your browser's console logs. If you see errors in there about a violated CORS policy, that
> means you didn't configure CORS correctly.


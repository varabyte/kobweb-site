---
follows: SharingState
---

For simplicity, new projects can choose to put all their pages and widgets inside a single application module, e.g.
`site/`.

However, you can define components and/or pages in separate modules and apply the `com.varabyte.kobweb.library` plugin
on them (in contrast to your main module which applies the `com.varabyte.kobweb.application` plugin.)

In other words, you can split up and organize your project like this:

```
my-project
├── sitelib
│   ├── build.gradle.kts # apply "com.varabyte.kobweb.library"
│   └── src/jsMain
│       └── kotlin.org.example.myproject.sitelib
│           ├── components
│           └── pages
└── site
    ├── build.gradle.kts # apply "com.varabyte.kobweb.application"
    ├── .kobweb/conf.yaml
    └── src/jsMain
        └── kotlin.org.example.myproject.site
            ├── components
            └── pages
```

If you'd like to explore a multimodule project example, you can do so by running:

```bash
$ kobweb create examples/chat
```

which demonstrates a chat application with its auth and chat functionality each managed in their own separate modules.

---
root: .components.layouts.DocsLayout
follows: CreateFirstSite
imports:
  - .components.widgets.*
---

# Callout Demo

To be deleted.

##  From Markdown

```md
> [!IMPORTANT]
> The last part of a URL, here `settings`, is called a *slug*.
```
> [!IMPORTANT]
> The last part of a URL, here `settings`, is called a *slug*.

```md
> [!NOTE]
> You can find [official documentation for CSS custom properties here](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties).
```

> [!NOTE]
> You can find [official documentation for CSS custom properties here](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties).

> [!TIP]
> Compose HTML provides a `CSSLengthValue`, which represents concrete values like `10.px` or `5.cssRem`. However, Kobweb
> provides a `CSSLengthNumericValue` type which represents the concept more generally, e.g. as the result of
> intermediate calculations. There are `CSS*NumericValue` types provided for all relevant units, and it is recommended
> to use them when declaring style variables as they more naturally support being used in calculations.
>
> We discuss [CSSNumericValue types▼](#cssnumeric) in more detail later in this document.

> [!CAUTION]
> Despite the flexibility allowed here, you should not be using this feature frequently, if at all. A Kobweb project
> benefits from the fact that a user can easily associate a URL on your site with a file in your codebase, but this
> feature allows you to break those assumptions. It is mainly provided to enable dynamic routing (see the
> [Dynamic Routes▼](https://github.com/varabyte/kobweb?tab=readme-ov-file#dynamic-routes) section) or enabling a URL
> name that uses characters which aren't allowed in Kotlin filenames.



## All Callouts


{{{ GitHubStyleCallout(CalloutType.NOTE, "Highlights information that users should take into account, even when skimming.") }}}

{{{ GitHubStyleCallout(CalloutType.TIP, "Provides a helpful tip or trick that can make the user's life easier.") }}}

{{{ GitHubStyleCallout(CalloutType.IMPORTANT, "Highlights important information that users should be aware of.") }}}

{{{ GitHubStyleCallout(CalloutType.WARNING, "Warns users about potential issues or pitfalls.") }}}

{{{ GitHubStyleCallout(CalloutType.CAUTION, "Warns users about potential issues or pitfalls that could lead to data loss.") }}}

{{{ GitHubStyleCallout(CalloutType.QUOTE, "Highlights a quote or excerpt from the text.") }}}

{{{ GitHubStyleCallout(CalloutType.SUCCESS, "Highlights a successful operation or positive outcome.") }}}

{{{ GitHubStyleCallout(CalloutType.FAILURE, "Highlights a failed operation or negative outcome.") }}}

{{{ GitHubStyleCallout(CalloutType.INFO, "Highlights general information that users should be aware of.") }}}

{{{ MkDocsStyleCallout(CalloutType.NOTE, "Highlights information that users should take into account, even when skimming.") }}}

{{{ MkDocsStyleCallout(CalloutType.TIP, "Provides a helpful tip or trick that can make the user's life easier.") }}}

{{{ MkDocsStyleCallout(CalloutType.IMPORTANT, "Highlights important information that users should be aware of.") }}}

{{{ MkDocsStyleCallout(CalloutType.WARNING, "Warns users about potential issues or pitfalls.") }}}

{{{ MkDocsStyleCallout(CalloutType.CAUTION, "Warns users about potential issues or pitfalls that could lead to data loss.") }}}

{{{ MkDocsStyleCallout(CalloutType.QUOTE, "Highlights a quote or excerpt from the text.") }}}

{{{ MkDocsStyleCallout(CalloutType.SUCCESS, "Highlights a successful operation or positive outcome.") }}}

{{{ MkDocsStyleCallout(CalloutType.FAILURE, "Highlights a failed operation or negative outcome.") }}}

{{{ MkDocsStyleCallout(CalloutType.INFO, "Highlights general information that users should be aware of.") }}}

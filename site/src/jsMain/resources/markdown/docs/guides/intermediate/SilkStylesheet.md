---
follows: ApplicationRoot
---

The default styles picked by browsers for many HTML elements rarely fit most site designs, and it's likely you'll want
to tweak at least some of them. A very common example of this is the default web font, which if left as is will make
your site look a bit archaic.

Most traditional sites overwrite styles by creating a CSS stylesheet and then linking to it in their HTML. However, if
you are using Silk in your Kobweb application, you can use an approach very similar to `CssStyle` discussed above but
for general HTML elements.

To do this, create an `@InitSilk` method. The context parameter includes a `stylesheet` property that represents the CSS
stylesheet for your site, providing a Silk-idiomatic API for adding CSS rules to it.

Below is a simple example that sets the whole site to more aesthetically pleasing fonts than the browser defaults, one
for regular text and one for code:

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
  ctx.stylesheet.registerStyleBase("body") {
    Modifier.fontFamily("Ubuntu", "Roboto", "Arial", "Helvetica", "sans-serif")
      .fontSize(18.px)
      .lineHeight(1.5)
  }

  ctx.stylesheet.registerStyleBase("code") {
    Modifier.fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")
  }
}
```

> [!TIP]
> The `registerStyleBase` method is commonly used for registering styles with minimal code, but you can also use
> `registerStyle`, especially if you want to add some support for one or more pseudo-classes (
> e.g. `hover`, `focus`, `active`):
>
> ```kotlin
> ctx.stylesheet.registerStyle("code") {
>   base {
>     Modifier
>       .fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")
>       .userSelect(UserSelect.None) // No copying code allowed!
>   }
>   hover {
>     Modifier.cursor(Cursor.NotAllowed)
>   }
> }
> ```

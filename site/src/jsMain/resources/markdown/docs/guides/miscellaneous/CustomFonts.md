---
follows: Debugging
---

It is common to want to use a custom font on your site. Setting this up is fairly easy.

### Font hosting service

The easiest way to use a custom font is if it is already hosted for you. For example, Google Fonts provides a CDN that
you can use to load fonts directly.

> [!CAUTION]
> While this is the easiest approach, be sure you won't run into compliance issues! If you use Google Fonts on your
> site, you may technically be in violation of the GDPR in Europe, because an EU citizen's IP address is communicated to
> Google and logged. You may wish to find a Europe-safe host instead, or self-host, which you can read about
> in [the next section▼](#self-hosted-fonts).

The font service should give you HTML to add to your site's `<head>` tag. For example, Google Fonts suggests the
following when I select Roboto Regular 400:

```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
```

This code should be converted into Kotlin and added to the `kobweb` block of your site's `build.gradle.kts` script:

```kotlin
kobweb {
  app {
    index {
      head.add {
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
        link(
          href = "https://fonts.googleapis.com/css2?family=Roboto&display=swap",
          rel = "stylesheet"
        )
      }
    }
  }
}
```

Once done, you can now reference this new font:

```kotlin
Column(Modifier.fontFamily("Roboto")) {
    Text("Hello world!")
}
```

### Self-hosted fonts

Users can flexibly declare a custom font by using
CSS's [`@font-face` rule](https://developer.mozilla.org/en-US/docs/Web/CSS/@font-face).

In Kobweb, you can normally declare CSS properties in Kotlin (within an `@InitSilk` block), but unfortunately, Firefox
doesn't allow you to define or modify `@font-face` entries
in code ([relevant Bugzilla issue](https://bugzilla.mozilla.org/show_bug.cgi?id=443978)). Therefore, for guaranteed
cross-platform compatibility, you should create a CSS file and reference it from your build script.

To keep the example concrete, let's say you've downloaded the open
source font [Lobster](https://fonts.google.com/specimen/Lobster) from Google Fonts (and its license as well, of course).

You need to put the font file inside your public resources directory, so it can be found by the user visiting your site.
I recommend the following file organization:

```
jsMain
└── resources
    └── public
        └── fonts
            ├── faces.css
            └── lobster
                ├── OFL.txt
                └── Lobster-Regular.ttf
```

where `faces.css` contains all your `@font-face` rule definitions (we just have a single one for now):

```css
@font-face {
  font-family: 'Lobster';
  src: url('/fonts/lobster/Lobster-Regular.ttf');
}
```

> [!NOTE]
> The above layout may be slightly overkill if you are sure you'll only ever have a single font, but it's flexible
> enough to support additional fonts if you decide to add more in the future, which is why we recommend it as a general
> advice here.

Now, you need to reference this CSS file from your `build.gradle.kts` script:

```kotlin
kobweb {
  app {
    index {
      head.add {
        link(rel = "stylesheet", href = "/fonts/faces.css")
      }
    }
  }
}
```

Finally, you can reference the font in your code:

```kotlin
Column(Modifier.fontFamily("Lobster")) {
    Text("Hello world!")
}
```

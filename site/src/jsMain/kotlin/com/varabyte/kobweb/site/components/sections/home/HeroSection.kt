package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.components.icons.fa.FaBook
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
import com.varabyte.kobweb.site.components.widgets.GradientBox
import com.varabyte.kobweb.site.components.widgets.LinkButton
import com.varabyte.kobweb.site.components.widgets.Section
import com.varabyte.kobweb.site.components.widgets.code.CodeBlock
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private val DARK_BACKGROUND = Color.rgb(25, 25, 25)
private val LIGHT_BACKGROUND = DARK_BACKGROUND.inverted()

@Composable
fun HeroCode() {
    CodeBlock(
        """
        @Page
        @Composable
        fun HomePage() {
          Column(
            Modifier
              .fillMaxWidth().whiteSpace(WhiteSpace.PreWrap).textAlign(TextAlign.Center),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            var colorMode by ColorMode.currentState
            Button(
              onClick = { colorMode = colorMode.opposite },
              Modifier.borderRadius(50.percent).padding(0.px).align(Alignment.End)
            ) {
              // Includes support for Font Awesome icons
              if (colorMode.isLight) FaMoon() else FaSun()
            }
            H1 {
              Text("Welcome to Kobweb!")
            }
            Span {
              Text("Create rich, dynamic web apps with ease, leveraging ")
              Link("https://kotlinlang.org/", "Kotlin")
              Text(" and ")
              Link(
                "https://github.com/JetBrains/compose-multiplatform/#compose-html",
                "Compose HTML"
              )
            }
          }
        }
        """.trimIndent(),
        lang = "kotlin",
    )
}

@Composable
fun HeroExample() {
    // For the example, we create our own local mode divorced from the site-wide value
    var localColorMode by remember { mutableStateOf(ColorMode.LIGHT) }
    val background = if (localColorMode.isLight) LIGHT_BACKGROUND else DARK_BACKGROUND
    val foreground = if (localColorMode.isLight) Colors.Black else Colors.White

    // Wrap in a surface so that we can override the color mode for this specific section
    Surface(Modifier.fillMaxWidth().backgroundColor(Colors.Transparent), colorModeOverride = localColorMode) {
        Column(
            SmoothColorStyle.toModifier()
                .fillMaxWidth()
                .borderRadius(12.px)
                .backgroundColor(background)
                .color(foreground)
                .padding(12.px)
                .whiteSpace(WhiteSpace.PreWrap)
                .textAlign(TextAlign.Center),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { localColorMode = localColorMode.opposite },
                Modifier.borderRadius(50.percent).padding(0.px).align(Alignment.End)
            ) {
                if (localColorMode.isLight) MoonIcon() else SunIcon()
            }
            // We have to slightly tweak header settings here from the actual code sample above since
            // the overall site overloads H1 values from the default
            H3(attrs = Modifier.margin(bottom = 1.cssRem).toAttrs()) {
                Text("Welcome to Kobweb!")
            }
            Span {
                Text("Create rich, dynamic web apps with ease, leveraging ")
                Link("https://kotlinlang.org/", "Kotlin")
                Text(" and ")
                Link("https://github.com/JetBrains/compose-multiplatform/#compose-html", "Compose HTML")
            }
        }
    }
}

val HeroButton = CssStyle {
    base {
        // Extra height helps these hero buttons feel a bit more substantial
        Modifier.width(300.px).setVariable(ButtonVars.Height, 3.cssRem)
    }

    Breakpoint.MD {
        Modifier.width(150.px)
    }
}

/**
 * A section which demonstrates a concise "hero" example of Kobweb code and the result it produces.
 */
@Composable
fun HeroSection() {
    GradientBox(contentAlignment = Alignment.Center) {
        Section {
            Column(
                Modifier.margin(top = 3.em, leftRight = 1.em),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                H1(Modifier.textAlign(TextAlign.Center).toAttrs()) {
                    SpanText("Create web apps in Kotlin")
                }
                SpanText(
                    "Kobweb is an opinionated framework built on top of Compose HTML. It includes everything you need to build rich, dynamic websites, as well as web applications, while being able to leverage the greater Kotlin ecosystem.",
                    Modifier.siteText(SiteTextSize.NORMAL).textAlign(TextAlign.Center),
                    MutedSpanTextVariant
                )
            }

            val iconMargin = Modifier.margin(right = 0.5.cssRem)

            SimpleGrid(
                numColumns(1, md = 2),
                Modifier.margin(topBottom = 2.cssRem).rowGap(1.cssRem).columnGap(1.cssRem),
            ) {
                LinkButton("/docs", HeroButton.toModifier(), "Get Started", primary = true) {
                    FaBook(iconMargin)
                }
                Tooltip(ElementTarget.PreviousSibling, "Read the Kobweb guide")


                LinkButton("https://github.com/varabyte/kobweb", HeroButton.toModifier(), "Github") {
                    FaGithub(iconMargin)
                }
                Tooltip(ElementTarget.PreviousSibling, "Kobweb source on GitHub")
            }

            Column(
                Modifier.margin(topBottom = 2.cssRem).displayIfAtLeast(Breakpoint.MD),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeroExample()
                HeroCode()
            }
        }
    }
}

package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.widgets.GradientBox
import com.varabyte.kobweb.site.components.widgets.KotlinCode
import com.varabyte.kobweb.site.components.widgets.LinkButton
import com.varabyte.kobweb.site.components.widgets.Section
import kotlinx.browser.window
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

private val DARK_BACKGROUND = Color.rgb(25, 25, 25)
private val LIGHT_BACKGROUND = DARK_BACKGROUND.inverted()

@Composable
private fun HeroExample(modifier: Modifier) {
    // For the example, we create our own local mode divorced from the site-wide value
    var localColorMode by remember { mutableStateOf(ColorMode.LIGHT) }
    val background = if (localColorMode.isLight) LIGHT_BACKGROUND else DARK_BACKGROUND
    val foreground = if (localColorMode.isLight) Colors.Black else Colors.White

    LaunchedEffect(Unit) {
        window.setInterval({
            localColorMode = localColorMode.opposite
        }, timeout = 5000)
    }

    Column(
        modifier.backgroundColor(background).color(foreground).padding(12.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.align(Alignment.End)) {
            if (localColorMode.isLight) FaMoon() else FaSun()
        }
        // We have to slightly tweak header settings here from the actual code sample above since
        // the overall site overloads H1 values from the default
        H3(attrs = Modifier.margin(bottom = 1.cssRem).toAttrs()) {
            Text("Welcome to Kobweb!")
        }
        Row {
            SpanText("Create rich, dynamic web apps with ease, leveraging ")
            Link("https://kotlinlang.org/", "Kotlin")
            SpanText(" and ")
            Link("https://compose-web.ui.pages.jetbrains.team/", "Compose for Web")
        }
    }
}

val HeroButton by ComponentStyle {
    base {
        Modifier.width(300.px)
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
                Modifier.margin(left = 3.em, right = 3.em, top = 3.em),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                H1 {
                    SpanText(
                        "Create web apps in Kotlin",
                        Modifier.textAlign(TextAlign.Center)
                    )
                }
                SpanText(
                    "Kobweb is an opinionated framework built on top of Compose for Web. It includes everything you need to build rich, dynamic websites, as well as web applications, while being able to leverage the greater Kotlin ecosystem.",
                    Modifier.lineHeight(1.5).fontSize(1.25.cssRem)
                        .textAlign(TextAlign.Center),
                    MutedSpanTextVariant
                )
            }

            SimpleGrid(
                numColumns(1, md = 2),
                Modifier.margin(topBottom = 32.px).rowGap(1.cssRem).columnGap(16.px),
            ) {
                LinkButton("/docs", HeroButton.toModifier(), "Start Learning", primary = true)
                LinkButton("https://github.com/varabyte/kobweb", HeroButton.toModifier(), "Github") {
                    FaGithub(Modifier.margin(right = 8.px))
                }
            }

            Column(
                Modifier.margin(top = 32.px, bottom = 32.px).displayIfAtLeast(Breakpoint.MD),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeroExample(Modifier.fillMaxWidth().borderRadius(12.px))
                KotlinCode(
                    // Set the color explicitly to opt-out of color mode for this section, which will always be on a grey
                    // background
                    modifier = Modifier
                        // Choose a background color that's dark-ish but not as dark as the hero example itself, so it
                        // stands out
                        .color(Colors.White)
                        .lineHeight(1.5.cssRem)
                        .padding(0.75.cssRem)
                        .background(ColorMode.DARK)
                        .borderRadius(12.px),
                    code = """
                        @Page
                        @Composable
                        fun HomePage() {
                          Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.align(Alignment.End)) {
                              var colorMode by rememberColorMode()
                              Button(
                                onClick = { colorMode = colorMode.opposite() },
                                Modifier.padding(0.px).clip(Circle())
                              ) {
                                Box(Modifier.margin(4.px)) {
                                  // Includes support for Font Awesome icons
                                  if (colorMode.isLight()) FaMoon() else FaSun()
                                }
                              }
                            }
                            H1 {
                              Text("Welcome to Kobweb!")
                            }
                            Row {
                              SpanText("Create rich, dynamic web apps with ease, leveraging ")
                              Link("https://kotlinlang.org/", "Kotlin")
                              SpanText(" and ")
                              Link("https://compose-web.ui.pages.jetbrains.team/", "Compose for Web")
                            }
                          }
                        }
                    """.trimIndent()
                )
            }
        }
    }
}

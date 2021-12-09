package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.widgets.GradientBox
import com.varabyte.kobweb.site.components.widgets.KotlinCode
import com.varabyte.kobweb.site.components.widgets.LinkButton
import com.varabyte.kobweb.site.components.widgets.Section
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.H1

private val DARK_BACKGROUND = Color.rgb(25, 25, 25)
private val LIGHT_BACKGROUND = DARK_BACKGROUND.inverted()

@Composable
private fun HeroExample(modifier: Modifier) {
    // For the example, we create our own local mode divorced from the site-wide value
    var localColorMode by remember { mutableStateOf(ColorMode.LIGHT) }
    val background = if (localColorMode.isLight()) LIGHT_BACKGROUND else DARK_BACKGROUND
    val foreground = if (localColorMode.isLight()) Colors.Black else Colors.White

    LaunchedEffect(Unit) {
        window.setInterval({
            localColorMode = localColorMode.opposite()
        }, timeout = 5000)
    }

    Column(
        modifier.background(background).color(foreground).padding(12.px).styleModifier {
            // Toggling color mode looks much more engaging if it animates instead of being instant
            transitionProperty("background-color", "color")
            transitionDuration(400.ms)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.align(Alignment.End)) {
            if (localColorMode.isLight()) FaSun() else FaMoon()
        }
        H1 {
            Text("Welcome to Kobweb!")
        }
        Row {
            Text("Create rich, dynamic web apps with ease, leveraging ")
            Link("https://kotlinlang.org/", "Kotlin")
            Text(" and ")
            Link("https://compose-web.ui.pages.jetbrains.team/", "Web Compose")
        }
    }
}

/**
 * A section which demonstrates a concise "hero" example of Kobweb code and the result it produces.
 */
@Composable
fun HeroSection() {
    GradientBox(contentAlignment = Alignment.Center) {
        Section {
            Row (modifier = Modifier.margin(left = 3.em, right = 3.em, top = 3.em)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "Modern framework for full stack web apps in Kotlin",
                        Modifier.fontSize(64.px).fontWeight(FontWeight.Bold).styleModifier {
                            textAlign(TextAlign.Center)
                        },
                    )
                    Br()
                    Text(
                        "Create full stack web apps in a modern, concise and type safe programming language Kotlin. Kobweb is an opinionated Kotlin framework built on top of Web Compose and includes everything you need to build modern static websites, as well as web applications faster.",
                        Modifier.lineHeight(1.5).fontSize(1.25.cssRem).styleModifier {
                            opacity(70.percent)
                            textAlign(TextAlign.Center)
                        }
                    )
                }
            }

            Row(Modifier.margin(top = 32.px)) {
                LinkButton("/docs", Modifier.width(150.px), "Start Learning", primary = true)
                LinkButton(
                    "https://github.com/varabyte/kobweb",
                    Modifier.margin(left = 12.px).width(150.px),
                    "Github"
                ) {
                    FaGithub(Modifier.margin(right = 8.px))
                }
            }
        }
        Box (
            Modifier.margin(top = 32.px, bottom = 32.px),
            contentAlignment = Alignment.Center
        ) {
            Column {
                KotlinCode(
                    // Set the color explicitly to opt-out of color mode for this section, which will always be on a grey
                    // background
                    modifier = Modifier
                        .color(Colors.White).fontSize(12.px).lineHeight(18.px).padding(12.px).styleModifier {
                            borderRadius(12.px)
                        },
                    code = """
                        @Page
                        @Composable
                        fun HomePage() {
                          Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.align(Alignment.End)) {
                              var colorMode by rememberColorMode()
                              Button(
                                onClick = { colorMode = colorMode.opposite() },
                                Modifier.clip(Circle())
                              ) {
                                Box(Modifier.margin(4.px)) {
                                  // Includes support for Font Awesome icons
                                  if (colorMode.isLight()) FaSun() else FaMoon()
                                }
                              }
                            }
                            H1 {
                              Text("Welcome to Kobweb!")
                            }
                            Row {
                              Text("Create rich, dynamic web apps with ease, leveraging ")
                              Link("https://kotlinlang.org/", "Kotlin")
                              Text(" and ")
                              Link("https://compose-web.ui.pages.jetbrains.team/", "Web Compose")
                            }
                          }
                        }
                    """.trimIndent()
                )

                HeroExample(Modifier.fillMaxWidth().styleModifier {
                    borderRadius(12.px)
                })
            }
        }
    }
}
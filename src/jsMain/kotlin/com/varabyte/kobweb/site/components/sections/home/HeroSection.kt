package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.site.components.widgets.GradientBox
import com.varabyte.kobweb.site.components.widgets.KotlinCode
import com.varabyte.kobweb.site.components.widgets.LinkButton
import com.varabyte.kobweb.site.components.widgets.SectionBox
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Img

/**
 * A section which demonstrates a concise "hero" example of Kobweb code and the result it produces.
 */
@Composable
fun HeroSection() {
    GradientBox(contentAlignment = Alignment.Center) {
        SectionBox {
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
            Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                KotlinCode(
                    // Set the color explicitly to opt-out of color mode for this section, which will always be on a grey
                    // background
                    modifier = Modifier.padding(12.px).color(Colors.White).styleModifier {
                        background("radial-gradient(circle at left, rgb(25,25,25) 0%, rgb(45,45,45) 100%)")
                        borderRadius(12.px)
                    },
                    code = """
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

                Img(
                    "images/hero-browser.png",
                    attrs = {
                        style {
                            height(475.px)
                            margin(8.px)
                        }
                    }
                )
            }
        }
    }
}
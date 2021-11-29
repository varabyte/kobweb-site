package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.site.components.widgets.GradientBox
import com.varabyte.kobweb.site.components.widgets.LinkButton
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Img

/**
 * A section which demonstrates a concise "hero" example of Kobweb code and the result it produces.
 */
@Composable
fun HeroSection() {
    GradientBox(contentAlignment = Alignment.Center) {
        Box(
            Modifier.padding(left = 12.em, right = 12.em, top = 6.em),
            contentAlignment = Alignment.Center
        ) {
            Row {
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

            Row(Modifier.padding(top = 32.px)) {
                LinkButton("/docs", Modifier.width(150.px), "Start Learning", primary = true)
                LinkButton(
                    "https://github.com/varabyte/kobweb",
                    Modifier.padding(left = 12.px).width(150.px),
                    "Github"
                ) {
                    FaGithub(Modifier.padding(right = 8.px))
                }
            }
        }
        Box (Modifier.padding(top = 32.px, bottom = 32.px), contentAlignment = Alignment.Center) {
            Row (horizontalArrangement = Arrangement.Center) {
                Img(
                    "https://storage.googleapis.com/kobweb-example-cdn/hero-ide.png",
                    attrs = {
                        style {
                            height(475.px)
                            margin(8.px)
                        }
                    }
                )
                Img(
                    "https://storage.googleapis.com/kobweb-example-cdn/hero-browser.png",
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
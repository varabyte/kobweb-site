    package org.example.myproject.pages

    import androidx.compose.runtime.*
    import com.varabyte.kobweb.compose.css.FontWeight
    import com.varabyte.kobweb.compose.css.TextAlign.Companion.Center
    import com.varabyte.kobweb.compose.css.textAlign
    import com.varabyte.kobweb.compose.foundation.layout.Box
    import com.varabyte.kobweb.compose.foundation.layout.Column
    import com.varabyte.kobweb.compose.foundation.layout.Row
    import com.varabyte.kobweb.compose.ui.*
    import com.varabyte.kobweb.core.Page
    import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
    import com.varabyte.kobweb.silk.components.text.Text
    import org.example.myproject.components.layouts.PageLayout
    import org.example.myproject.components.widgets.CustomButtonComponent
    import org.jetbrains.compose.web.css.*
    import org.jetbrains.compose.web.dom.Br

    @Composable
    private fun HeroSection() {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(760.px).padding(top = 4.cssRem)
        ) {
            Row {
                Box (contentAlignment = Alignment.Center) {
                    Text(
                        text = "Modern framework for full stack web apps in Kotlin",
                        modifier = Modifier.color(Color.whitesmoke).fontSize(64.px).fontWeight(FontWeight.Bold).styleModifier {
                            textAlign(Center)
                        },
                    )
                    Br {  }
                    Text(
                        text = "Create full stack web apps in a modern, concise and type safe programming language Kotlin. Kobweb is an opinionated Kotlin framework built on top of Web Compose and includes everything you need to build modern static websites, as well as web applications faster.",
                        modifier = Modifier.lineHeight(1.5).fontSize(1.25.cssRem).color(Color.whitesmoke).styleModifier {
                            opacity(60.percent)
                            textAlign(Center)
                        }
                    )
                }
            }

            Row (modifier = Modifier.padding(top = 32.px)) {
                CustomButtonComponent("/docs", "Start Learning", primary = true, modifier = Modifier.width(150.px)) {}
                CustomButtonComponent("https://github.com/varabyte/kobweb", "Github", modifier = Modifier.padding(left = 12.px).width(150.px)) {
                    FaGithub(modifier = Modifier.padding(right = 8.px))
                }
            }
        }
    }

    @Composable
    private fun GridItem(heading: String, desc: String) {
        Box (
            modifier = Modifier.width(260.px).height(260.px).padding(18.px).styleModifier {
                background("radial-gradient(circle at top, rgba(41,41,46,1) 0%, rgba(25,25,28,1) 100%)")
                borderRadius(12.px)
                padding(2.em)
            }
        ) {
            Column {
                Text(heading, Modifier.color(Color.whitesmoke).fontWeight(FontWeight.Bold))
                Br {}
                Text(desc, Modifier.lineHeight(1.5).color(Color.whitesmoke).styleModifier {
                    opacity(70.percent)
                })
            }
        }

    }

    @Composable
    private fun FeaturesSection() {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(940.px).padding(top = 6.cssRem)
        ) {
            Row {
                Box (contentAlignment = Alignment.Center) {
                    Text(
                        text = "Why Kobweb",
                        modifier = Modifier.color(Color.whitesmoke).fontSize(48.px).fontWeight(FontWeight.Bold).styleModifier {
                            textAlign(Center)
                        },
                    )
                    Br {  }
                    Text(
                        text = "Kobweb has all the tools you need to build full stack web apps",
                        modifier = Modifier.lineHeight(1.5).fontSize(1.25.cssRem).color(Color.whitesmoke).styleModifier {
                            opacity(60.percent)
                            textAlign(Center)
                        }
                    )
                }
            }
            Row (
                modifier = Modifier.padding(top = 2.cssRem)
            ) {
                GridItem(heading = "API Routes", desc = "Define and annotate methods which will generate server endpoints you can interact with")
                GridItem(heading = "File-system Routing", desc = "Every @Composable inside pages directory with @Page becomes a route")
                GridItem(heading = "Component library", desc = "Silk is a UI layer included with Kobweb and built upon Web Compose")
            }
            Row {
                GridItem(heading = "Live Reloading", desc = "An environment built from the ground up around live reloading")
                GridItem(heading = "Shared Types", desc = "Shared, rich types between client and server")
                GridItem(heading = "Markdown support", desc = "Out-of-the-box Markdown support")
            }
            Row {
                GridItem(heading = "Themeable", desc = "Customize any part of our components to match your design needs")
                GridItem(heading = "Light and Dark UI", desc = "Optimized for multiple color modes")
                GridItem(heading = "Optimized for SEO", desc = "static site exports for improved SEO")
            }
        }
    }

    @Page
    @Composable
    fun HomePage() {
        PageLayout("") {
            HeroSection()
            FeaturesSection()
        }
    }

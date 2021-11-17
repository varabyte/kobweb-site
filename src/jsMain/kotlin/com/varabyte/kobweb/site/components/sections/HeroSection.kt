import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.site.components.widgets.GradientBox
import com.varabyte.kobweb.site.components.widgets.LinkButton
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Img

@Composable
fun HeroSection() {
    GradientBox(Modifier.padding(left = 8.em, right = 8.em), Alignment.Center) {
        Box(
            Modifier.padding(left = 8.em, right = 8.em),
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
                            opacity(80.percent)
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
        Box (Modifier.padding(top = 32.px), contentAlignment = Alignment.Center) {
            Row {
                Img(
                    "https://storage.googleapis.com/kobweb-example-cdn/hero-ide.png",
                    attrs = {
                        style {
                            height(475.px)
                        }
                    }
                )
                Img(
                    "https://storage.googleapis.com/kobweb-example-cdn/hero-browser.png",
                    attrs = {
                        style {
                            height(475.px)
                        }
                    }
                )
            }
        }
    }
}
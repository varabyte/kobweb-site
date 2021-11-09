import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.text.Text
import org.example.myproject.components.widgets.CustomButton
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Img

@Composable
fun HeroSection() {
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier.width(940.px)
    ) {
        Row {
            Box (contentAlignment = Alignment.Center) {
                Text(
                    text = "Modern framework for full stack web apps in Kotlin",
                    modifier = Modifier.color(Color.whitesmoke).fontSize(64.px).fontWeight(FontWeight.Bold).styleModifier {
                        textAlign(TextAlign.Center)
                    },
                )
                Br {  }
                Text(
                    text = "Create full stack web apps in a modern, concise and type safe programming language Kotlin. Kobweb is an opinionated Kotlin framework built on top of Web Compose and includes everything you need to build modern static websites, as well as web applications faster.",
                    modifier = Modifier.lineHeight(1.5).fontSize(1.25.cssRem).color(Color.whitesmoke).styleModifier {
                        opacity(80.percent)
                        textAlign(TextAlign.Center)
                    }
                )
            }
        }

        Row (modifier = Modifier.padding(top = 32.px)) {
            CustomButton("/docs", "Start Learning", primary = true, modifier = Modifier.width(150.px)) {}
            CustomButton("https://github.com/varabyte/kobweb", "Github", modifier = Modifier.padding(left = 12.px).width(150.px)) {
                FaGithub(modifier = Modifier.padding(right = 8.px))
            }
        }
        Box (contentAlignment = Alignment.Center,
            modifier = Modifier.padding(top = 32.px)) {
            Img(
                "https://storage.googleapis.com/kobweb-example-cdn/hero-code.png",
                attrs = {
                    style {
                        height(582.px)
                    }
                }
            )
        }
    }
}

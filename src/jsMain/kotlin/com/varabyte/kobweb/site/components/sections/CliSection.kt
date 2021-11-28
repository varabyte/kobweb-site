import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.Text
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Img

@Composable
fun CliSection() {
    Box(
        Modifier.width(940.px).padding(top = 6.cssRem),
        contentAlignment = Alignment.Center
    ) {
        Row (
            horizontalArrangement = Arrangement.Center
        ) {
            Box (contentAlignment = Alignment.Center) {
                Text(
                    "Kobweb CLI",
                    Modifier.fontSize(48.px).fontWeight(FontWeight.Bold).styleModifier {
                        textAlign(TextAlign.Center)
                    },
                )
                Br {  }
                Text(
                    "Kobweb CLI provides commands to handle the parts of building a Web Compose app that are less glamorous including project setup and configuration",
                    Modifier.lineHeight(1.5).fontSize(1.25.cssRem).styleModifier {
                        opacity(70.percent)
                        textAlign(TextAlign.Center)
                    }
                )
            }
            Box (
                Modifier.padding(top = 2.cssRem),
                contentAlignment = Alignment.Center
            ) {
                Img(
                    "https://storage.googleapis.com/kobweb-example-cdn/kobweb-cli.gif",
                    attrs = {
                        style {
                            height(432.px)
                        }
                    }
                )
            }
        }
    }
}
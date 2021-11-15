import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.silk.components.text.Text
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Img

@Composable
fun CliSection() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.width(940.px).padding(top = 6.cssRem)
    ) {
        Row (
            horizontalArrangement = Arrangement.Center
        ) {
            Box (contentAlignment = Alignment.Center) {
                Text(
                    text = "Kobweb CLI",
                    modifier = Modifier.fontSize(48.px).fontWeight(FontWeight.Bold).styleModifier {
                        textAlign(TextAlign.Center)
                    },
                )
                Br {  }
                Text(
                    text = "Kobweb CLI provides commands to handle the parts of building a Web Compose app that are less glamorous including project setup and configuration",
                    modifier = Modifier.lineHeight(1.5).fontSize(1.25.cssRem).styleModifier {
                        opacity(80.percent)
                        textAlign(TextAlign.Center)
                    }
                )
            }
            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 2.cssRem)
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
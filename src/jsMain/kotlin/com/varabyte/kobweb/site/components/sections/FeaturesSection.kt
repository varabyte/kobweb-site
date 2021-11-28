import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.site.components.sections.getBoxShadow
import com.varabyte.kobweb.site.components.widgets.GradientBox
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br

private fun getBackgroundColor(colorMode: ColorMode): String {
    return when (colorMode) {
        ColorMode.DARK -> "radial-gradient(circle at top, rgba(41,41,46,1) 0%, rgba(25,25,28,1) 100%)"
        ColorMode.LIGHT -> "#ffffff"
    }
}

@Composable
private fun GridItem(heading: String, desc: String) {
    val colorMode by rememberColorMode()

    Box (
        Modifier.width(260.px).height(200.px).padding(18.px).styleModifier {
            borderRadius(12.px)
            background(getBackgroundColor(colorMode))
            padding(2.em)
            property("box-shadow", getBoxShadow(colorMode))
        }
    ) {
        Column {
            Text(heading, Modifier.fontWeight(FontWeight.Bold))
            Br {}
            Text(desc, Modifier.lineHeight(1.5).styleModifier {
                opacity(70.percent)
            })
        }
    }

}

@Composable
fun FeaturesSection() {
    GradientBox (
        Modifier.width(940.px).padding(top = 6.cssRem),
        contentAlignment = Alignment.Center,
    ) {
        Row {
            Box (contentAlignment = Alignment.Center) {
                Text(
                    "Why Kobweb?",
                    Modifier.fontSize(48.px).fontWeight(FontWeight.Bold).styleModifier {
                        textAlign(TextAlign.Center)
                    },
                )
                Br {  }
                Text(
                    "Kobweb has all the tools you need to build production full stack web apps",
                    Modifier.lineHeight(1.5).fontSize(1.25.cssRem).styleModifier {
                        opacity(70.percent)
                        textAlign(TextAlign.Center)
                    }
                )
            }
        }
        Row (Modifier.padding(top = 2.cssRem)) {
            GridItem("API Routes", "Define and annotate methods which will generate server endpoints you can interact with")
            GridItem("File-system Routing", "Every @Composable inside pages directory with @Page becomes a route")
            GridItem("Component library", "Silk is a UI layer included with Kobweb and built upon Web Compose")
        }
        Row {
            GridItem("Live Reloading", "An environment built from the ground up around live reloading")
            GridItem("Shared Types", "Shared, rich types between client and server")
            GridItem("Markdown support", "Out-of-the-box Markdown support")
        }
        Row {
            GridItem("Themeable", "Customize any part of our components to match your design needs")
            GridItem("Light and Dark UI", "Optimized for multiple color modes")
            GridItem("Optimized for SEO", "static site exports for improved SEO")
        }
    }
}
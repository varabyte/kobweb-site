package org.example.myproject

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.height
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.width
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.*

object CssGlobalsStyleSheet : StyleSheet() {
    init {
        "body" style {
            fontFamily("-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif")
            background("radial-gradient(circle at calc(60%),#2FCCB3 0,rgba(47, 204, 179,.5) 0,transparent 45%)")
            backgroundColor(Color.black)
        }
    }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    Style(CssGlobalsStyleSheet)
    SilkApp {
        SilkTheme {
            Surface(
                Modifier.width(100.vw).height(100.vh).styleModifier {
                    background("radial-gradient(circle at calc(60%),#2FCCB3 0,rgba(47, 204, 179,.5) 0,transparent 45%)")
                    backgroundColor(Color.black)
                    color(Color.antiquewhite)
                }
            ) {
                content()
            }
        }
    }
}

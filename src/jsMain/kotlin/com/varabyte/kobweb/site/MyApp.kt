package com.varabyte.kobweb.site

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.width
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.*
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.widgets.GradientBox
import org.jetbrains.compose.web.css.*

object CssGlobalsStyleSheet : StyleSheet() {
    init {
        "body" style {
            fontFamily("-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif")
        }
    }
}

@InitSilk
fun updateTheme(ctx: InitSilkContext) {
    ctx.config.initialColorMode = ColorMode.DARK
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    Style(CssGlobalsStyleSheet)

    SilkApp {
        GradientBox(Modifier.width(100.vw)) {
            content()
        }
    }
}
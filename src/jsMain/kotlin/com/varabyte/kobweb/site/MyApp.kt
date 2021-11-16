package com.varabyte.kobweb.site

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.*
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
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
        val backgroundColor = SilkTheme.palette.background
        Surface(
            Modifier.width(100.vw).styleModifier {
                background("radial-gradient(circle at calc(60%),#0079f2 0,rgba(0, 121, 242,.5) 0,transparent 45%)")
                backgroundColor(backgroundColor.toCssColor())
            }
        ) {
            content()
        }
    }
}
package com.varabyte.kobweb.site

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.height
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.width
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.*
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.SilkConfig
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorSchemes
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalettes
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.Color.black

object CssGlobalsStyleSheet : StyleSheet() {
    init {
        "body" style {
            fontFamily("-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif")
        }
    }
}

val DarkSilkPalette = run {
    val buttonBase = ColorSchemes.White._900
    SilkPalette(
        ColorMode.DARK,
        background = Color.White,
        color = Color(245, 245, 245, 1),
        link = SilkPalette.Link(
            default = Color.Blue,
            visited = Color.Purple,
        ),
        button = SilkPalette.Button(
            default = buttonBase,
            hover = buttonBase.darkened(),
            pressed = buttonBase.darkened().darkened()
        )
    )
}

val LightSilkPalette = run {
    val buttonBase = ColorSchemes.Black._900
    SilkPalette(
        ColorMode.LIGHT,
        background = Color.Black,
        color = Color.Black,
        link = SilkPalette.Link(
            default = Color.Blue.lightened(),
            visited = Color.Purple.lightened(),
        ),
        button = SilkPalette.Button(
            default = buttonBase,
            hover = buttonBase.lightened(),
            pressed = buttonBase.lightened().lightened()
        )
    )
}

@InitSilk
fun updateTheme(context: InitSilkContext) {
    // Note: This will become `context.config.initialColorMode` after we upgrade to a newer version of Kobweb
    // so the above greyed out `context` warning will go away.
    SilkConfig.initialColorMode = ColorMode.DARK
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
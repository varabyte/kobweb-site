package com.varabyte.kobweb.site

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.*
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import com.varabyte.kobweb.silk.theme.registerBaseStyle
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.*

private const val COLOR_MODE_KEY = "app:colorMode"

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.config.initialColorMode = localStorage.getItem(COLOR_MODE_KEY)?.let { ColorMode.valueOf(it) } ?: ColorMode.DARK
    ctx.config.registerBaseStyle("body") {
        Modifier.styleModifier {
            fontFamily("-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif")
        }
    }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = getColorMode()
        LaunchedEffect(colorMode) {
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        Surface {
            Box(Modifier.fillMaxSize().background(SilkTheme.palettes[colorMode].background.toCssColor())
            ) {
                content()
            }
        }
    }
}
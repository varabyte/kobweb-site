package com.varabyte.kobweb.site

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.AnimatedColorSurfaceVariant
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
        Modifier.fontFamily(
            "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
            "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
        )
    }

    val headerCommon = Modifier.fontWeight(FontWeight.Bold).textAlign(TextAlign.Center).margin(bottom = 1.cssRem)
    ctx.config.registerBaseStyle("h1") {
        headerCommon.fontSize(3.5.cssRem)
    }

    ctx.config.registerBaseStyle("h2") {
        headerCommon.fontSize(2.5.cssRem)
    }

    ctx.config.registerBaseStyle("h3") {
        headerCommon.fontSize(1.5.cssRem)
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

        Surface(variant = AnimatedColorSurfaceVariant) {
            Box(
                Modifier.fillMaxSize().backgroundColor(SilkTheme.palettes[colorMode].background)
            ) {
                content()
            }
        }
    }
}
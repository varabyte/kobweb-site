package com.varabyte.kobweb.site

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.BorderCollapse
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.layer
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.layer.SilkLayer
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.style.DividerColor
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.*

private const val COLOR_MODE_KEY = "app:colorMode"

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.config.initialColorMode = localStorage.getItem(COLOR_MODE_KEY)?.let { ColorMode.valueOf(it) } ?: ColorMode.DARK

    ctx.stylesheet.apply {
//        registerStyleBase("body") { Modifier.fontFamily("Roboto", "sans-serif") }
//        registerStyleBase("code, pre") { Modifier.fontFamily("Roboto Mono", "monospace") }
        registerStyleBase("body") {
            Modifier.fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
        }

        registerStyle("html") {
            base {
                Modifier.scrollPadding(top = 5.5.cssRem)
            }
            cssRule(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("no-preference"))) {
                Modifier.scrollBehavior(ScrollBehavior.Smooth)
            }
        }
        layer(SilkLayer.BASE) {
            registerStyleBase("ul, ol, menu") {
                Modifier
                    .listStyle(ListStyleType.None)
                    .padding(0.px)
                    .margin(0.px)
            }
            registerStyleBase("table") {
                Modifier.borderCollapse(BorderCollapse.Collapse)
            }
        }

        val headerCommon = Modifier.fontWeight(FontWeight.Bold).margin(top = 1.25.cssRem, bottom = 0.75.cssRem)
        registerStyleBase("h1") {
            headerCommon.fontSize(3.cssRem)
        }

        registerStyleBase("h2") {
            headerCommon.fontSize(2.cssRem)
        }

        registerStyleBase("h3") {
            headerCommon.fontSize(1.5.cssRem)
        }

        registerStyleBase("h4") {
            headerCommon
        }
    }
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = ColorMode.current
        LaunchedEffect(colorMode) {
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        Surface(
            SmoothColorStyle.toModifier()
                .fillMaxWidth()
                .minHeight(100.vh)
                .setVariable(
                    DividerColor,
                    if (colorMode.isDark) Color.rgba(238, 238, 238, 0.2f) else Color.rgba(17, 17, 17, 0.2f)
                )
        ) {
            content()
        }
    }
}

package com.varabyte.kobweb.site

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.BorderCollapse
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.layer
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.layer.SilkLayer
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.layouts.SideBar
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh

private const val COLOR_MODE_KEY = "app:colorMode"

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.config.initialColorMode = localStorage.getItem(COLOR_MODE_KEY)?.let { ColorMode.valueOf(it) } ?: ColorMode.DARK

    ctx.stylesheet.apply {
        // registerStyleBase("body") { Modifier.fontFamily("Roboto", "sans-serif") }
        // registerStyleBase("code, pre") { Modifier.fontFamily("Roboto Mono", "monospace") }
        registerStyleBase("body") {
            Modifier.fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
        }

        registerStyleBase("html") {
            Modifier
                .scrollPadding(top = 5.5.cssRem)
                // Kobweb enables smooth scrolling by default on link clicks, but not in general (e.g. popstate events)
                .scrollBehavior(ScrollBehavior.Smooth)
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

val LocalSideBarContent = compositionLocalOf {
    movableContentOf { _: Modifier -> }
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = ColorMode.current
        LaunchedEffect(colorMode) {
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        val sideBarContent = remember { movableContentOf { it: Modifier -> SideBar(it) } }
        Surface(SmoothColorStyle.toModifier().fillMaxWidth().minHeight(100.vh)) {
            CompositionLocalProvider(LocalSideBarContent provides sideBarContent) {
                content()
            }
        }
    }
}

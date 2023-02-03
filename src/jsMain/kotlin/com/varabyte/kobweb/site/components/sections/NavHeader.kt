package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.site.components.style.boxShadow
import com.varabyte.kobweb.site.components.widgets.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img

@Composable
private fun HomeLogo() {
    Anchor(
        href = "/",
    ) {
        Box(Modifier.margin(4.px)) {
            Img(
                "images/logo.png",
                attrs = Modifier.height(32.px).toAttrs()
            )
        }
    }
}

private fun getNavBackgroundColor(colorMode: ColorMode): CSSColorValue {
    return when (colorMode) {
        ColorMode.DARK -> rgba(0.0, 0.0, 0.0, 0.65)
        ColorMode.LIGHT -> rgba(255, 255, 255, 0.65)
    }
}

private val BUTTON_MARGIN = Modifier.margin(0.px, 10.px)

@Composable
fun NavHeader() {
    var colorMode by rememberColorMode()
    Box(
        Modifier
            .zIndex(1)
            .fillMaxWidth()
            .backgroundColor(getNavBackgroundColor(colorMode))
            .position(Position.Sticky)
            .top(0.percent)
            .backdropFilter(saturate(180.percent), blur(5.px))
            .boxShadow(colorMode),

        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier.fillMaxWidth(90.percent).margin(1.em),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeLogo()
            Spacer()
            Row(Modifier.margin(0.px, 12.px)) {
                LinkButton("https://discord.gg/5NZ2GKV5Cs", BUTTON_MARGIN, shape = ButtonShape.CIRCLE) {
                    FaDiscord()
                }
                LinkButton("https://github.com/varabyte/kobweb", BUTTON_MARGIN, shape = ButtonShape.CIRCLE) {
                    FaGithub()
                }
                ThemedButton(
                    onClick = { colorMode = colorMode.opposite() },
                    BUTTON_MARGIN,
                    shape = ButtonShape.CIRCLE
                ) {
                    when (colorMode) {
                        ColorMode.DARK -> FaSun()
                        ColorMode.LIGHT -> FaMoon()
                    }
                }
            }
        }
    }
}
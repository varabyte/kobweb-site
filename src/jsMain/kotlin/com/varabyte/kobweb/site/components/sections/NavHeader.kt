package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.navigation.Link
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.site.components.widgets.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img

@Composable
private fun HomeLogo() {
    Link(
        href = "/",
    ) {
        Box(
            Modifier.padding(4.px)
        ) {
            Img(
                "https://storage.googleapis.com/kobweb-example-cdn/logo.png",
                attrs = {
                    style {
                        height(32.px)
                    }
                }
            )
        }
    }
}

 fun getBoxShadow(colorMode: ColorMode): String {
    val colorStr = when (colorMode) {
        ColorMode.DARK -> "#eee"
        ColorMode.LIGHT -> "#111111"
    }
    return "0 0 0 0.1px $colorStr"
}

private fun getNavBackgroundColor(colorMode: ColorMode): CSSColorValue {
    return when (colorMode) {
        ColorMode.DARK -> rgba(0.0, 0.0, 0.0, 0.65)
        ColorMode.LIGHT -> rgba(255, 255, 255, 0.65)
    }
}

private val BUTTON_PADDING = Modifier.padding(0.px, 10.px)

@Composable
fun NavHeader() {
    var colorMode by rememberColorMode()
    Box(
        Modifier
            .fillMaxWidth()
            .background(getNavBackgroundColor(colorMode))
            .styleModifier {
                position(Position.Sticky)
                top(0.percent)
                property("backdrop-filter", "saturate(180%) blur(5px)")
                property("box-shadow", getBoxShadow(colorMode))
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier.fillMaxWidth(70.percent).padding(1.em),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeLogo()
            Spacer()
            Row(Modifier.padding(0.px, 12.px)) {
                LinkButton("https://discord.gg/5NZ2GKV5Cs", BUTTON_PADDING, shape = ButtonShape.CIRCLE) {
                    FaDiscord()
                }
                LinkButton("https://github.com/varabyte/kobweb", BUTTON_PADDING, shape = ButtonShape.CIRCLE) {
                    FaGithub()
                }
                ThemedButton(
                    onClick = { colorMode = colorMode.opposite() },
                    BUTTON_PADDING,
                    shape = ButtonShape.CIRCLE
                ) {
                    when (colorMode) {
                        ColorMode.DARK -> FaMoon()
                        ColorMode.LIGHT -> FaSun()
                    }
                }
            }
        }
    }
}
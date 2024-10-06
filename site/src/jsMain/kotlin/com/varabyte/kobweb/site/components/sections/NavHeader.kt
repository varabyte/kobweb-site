package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.style.dividerBoxShadow
import com.varabyte.kobweb.site.components.widgets.ButtonShape
import com.varabyte.kobweb.site.components.widgets.LinkButton
import com.varabyte.kobweb.site.components.widgets.ThemedButton
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img

val NavHeaderHeight by StyleVariable(64.px)

val NavHeaderStyle = CssStyle(extraModifier = { SmoothColorStyle.toModifier() }) {
    base {
        Modifier
            .fillMaxWidth()
            .backgroundColor(getNavBackgroundColor(colorMode))
            .position(Position.Sticky)
            .top(0.percent)
            .backdropFilter(saturate(180.percent), blur(5.px))
            .dividerBoxShadow()
            .height(NavHeaderHeight.value())
    }
}

@Composable
private fun HomeLogo() {
    Anchor(
        href = "/",
    ) {
        Box(Modifier.margin(4.px)) {
            Img(
                "/images/logo.png",
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
    var colorMode by ColorMode.currentState
    Box(NavHeaderStyle.toModifier(), contentAlignment = Alignment.Center) {
        Row(
            Modifier.fillMaxWidth(90.percent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeLogo()
            Spacer()
            Row(Modifier.margin(0.px, 12.px)) {
                LinkButton("https://github.com/varabyte/kobweb", BUTTON_MARGIN, shape = ButtonShape.CIRCLE) {
                    FaGithub()
                }
                Tooltip(ElementTarget.PreviousSibling, "Kobweb source on GitHub")

                LinkButton("https://discord.gg/5NZ2GKV5Cs", BUTTON_MARGIN, shape = ButtonShape.CIRCLE) {
                    FaDiscord()
                }
                Tooltip(ElementTarget.PreviousSibling, "Chat with us on Discord")

                ThemedButton(
                    onClick = { colorMode = colorMode.opposite },
                    BUTTON_MARGIN,
                    shape = ButtonShape.CIRCLE
                ) {
                    when (colorMode) {
                        ColorMode.DARK -> FaSun()
                        ColorMode.LIGHT -> FaMoon()
                    }
                }
                Tooltip(ElementTarget.PreviousSibling, "Toggle color mode")
            }
        }
    }
}

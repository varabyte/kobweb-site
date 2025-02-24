package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.style.CssLayer
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.site.components.style.dividerBoxShadow
import com.varabyte.kobweb.site.components.widgets.ButtonShape
import com.varabyte.kobweb.site.components.widgets.LinkButton
import com.varabyte.kobweb.site.components.widgets.ThemedButton
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import kotlin.js.json

val NavHeaderHeight by StyleVariable(64.px)

val NavHeaderBackgroundStyle = SmoothColorStyle.extendedByBase {
    Modifier
        .backgroundColor(getNavBackgroundColor(colorMode))
        .backdropFilter(saturate(180.percent), blur(5.px))
        .dividerBoxShadow()
}

val NavHeaderDarkenedBackgroundStyle = NavHeaderBackgroundStyle.extendedByBase {
    Modifier
        .backgroundColor(getNavBackgroundColor(colorMode).copyf(alpha = 0.8f))
}

val NavHeaderStyle = NavHeaderBackgroundStyle.extendedByBase {
    Modifier
        .fillMaxWidth()
        .position(Position.Sticky)
        .top(0.percent)
        .height(NavHeaderHeight.value())
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

private fun getNavBackgroundColor(colorMode: ColorMode): Color.Rgb {
    return when (colorMode) {
        ColorMode.DARK -> Colors.Black
        ColorMode.LIGHT -> Colors.White
    }.copyf(alpha = 0.65f)
}

private val BUTTON_MARGIN = Modifier.margin(0.px, 10.px)

external class PagefindUI(options: dynamic)

@CssLayer("")
val SearchStyle = CssStyle {
    cssRule(" form") {
        Modifier
            .position(Position.Relative)
    }
    cssRule(" .pagefind-ui__drawer") {
        Modifier
            .position(Position.Absolute)
            .left((-50).percent)
            .right((-50).percent)
            .maxHeight(500.px)
            .overflow { y(Overflow.Auto) }
            .backgroundColor(colorMode.toPalette().background)
            .padding(1.cssRem)
    }
}

@Composable
fun NavHeader() {
    var colorMode by ColorMode.currentState
    Box(NavHeaderStyle.toModifier().zIndex(10), contentAlignment = Alignment.Center) {
        Row(
            Modifier.fillMaxWidth(90.percent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeLogo()
            Spacer()
            Div(SearchStyle.toModifier().background(colorMode.toPalette().background).toAttrs()) {
                Div(Modifier.id("search").toAttrs())
                DisposableEffect(Unit) {
                    PagefindUI(
                        json(
                            "element" to "#search",
                            "showSubResults" to true,
                            "showImages" to false,
                            "processResult" to { result: dynamic ->
                                result.url = result.url.replace(".html", "")
                                result.sub_results.forEach { subResult ->
                                    subResult.url = subResult.url.replace(".html", "")
                                }
                                result
                            }
                        )
                    )
                    onDispose { }
                }
            }
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

package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.browser.util.invokeLater
import com.varabyte.kobweb.compose.css.CSSLengthNumericValue
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
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.site.components.sections.listing.UnstyledButtonVariant
import com.varabyte.kobweb.site.components.style.dividerBoxShadow
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.w3c.dom.events.Event
import kotlin.js.json

val NavHeaderHeight by StyleVariable<CSSLengthNumericValue>()

@InitSilk
fun initNavHeaderHeight(ctx: InitSilkContext) = with(ctx.stylesheet) {
    registerStyle("html") {
        base { Modifier.setVariable(NavHeaderHeight, 56.px) }
        Breakpoint.MD { Modifier.setVariable(NavHeaderHeight, 64.px) }
    }
}

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

val HoverBrightenStyle = CssStyle {
    val color = colorMode.toPalette().color
    base {
        Modifier.color(color.shifted(colorMode.opposite, 0.2f))
    }
    hover {
        Modifier.color(color)
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

private fun getNavBackgroundColor(colorMode: ColorMode): Color.Rgb {
    return when (colorMode) {
        ColorMode.DARK -> Colors.Black
        ColorMode.LIGHT -> Colors.White
    }.copyf(alpha = 0.65f)
}

// The nav header needs a higher z-index to be shown above elements with `position: sticky`
fun Modifier.navHeaderZIndex() = this.zIndex(10)

@JsModule("@docsearch/js")
external fun docsearch(options: dynamic)

@JsModule("preact")
external object Preact {
    fun createElement(
        type: String,
        props: dynamic,
        vararg children: dynamic
    ): dynamic
}

// Algolia search returns absolute URLs with a prefix we configured with them. However, absolute paths cause Kobweb to
// send out a request to the server each time, instead of routing instantly which happens when we navigate to an
// internal route, which we want to do instead (so, not `https://blah.com/...` but `/...`).
private fun String.removeAlgoliaPrefix() = removePrefix("https://kobweb.varabyte.com")

@Composable
fun NavHeader() {
    var colorMode by ColorMode.currentState
    LaunchedEffect(colorMode) {
        // Algolia DocSearch uses the `data-theme` attribute to determine the color mode for its default styles
        document.documentElement?.setAttribute(
            "data-theme",
            if (colorMode == ColorMode.DARK) "dark" else "light"
        )
    }

    Box(NavHeaderStyle.toModifier().navHeaderZIndex(), contentAlignment = Alignment.Center) {
        Row(
            Modifier.fillMaxWidth(90.percent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeLogo()
            Spacer()

            val ctx = rememberPageContext()
            Div(Modifier.toAttrs()) {
                DisposableEffect(Unit) {
                    fun kobwebNavigate(url: String) {
                        // The invokeLater prevents wrong scroll position - maybe a kobweb bug?
                        window.invokeLater { ctx.router.navigateTo(url) }
                    }
                    docsearch(
                        // See https://docsearch.algolia.com/docs/api (and thank you Algolia!)
                        json(
                            "container" to scopeElement,
                            "appId" to "X21XB42TEV",
                            "apiKey" to "34b8a0edc48e894f0181756e01d54e63",
                            "indexName" to "kobweb-varabyte",
                            "hitComponent" to { data: dynamic ->
                                val url = data.hit.url
                                    .unsafeCast<String>()
                                    .removeAlgoliaPrefix()
                                Preact.createElement(
                                    "a", json(
                                        "href" to url,
                                        "onClick" to { event: Event ->
                                            event.preventDefault()
                                            // The invokeLater prevents wrong scroll position - maybe a kobweb bug?
                                            kobwebNavigate(url)
                                        }),
                                    data.children
                                )
                            },
                            "navigator" to json(
                                //https://www.algolia.com/doc/ui-libraries/autocomplete/core-concepts/keyboard-navigation/#usage
                                "navigate" to { data: dynamic ->
                                    kobwebNavigate(data.itemUrl.unsafeCast<String>().removeAlgoliaPrefix())
                                }
                            )
                        )
                    )
                    onDispose { }
                }
            }

            Row(
                Modifier
                    .margin(0.px, 12.px)
                    .gap(1.cssRem)
                    .fontSize(1.5.cssRem),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Link("https://github.com/varabyte/kobweb", HoverBrightenStyle.toModifier()) {
                    FaGithub()
                }
                Tooltip(ElementTarget.PreviousSibling, "Kobweb source on GitHub", Modifier.navHeaderZIndex())

                Link("https://discord.gg/5NZ2GKV5Cs", HoverBrightenStyle.toModifier()) {
                    FaDiscord()
                }
                Tooltip(ElementTarget.PreviousSibling, "Chat with us on Discord", Modifier.navHeaderZIndex())

                Button(
                    onClick = { colorMode = colorMode.opposite },
                    modifier = HoverBrightenStyle.toModifier(),
                    variant = UnstyledButtonVariant,
                ) {
                    when (colorMode) {
                        ColorMode.DARK -> SunIcon()
                        ColorMode.LIGHT -> MoonIcon()
                    }
                }
                Tooltip(ElementTarget.PreviousSibling, "Toggle color mode", Modifier.navHeaderZIndex())
            }
        }
    }
}

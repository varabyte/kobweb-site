package com.varabyte.kobweb.site.components.widgets.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.clearFocus
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaLink
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.focus
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

private val SHOWN_LINK_OPACITY = 80.percent

@InitSilk
fun initHeaderLinkInteraction(ctx: InitSilkContext) {
    (2..6).forEach { headingLevel ->
        ctx.stylesheet.registerStyleBase("h${headingLevel}:hover > .hover-link") {
            Modifier.opacity(SHOWN_LINK_OPACITY)
        }
    }
}


val HoverLinkStyle = CssStyle {
    base {
        Modifier
            .opacity(0.percent)
            .transition(Transition.of("opacity", 0.15.s))
            .fontSize(0.8.em)
            .margin(left = 0.7.em)
    }
    link { Modifier.color(colorMode.toPalette().color) }
    visited { Modifier.color(colorMode.toPalette().color) }
    focus { Modifier.opacity(SHOWN_LINK_OPACITY) }
}

/**
 * A link icon which appears only when hovered over
 */
@Composable
fun HoverLink(href: String, modifier: Modifier = Modifier) {
    Link(href, HoverLinkStyle.toModifier().onClick {
        (document.activeElement as? HTMLElement)?.clearFocus()
    }.then(modifier)) {
        FaLink()
    }
}

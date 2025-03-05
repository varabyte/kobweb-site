package com.varabyte.kobweb.site.components.widgets.navigation

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.browser.dom.clearFocus
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaLink
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.browser.document
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.s
import org.w3c.dom.HTMLElement

val HoverLinkStyle = CssStyle {
    base {
        Modifier
            .opacity(0.percent)
            .transition(Transition.of("opacity", 0.2.s))
            .fontSize(0.8.em)
            .margin(left = 0.5.em)
    }

    cssRule(":is(:hover > *, :focus)") { Modifier.opacity(80.percent) }
}

/**
 * A link icon which appears only when hovered over
 */
@Composable
fun HoverLink(href: String, modifier: Modifier = Modifier) {
    Link(
        href,
        HoverLinkStyle.toModifier()
            .onClick { (document.activeElement as? HTMLElement)?.clearFocus() }
            .then(modifier),
        UndecoratedLinkVariant.then(UncoloredLinkVariant)
    ) {
        FaLink(Modifier.display(DisplayStyle.Inline))
    }
}

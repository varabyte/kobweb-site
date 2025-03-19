package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.fa.FaArrowRight
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.focus
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.link
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.dividerBoxShadow
import com.varabyte.kobweb.site.components.style.siteText
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms

val CtaGridItemStyle = CssStyle {
    base {
        Modifier
            .color(colorMode.toPalette().color)
            .textDecorationLine(TextDecorationLine.None)
            .transition(Transition.of("color", 50.ms))
    }

    val linkColorModifier = Modifier.color(colorMode.toPalette().link.default)
    hover { linkColorModifier }
    focus { linkColorModifier }
}

@Composable
private fun CtaGridItem(
    text: String,
    subText: String,
    href: String,
    content: @Composable () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(leftRight = 3.cssRem).dividerBoxShadow()
    ) {
        Anchor(href, attrs = CtaGridItemStyle.toAttrs()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                content()
                SpanText(
                    text,
                    Modifier.siteText(SiteTextSize.NORMAL).textAlign(TextAlign.Center)
                )
                SpanText(
                    subText,
                    Modifier
                        .siteText(SiteTextSize.NORMAL)
                        .margin(topBottom = 1.cssRem)
                        .textAlign(TextAlign.Center),
                    MutedSpanTextVariant
                )
            }
        }
    }
}

/**
 * A "call-to-action" section which includes buttons that direct the user to take actions that will help them learn
 * and support Kobweb.
 */
@Composable
fun CtaSection() {
    SimpleGrid(numColumns(1, md = 3), Modifier.margin(top = 6.cssRem).fillMaxWidth()) {
        val iconModifier = Modifier.fontSize(2.cssRem).margin(0.75.cssRem)
        CtaGridItem(
            Res.string.get_started,
            Res.string.get_started_description,
            "/docs"
        ) {
            FaArrowRight(iconModifier)
        }

        CtaGridItem(
            Res.string.star_and_contribute,
            Res.string.star_and_contribute_description,
            "https://github.com/varabyte/kobweb"
        ) {
            FaStar(iconModifier)
        }

        CtaGridItem(
            Res.string.join_the_community,
            Res.string.join_the_community_description,
            "https://discord.gg/5NZ2GKV5Cs"
        ) {
            FaDiscord(iconModifier)
        }
    }
}

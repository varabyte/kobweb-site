package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.selectors.focus
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.link
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.style.boxShadow
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px

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
    val colorMode by ColorMode.currentState
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(topBottom = 0.px, leftRight = 3.cssRem).boxShadow(colorMode)
    ) {
        Anchor(href, attrs = CtaGridItemStyle.toAttrs(), content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                content()
                SpanText(
                    text,
                    Modifier.fontSize(1.25.cssRem).textAlign(TextAlign.Center)
                )
                SpanText(
                    subText,
                    Modifier
                        .lineHeight(1.5)
                        .margin(top = 1.cssRem, bottom = 1.cssRem)
                        .textAlign(TextAlign.Center),
                    MutedSpanTextVariant
                )
            }
        })
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
            "Get started",
            "Visit our docs, with tutorials and API examples to get you up and running with your own site in no time.",
            "/docs"
        ) {
            FaArrowRight(iconModifier)
        }
        
        CtaGridItem(
            "Star & Contribute",
            "Kobweb is fully open source and community driven. We invite you to help make Kobweb the best web development framework!",
            "https://github.com/varabyte/kobweb"
        ) {
            FaStar(iconModifier)
        }

        CtaGridItem(
            "Join the Community",
            "Join our community for instant support and great conversations about the future of the Kobweb and web development using Kotlin.",
            "https://discord.gg/5NZ2GKV5Cs"
        ) {
            FaDiscord(iconModifier)
        }
    }
}

package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.navigation.Link
import com.varabyte.kobweb.silk.components.icons.fa.FaArrowRight
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.site.components.style.boxShadow
import org.jetbrains.compose.web.css.*

val CtaGridItemStyle = ComponentStyle("cta-grid-item") {
    base {
        Modifier
            .color(SilkTheme.palettes[colorMode].color)
            .textDecorationLine(TextDecorationLine.None)
            .transitionProperty("color")
            .transitionDuration(50.ms)
    }

    val linkColorModifier = Modifier.color(SilkTheme.palettes[colorMode].link.default)
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
    val colorMode by rememberColorMode()
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(topBottom = 0.px, leftRight = 3.cssRem).boxShadow(colorMode)
    ) {
        Link(href, attrs = CtaGridItemStyle.toModifier().asAttributesBuilder()) {
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
                        .opacity(70.percent)
                        .textAlign(TextAlign.Center)
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
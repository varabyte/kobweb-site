package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaArrowRight
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.navigation.Link
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.site.components.style.boxShadow
import org.jetbrains.compose.web.css.*

@Composable
private fun CtaGridItem(
    text: String,
    subText: String,
    href: String,
    content: @Composable () -> Unit = {}
) {
    val colorMode by rememberColorMode()
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(33.33.percent).height(300.px).styleModifier {
            padding(4.em)
            boxShadow(colorMode)
        }
    ) {
        content()
        Link (
            href = href
        ) {
            Text(
                text,
                Modifier.fontSize(1.25.em).styleModifier {
                    textAlign(TextAlign.Center)
                }
            )
        }
        Text(
            subText,
            Modifier.lineHeight(1.5).margin(top= 1.cssRem, bottom = 1.cssRem).styleModifier {
                opacity(70.percent)
                textAlign(TextAlign.Center)
            }
        )
    }
}

/**
 * A "call-to-action" section which includes buttons that direct the user to take actions that will help them learn
 * and support Kobweb.
 */
@Composable
fun CtaSection() {
    Box(
        Modifier.margin(top = 6.em),
        contentAlignment = Alignment.Center,
    ) {
        Row (Modifier.styleModifier {
            flexWrap(FlexWrap.Nowrap)
        }) {
            CtaGridItem("Get started", "Create a Web Compose website from scratch with Markdown support and live reloading, in under 10 seconds.", "/docs") {
                FaArrowRight(Modifier.fontSize(32.px).margin(12.px))
            }
            CtaGridItem("Star & Contribute", "Kobweb is fully open source and community driven. We invite you to help make Kobweb the best web development framework!", "https://github.com/varabyte/kobweb") {
                FaStar(Modifier.fontSize(32.px).margin(12.px))
            }
            CtaGridItem("Join the Community", "Join our community for instant support and great conversations about the future of the Kobweb.", "https://discord.gg/5NZ2GKV5Cs") {
                FaDiscord(Modifier.fontSize(32.px).margin(12.px))
            }
        }
    }
}
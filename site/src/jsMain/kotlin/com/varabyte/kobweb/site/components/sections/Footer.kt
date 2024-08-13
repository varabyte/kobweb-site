package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.boxShadow
import com.varabyte.kobweb.site.components.style.siteText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img
import com.varabyte.kobweb.silk.components.navigation.Link as SilkLink

@Composable
private fun OSSLabel() {
    Row(Modifier.margin(bottom = 32.px).flexWrap(FlexWrap.Wrap), verticalAlignment = Alignment.CenterVertically) {
        FaGithub(Modifier.margin(right = 8.px))
        SpanText("This site is ")
        SilkLink ("https://github.com/varabyte/kobweb-site", "open source")
    }
}

@Composable
private fun Logo() {
    Anchor(
        href = "/",
    ) {
        Box(Modifier.margin(4.px)) {
            Img(
                "/images/logo.png",
                attrs = {
                    style {
                        height(18.px)
                    }
                }
            )
        }
    }
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    val colorMode by ColorMode.currentState
    Box(
        Modifier.fillMaxWidth().minHeight(200.px).boxShadow(colorMode).then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxWidth(70.percent).margin(1.em),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OSSLabel()
            Logo()
            SpanText(
                "Copyright © 2024 Varabyte. All rights reserved.",
                Modifier.siteText(SiteTextSize.TINY).textAlign(TextAlign.Center),
                MutedSpanTextVariant
            )
        }
    }
}

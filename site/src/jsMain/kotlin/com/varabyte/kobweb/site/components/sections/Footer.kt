package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.style.boxShadow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import com.varabyte.kobweb.silk.components.navigation.Link as SilkLink

@Composable
private fun OssLabel() {
    Span(Modifier.margin(bottom = 2.cssRem).whiteSpace(WhiteSpace.PreWrap).textAlign(TextAlign.Center).toAttrs()) {
        FaGithub(Modifier.margin(right = 8.px))
        Text("This site is ")
        SilkLink("https://github.com/varabyte/kobweb-site", "open source")
        Text(".")
    }
}

@Composable
private fun Logo() {
    Anchor(
        href = "/",
    ) {
        Box(Modifier.margin(4.px)) {
            Img(
                "images/logo.png",
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
            OssLabel()
            Logo()
            SpanText(
                "Copyright © 2024 Varabyte. All rights reserved.",
                Modifier.fontSize(0.75.em).textAlign(TextAlign.Center),
                MutedSpanTextVariant
            )
        }
    }
}

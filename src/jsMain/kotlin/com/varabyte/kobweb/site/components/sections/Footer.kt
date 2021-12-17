package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.navigation.Link
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.site.components.style.boxShadow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img
import com.varabyte.kobweb.silk.components.navigation.Link as SilkLink

@Composable
private fun OSSLabel() {
    Row(Modifier.margin(bottom = 32.px)) {
        FaGithub(Modifier.margin(right = 8.px))
        Text ("This site is ")
        SilkLink ("https://github.com/varabyte/kobweb-site", "open source")
    }
}

@Composable
private fun Logo() {
    Link(
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
fun Footer() {
    val colorMode by rememberColorMode()
    Box(
        Modifier.fillMaxWidth().minHeight(200.px).boxShadow(colorMode),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxWidth(70.percent).margin(1.em),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OSSLabel()
            Logo()
            Text(
                "Copyright © 2021 Varabyte. All rights reserved.",
                Modifier.fontSize(0.75.em).opacity(70.percent).textAlign(TextAlign.Center)
            )
        }
    }
}
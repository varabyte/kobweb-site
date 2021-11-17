package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.site.components.layouts.PageLayout
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh

@Page
@Composable
fun DocsPage() {
    PageLayout("Docs") {
        Column (modifier = Modifier.minHeight(100.vh).padding(4.em), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Getting Started",
                Modifier.fontSize(36.px).fontWeight(FontWeight.Bolder).styleModifier {
                    textAlign(TextAlign.Center)
                })
            Text("Coming soon!",
                Modifier.fontSize(24.px).styleModifier {
                    textAlign(TextAlign.Center)
                })

        }
    }
}
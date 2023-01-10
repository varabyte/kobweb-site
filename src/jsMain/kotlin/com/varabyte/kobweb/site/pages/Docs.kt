package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.site.components.layouts.PageLayout
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun DocsPage() {
    PageLayout("Docs") {
        Column(
            modifier = Modifier.minHeight(100.vh).margin(4.em),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                "Getting Started",
                Modifier.fontSize(2.25.cssRem).fontWeight(FontWeight.Bolder)
            )

            Row(Modifier.fontSize(1.cssRem).flexWrap(FlexWrap.Wrap).margin(top = 2.cssRem)) {
                SpanText("Coming soon! Please refer to the ")
                Link("https://github.com/varabyte/kobweb/", "official project README")
                SpanText(" until this page is populated.")
            }

        }
    }
}
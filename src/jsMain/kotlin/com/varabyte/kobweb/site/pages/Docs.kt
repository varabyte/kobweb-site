package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.site.components.layouts.PageLayout
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

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

            Span(Modifier.whiteSpace(WhiteSpace.PreWrap).margin(top = 2.cssRem).toAttrs()) {
                Text("Coming soon! Please refer to the ")
                Link("https://github.com/varabyte/kobweb/", "official project README")
                Text(" until this page is populated.")
            }
        }
    }
}
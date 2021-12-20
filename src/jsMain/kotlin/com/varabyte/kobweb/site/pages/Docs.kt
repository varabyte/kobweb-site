package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.site.components.layouts.PageLayout
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun DocsPage() {
    PageLayout("Docs") {
        Column (modifier = Modifier.minHeight(100.vh).margin(4.em), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Getting Started",
                Modifier.fontSize(2.25.cssRem).fontWeight(FontWeight.Bolder).textAlign(TextAlign.Center)
            )
            Text("Coming soon!",
                Modifier.fontSize(1.5.cssRem).textAlign(TextAlign.Center)
            )

        }
    }
}
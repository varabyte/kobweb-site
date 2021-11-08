package org.example.myproject.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.Text
import org.example.myproject.components.layouts.PageLayout
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.px

@Page
@Composable
fun DocsPage() {
    PageLayout("") {
        Text(
            "Getting Started",
            modifier = Modifier.color(Color.whitesmoke).fontSize(36.px).fontWeight(FontWeight.Bold).styleModifier {
                textAlign(TextAlign.Center)
            })
        Text("Coming soon!",
            modifier = Modifier.color(Color.whitesmoke).fontSize(24.px).styleModifier {
                textAlign(TextAlign.Center)
            })

    }
}
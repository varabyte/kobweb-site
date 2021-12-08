package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

/**
 * Creates a code block that is colored by highlight.js
 */
@Composable
fun KotlinCode(code: String, modifier: Modifier = Modifier) {
    Pre(attrs = modifier.asAttributeBuilder()) {
        Code(attrs = { classes("language-kotlin") }) {
            Text(code)
        }
    }
}
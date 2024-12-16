package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.gridRow
import com.varabyte.kobweb.compose.ui.modifiers.gridTemplateRows
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.sections.Footer
import com.varabyte.kobweb.site.components.sections.NavHeader
import kotlinx.browser.document
import org.jetbrains.compose.web.css.fr
import org.jetbrains.compose.web.dom.Div

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    LaunchedEffect(title) {
        document.title = "Kobweb - $title"
    }

    val ctx = rememberPageContext()
    LaunchedEffect(ctx.route) {
        // See kobweb config in build.gradle.kts which sets up highlight.js
        js("hljs.highlightAll()")
    }

    val colorMode by ColorMode.currentState
    DisposableEffect(colorMode) {
        val styleElement = document.createElement("link").apply {
            setAttribute("type", "text/css")
            setAttribute("rel", "stylesheet")
            setAttribute("title", "hljs-style")
            setAttribute("href", "/highlight.js/styles/a11y-${colorMode.name.lowercase()}.min.css")
        }.also { document.head!!.appendChild(it) }

        onDispose { styleElement.remove() }
    }

    // Create a box with two rows: the main content (fills as much space as it can) and the footer (which reserves
    // space at the bottom). "auto" means the use the height of the row. "1fr" means give the rest of the space to
    // that row. Since this box is set to *at least* 100%, the footer will always appear at least on the bottom but
    // can be pushed further down if the first row grows beyond the page.
    Box(
        Modifier.fillMaxSize().gridTemplateRows { size(1.fr); size(auto) },
        contentAlignment = Alignment.TopCenter
    ) {
        Div(Modifier.fillMaxSize().align(Alignment.TopCenter).toAttrs()) {
            NavHeader()
            content()
        }
        // Associate the footer with the row that will get pushed off the bottom of the page if it can't fit.
        Footer(Modifier.gridRow(2, 3))
    }
}

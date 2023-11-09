package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.site.components.sections.Footer
import com.varabyte.kobweb.site.components.sections.NavHeader
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.fr

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    LaunchedEffect(title) {
        document.title = "Kobweb - $title"
    }

    LaunchedEffect(window.location.href) {
        // See kobweb config in build.gradle.kts which sets up highlight.js
        js("hljs.highlightAll()")
    }

    // Create a box with two rows: the main content (fills as much space as it can) and the footer (which reserves
    // space at the bottom). "auto" means the use the height of the row. "1fr" means give the rest of the space to
    // that row. Since this box is set to *at least* 100%, the footer will always appear at least on the bottom but
    // can be pushed further down if the first row grows beyond the page.
    Box(
        Modifier.fillMaxSize().gridTemplateRows { size(1.fr); size(auto) },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize().align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavHeader()
            content()
        }
        // Associate the footer with the row that will get pushed off the bottom of the page if it can't fit.
        Footer(Modifier.gridRow(2, 3))
    }
}

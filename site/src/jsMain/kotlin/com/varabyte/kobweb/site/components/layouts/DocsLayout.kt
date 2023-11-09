package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import kotlinx.browser.document
import com.varabyte.kobweb.site.components.sections.NavHeader
import com.varabyte.kobweb.site.components.sections.Footer

@Composable
fun DocsLayout(title: String? = null, content: @Composable () -> Unit) {
    PageLayout(title?.let { "Docs: $it" } ?: "Docs", content)
}
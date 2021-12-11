package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.*
import org.jetbrains.compose.web.css.*

val SectionStyle = ComponentStyle.base("kobweb-section") {
       Modifier.width(100.percent)
           .maxWidth(1024.px)
           .padding(top = 4.cssRem, bottom = 0.cssRem, left = 2.cssRem, right = 2.cssRem)
}

/**
 * Demarcate a new section of content, useful for pages that are a vertical list of sections.
 */
@Composable
fun Section(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    variant: ComponentVariant? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(SectionStyle.toModifier(variant).then(modifier), contentAlignment, content)
}
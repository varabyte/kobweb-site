package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.components.style.*
import org.jetbrains.compose.web.css.*

val SectionBoxStyle = ComponentStyle("kobweb-section") {
   base {
       Modifier.width(100.percent).maxWidth(1024.px).padding(top = 0.cssRem, bottom = 0.cssRem, left = 2.cssRem, right = 2.cssRem).margin(top = 4.cssRem)
   }
}

/**
 * Create a [Box] with a fancy, color aware gradient behind it.
 */
@Composable
fun SectionBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    variant: ComponentVariant? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(SectionBoxStyle.toModifier(variant).then(modifier), contentAlignment, content)
}
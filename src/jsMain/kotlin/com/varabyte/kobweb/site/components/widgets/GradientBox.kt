package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier

val GradientBoxStyle by ComponentStyle.base {
   Modifier
       .background("radial-gradient(circle at calc(60%), #0079f2 0, rgba(0, 121, 242, .5) 0, transparent 45%)")
}

/**
 * Create a [Box] with a fancy, color aware gradient behind it.
 */
@Composable
fun GradientBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    variant: ComponentVariant? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(GradientBoxStyle.toModifier(variant).then(modifier), contentAlignment, content = content)
}
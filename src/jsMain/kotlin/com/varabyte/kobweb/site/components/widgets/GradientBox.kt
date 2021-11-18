package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.TextStyle
import org.jetbrains.compose.web.css.*

val GradientBoxStyle = ComponentStyle("kobweb-gradient") { colorMode ->
   base =  Modifier.styleModifier {
       background("radial-gradient(circle at calc(60%), #0079f2 0, rgba(0, 121, 242, .5) 0, transparent 45%)")
       backgroundColor(SilkTheme.palettes[colorMode].background.toCssColor())
   }
}

@InitSilk
fun initGradientBox(ctx: InitSilkContext) {
    ctx.theme.registerComponentStyle(GradientBoxStyle)
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
    Box(GradientBoxStyle.toModifier(variant).then(modifier), contentAlignment, content)
}
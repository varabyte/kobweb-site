package com.varabyte.kobweb.site.components.widgets.code

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.dom.*

val InlineCodeStyle by ComponentStyle {
    Modifier
        .color(colorMode.toSilkPalette().color.shifted(colorMode, byPercent = -0.2f))
        .overflowWrap(OverflowWrap.BreakWord)
}

@Composable
fun InlineCode(text: String, modifier: Modifier = Modifier) {
    Code(attrs = InlineCodeStyle.toModifier().then(modifier).toAttrs()) {
        Text(text)
    }
}

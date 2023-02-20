package com.varabyte.kobweb.site.components.widgets.code

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.overflowWrap
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.theme.colors.shifted
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Text

val InlineCodeStyle = CssStyle {
    Modifier
        .color(colorMode.toPalette().color.shifted(colorMode, byPercent = -0.2f))
        .overflowWrap(OverflowWrap.BreakWord)
}

@Composable
fun InlineCode(text: String, modifier: Modifier = Modifier) {
    Code(attrs = InlineCodeStyle.toModifier().then(modifier).toAttrs()) {
        Text(text)
    }
}

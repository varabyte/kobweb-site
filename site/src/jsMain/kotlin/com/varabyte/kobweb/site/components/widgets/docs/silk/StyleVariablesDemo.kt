package com.varabyte.kobweb.site.components.widgets.docs.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Span

// Default to a debug color, so if we see it,
// that indicates we forgot to set it later.
private val BgColorVar by StyleVariable<CSSColorValue>(Colors.Magenta)

val ContainerStyle = CssStyle.base {
    Modifier.setVariable(BgColorVar, Colors.Blue)
        .backgroundColor(colorMode.opposite.toPalette().background)
        .size(14.cssRem)
}
val SquareStyle = CssStyle.base {
    Modifier.size(6.cssRem).backgroundColor(BgColorVar.value())
}
val RedSquareStyle = SquareStyle.extendedByBase {
    Modifier.setVariable(BgColorVar, Colors.Red)
}

@Composable
fun StyleVariablesDemo() {
    Box(
        ContainerStyle.toModifier(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Row {
                // 1: Color from ContainerStyle
                Box(SquareStyle.toModifier())
                // 2: Color from RedSquareStyle
                Box(RedSquareStyle.toModifier())
            }
            Row {
                // 3: Color from inline style
                Box(SquareStyle.toModifier().setVariable(BgColorVar, Colors.Green))

                Span(Modifier.setVariable(BgColorVar, Colors.Yellow).toAttrs()) {
                    // 4: Color from parent's inline style
                    Box(SquareStyle.toModifier())
                }
            }
        }
    }
}
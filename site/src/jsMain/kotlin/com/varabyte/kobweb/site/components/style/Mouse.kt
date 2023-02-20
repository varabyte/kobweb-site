package com.varabyte.kobweb.site.components.style

import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.site.util.focusable

val ClickableStyle = CssStyle(extraModifier = Modifier.focusable()) {
    hover { Modifier.cursor(Cursor.Pointer) }
}

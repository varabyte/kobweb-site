package com.varabyte.kobweb.site.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.StyleBuilder

private fun getBoxShadow(colorMode: ColorMode): String {
    val colorStr = when (colorMode) {
        ColorMode.DARK -> "#eee"
        ColorMode.LIGHT -> "#111111"
    }
    return "0 0 0 0.1px $colorStr"
}

fun StyleBuilder.boxShadow(colorMode: ColorMode) {
    property("box-shadow", getBoxShadow(colorMode))
}

fun Modifier.boxShadow(colorMode: ColorMode) = styleModifier { boxShadow(colorMode) }
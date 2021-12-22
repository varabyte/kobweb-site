package com.varabyte.kobweb.site.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode

fun Modifier.boxShadow(colorMode: ColorMode) = run {
    val colorStr = when (colorMode) {
        ColorMode.DARK -> "rgb(238, 238, 238, 0.2)"
        ColorMode.LIGHT -> "rgb(17, 17, 17, 0.2)"
    }
    boxShadow("0 0 0 1px $colorStr")
}
package com.varabyte.kobweb.site.components.style

import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxShadow
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.px

val DividerColor by StyleVariable<CSSColorValue>()

fun Modifier.dividerBoxShadow() = this.boxShadow(spreadRadius = 1.px, color = DividerColor.value())

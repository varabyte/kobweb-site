package com.varabyte.kobweb.site.util

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.tabIndex

fun Modifier.focusable() = Modifier.tabIndex(0)
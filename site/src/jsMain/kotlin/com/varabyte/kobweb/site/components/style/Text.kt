package com.varabyte.kobweb.site.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.*

enum class SiteTextSize {
    NORMAL,
    SMALL,
    TINY,
}

fun Modifier.siteText(size: SiteTextSize) =
    this
        .lineHeight(1.5)
        .fontSize(when (size) {
            SiteTextSize.NORMAL -> 1.25.cssRem
            SiteTextSize.SMALL -> 1.00.cssRem
            SiteTextSize.TINY -> 0.75.cssRem
        })

package com.varabyte.kobweb.site.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.*

enum class SiteTextSize {
    NORMAL,
    SMALL,
    CODE,
    TINY,
}

fun Modifier.siteText(size: SiteTextSize) =
    this
        .lineHeight(when (size) {
            SiteTextSize.NORMAL -> 1.75
            else -> 1.50
        })
        .fontSize(when (size) {
            SiteTextSize.NORMAL -> 1.05.cssRem
            SiteTextSize.SMALL -> 1.00.cssRem
            SiteTextSize.CODE -> 0.90.cssRem
            SiteTextSize.TINY -> 0.75.cssRem
        })

package com.varabyte.kobweb.site.components.widgets.video


import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.aspectRatio
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.percent

val VideoPlayerStyle = CssStyle {
    // Base style: Full width with 16:9 aspect ratio (standard video dimensions)
    base {
        Modifier
            .fillMaxWidth()
            .aspectRatio(width = 16, height = 9)
    }
    // For extra large screens, limit the width to prevent the video from becoming uncomfortably large
    Breakpoint.MD {
        Modifier.width(80.percent)
    }
}

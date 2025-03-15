package com.varabyte.kobweb.site.components.widgets.video


import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.aspectRatio
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Iframe

val YouTubeVideoStyle = CssStyle {
    base {
        Modifier.fillMaxWidth()
            .aspectRatio(width = 16, height = 9)
    }
    Breakpoint.ZERO {
        Modifier.width(50.percent)
    }
    Breakpoint.SM {
        Modifier.width(60.percent)
    }
    Breakpoint.MD {
        Modifier.width(70.percent)
    }
    Breakpoint.LG {
        Modifier.width(80.percent)
    }
    Breakpoint.XL {
        Modifier.width(90.percent)
    }
}

/**
 * Youtube Video Player
 */
@Composable
fun YouTubeVideo(url: String) {

    val videoId = try {
        val id = extractVideoId(url)
        id
    } catch (e: Exception) {
        console.error("parse $url video id failed", e.message)
        null
    }

    if (!videoId.isNullOrBlank()) {
        Box(
            modifier = YouTubeVideoStyle.toModifier(),
            contentAlignment = Alignment.CenterStart
        ) {
            Iframe(
                attrs = YouTubeVideoStyle.toAttrs {
                    attr("src", "https://www.youtube.com/embed/$videoId")
                }
            )
        }
    }
}

private fun extractVideoId(url: String): String? {
    if (!url.contains(".youtube.com")) return null
    val regex = Regex("v=([A-Za-z0-9_-]{11})")
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value
}
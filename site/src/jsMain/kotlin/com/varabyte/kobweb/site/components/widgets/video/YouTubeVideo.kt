package com.varabyte.kobweb.site.components.widgets.video


import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ref
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
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.asList

val YouTubeVideoStyle = CssStyle {
    base {
        Modifier.fillMaxWidth()
            .aspectRatio(width = 16, height = 9)
    }
    Breakpoint.ZERO {
        Modifier.width(85.percent)
    }
    Breakpoint.SM {
        Modifier.width(86.percent)
    }
    Breakpoint.MD {
        Modifier.width(88.percent)
    }
    Breakpoint.LG {
        Modifier.width(90.percent)
    }
    Breakpoint.XL {
        Modifier.width(100.percent)
    }
}

/**
 * Youtube Video Player
 */
@Composable
fun YouTubeVideo(url: String? = null) {
    var videoId by remember { mutableStateOf(url?.let { extractVideoId(url) }) }

    Box(
        modifier = YouTubeVideoStyle.toModifier(),
        contentAlignment = Alignment.CenterStart,
        ref = ref { element ->
            if (videoId != null) return@ref
            try {
                var currentElement = element.parentElement
                while (currentElement != null && videoId == null) {
                    val links = currentElement.querySelectorAll("a")
                    links.asList().forEach { node ->
                        if (node is HTMLAnchorElement && node.href.contains("youtube.com")) {
                            extractVideoId(node.href)?.let {
                                videoId = it
                                return@forEach
                            }
                        }
                    }
                    currentElement = currentElement.parentElement
                }
            } catch (e: Exception) {
                console.error("Error finding YouTube links: ${e.message}")
            }
        }
    ) {
        if (videoId != null) {
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
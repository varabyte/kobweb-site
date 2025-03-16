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
    // Base style: Full width with 16:9 aspect ratio (standard video dimensions)
    base {
        Modifier.fillMaxWidth()
            .aspectRatio(width = 16, height = 9)
    }
    // For mobile and tablet screens (up to large breakpoint),
    // use full width to maximize video visibility on smaller screens
    (Breakpoint.ZERO until Breakpoint.LG) {
        Modifier.width(100.percent)
    }
    // For extra large screens, use 90% width to prevent the video
    // from becoming uncomfortably large and maintain
    Breakpoint.XL {
        Modifier.width(90.percent)
    }
}

/**
 * Youtube Video Player
 *
 * @param url The full YouTube URL to play (e.g. `https://www.youtube.com/watch?v=...`).
 *    If incorrectly formatted, the player will be left blank. If null, then the player will search
 *    its siblings for a YouTube URL, so that you can have a direct link to a video followed by
 *    an embedded video player that will just work automatically.
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
                // Only check the immediate parent
                element.parentElement?.let { parent ->
                    val links = parent.querySelectorAll("a")
                    links.asList().forEach { node ->
                        if (node is HTMLAnchorElement && node.href.contains("youtube.com")) {
                            extractVideoId(node.href)?.let {
                                videoId = it
                                return@forEach
                            }
                        }
                    }
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
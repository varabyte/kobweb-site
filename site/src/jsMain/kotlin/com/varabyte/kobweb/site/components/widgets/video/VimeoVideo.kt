package com.varabyte.kobweb.site.components.widgets.video


import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.dom.Iframe

/**
 * Youtube Video Player
 *
 * @param url The full Vimeo URL to play (e.g. `https://player.vimeo.com/video/....`). If incorrectly formatted, no
 *   element will be composed and an error will be logged to the console.
 */
@Composable
fun VimeoVideo(url: String) {
    val videoId = remember {
        extractVideoId(url)
            .also {
                if (it == null) {
                    console.error("Could not extract Vimeo video ID from URL: $url")
                }
            }
    } ?: return

    Box(
        modifier = VideoPlayerStyle.toModifier(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Iframe(
            attrs = Modifier.fillMaxSize().toAttrs {
                attr("src", "https://player.vimeo.com/video/$videoId")
            }
        )
    }
}

private fun extractVideoId(url: String): String? {
    if (!url.contains(".vimeo.com")) return null
    val regex = Regex("video/([A-Za-z0-9_-]+)")
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value
}
package com.varabyte.kobweb.site.components.widgets.video


import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.height
import com.varabyte.kobweb.compose.css.width
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Iframe

/**
 * Youtube Video Player
 */
@Composable
fun YoutubeVideo(url: String) {

    val videoId = try {
        val id = extractYoutubeId(url)
        console.log("Parse: $url Success! Id is: $id")
        id
    } catch (e: Exception) {
        console.error("Parse: $url Failed!", e.message)
        null
    }

    if (!videoId.isNullOrBlank()) {
        val breakpoint = rememberBreakpoint()
        val (videoWidth, videoHeight) = when (breakpoint) {
            Breakpoint.ZERO, Breakpoint.SM -> 426 to 240  // 16:9 ratio for small screens
            Breakpoint.MD -> 768 to 432    // 16:9 ratio for medium screens
            Breakpoint.LG -> 854 to 480  // 16:9 ratio for large screens
            Breakpoint.XL -> 854 to 480 // 16:9 ratio for extra large screens
        }
        Box(
            modifier = Modifier.padding(top = 0.cssRem),
            contentAlignment = Alignment.CenterStart
        ) {
            Iframe(
                attrs = {
                    width(videoWidth)
                    height(videoHeight)
                    attr("src", "https://www.youtube.com/embed/$videoId")
                    attr("frameborder", "0")
                    attr(
                        "allow",
                        "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    )
                    attr("referrerpolicy", "strict-origin-when-cross-origin")
                    attr("allowfullscreen", "true")
                }
            )
        }
    }
}

/**
 * Parse youtube url video id
 */
private fun extractYoutubeId(url: String): String? {
    val regex = Regex(
        pattern = """^(?:https?://)?(?:www\.)?(?:youtube\.com/(?:watch\?v=|embed/)|youtu\.be/)([A-Za-z0-9_-]{11}).*""",
        option = RegexOption.IGNORE_CASE
    )
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value
}
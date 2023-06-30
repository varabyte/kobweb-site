package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.widgets.Section
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Video

/**
 * A section that demonstrates what the Kobweb CLI behavior looks like.
 */
@Composable
fun CliSection() {
    Section(Modifier.displayIfAtLeast(Breakpoint.MD)) {
        H2 {
            Text("Kobweb CLI")
        }
        SpanText(
            "Kobweb CLI provides commands to handle the tedious parts of building a Compose for Web app, including project setup and configuration",
            Modifier.lineHeight(1.5).fontSize(1.25.cssRem).textAlign(TextAlign.Center),
            MutedSpanTextVariant
        )

        Box(
            Modifier.padding(top = 2.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Video(attrs = {
                attr("width", 900.px.toString())
                attr("height", 432.px.toString())
                attr("controls", "")
            }) {
                Source(attrs = {
                    attr("src", "videos/kobweb-cli.mp4")
                    attr("type", "video/mp4")
                })
            }
        }
    }
}

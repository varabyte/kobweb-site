package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIf
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.site.components.widgets.Section
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Video

/**
 * A section that demonstrates what the Kobweb CLI behavior looks like.
 */
@Composable
fun CliSection() {
    Section(Modifier.displayIf(Breakpoint.MD)) {
        Row (
            horizontalArrangement = Arrangement.Center
        ) {
            Box (contentAlignment = Alignment.Center) {
                H2 {
                    Text("Kobweb CLI")
                }
                Text(
                    "Kobweb CLI provides commands to handle the tedious parts of building a Web Compose app, including project setup and configuration",
                    Modifier.lineHeight(1.5).fontSize(1.25.cssRem).opacity(70.percent).textAlign(TextAlign.Center)
                )
            }
            Box (
                Modifier.padding(top = 2.cssRem),
                contentAlignment = Alignment.Center
            ) {
                Video(attrs = {
                    attr("width", 900.px.toString())
                    attr("height", 432.px.toString())
                    attr("controls", "")
                }) {
                    Source(attrs = {
                        attr("src", "images/kobweb-cli.mp4")
                        attr("type", "video/mp4")
                    })
                }
            }
        }
    }
}
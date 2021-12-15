package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.site.components.widgets.Section
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Video

/**
 * A section that demonstrates what the Kobweb CLI behavior looks like.
 */
@Composable
fun CliSection() {
    Section {
        Row (
            horizontalArrangement = Arrangement.Center
        ) {
            Box (contentAlignment = Alignment.Center) {
                Text(
                    "Kobweb CLI",
                    Modifier.fontSize(48.px).fontWeight(FontWeight.Bold).textAlign(TextAlign.Center)
                )
                Br {  }
                Text(
                    "Kobweb CLI provides commands to handle the parts of building a Web Compose app that are less glamorous including project setup and configuration",
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
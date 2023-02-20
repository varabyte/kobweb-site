package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.height
import com.varabyte.kobweb.compose.css.width
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
<<<<<<< HEAD:site/src/jsMain/kotlin/com/varabyte/kobweb/site/components/sections/home/CliSection.kt
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
=======
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
>>>>>>> 487ee04 (Update kobweb to v0.13.6):src/jsMain/kotlin/com/varabyte/kobweb/site/components/sections/home/CliSection.kt
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
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
<<<<<<< HEAD:site/src/jsMain/kotlin/com/varabyte/kobweb/site/components/sections/home/CliSection.kt
<<<<<<< HEAD:site/src/jsMain/kotlin/com/varabyte/kobweb/site/components/sections/home/CliSection.kt
            "Kobweb CLI provides commands to handle the tedious parts of building a Compose HTML app, including project setup and configuration",
            Modifier.lineHeight(1.5).fontSize(1.25.cssRem).textAlign(TextAlign.Center),
=======
            "Kobweb CLI provides commands to handle the tedious parts of building a Compose for Web app, including project setup and configuration",
=======
            "Kobweb CLI provides commands to handle the tedious parts of building a Compose HTML app, including project setup and configuration",
>>>>>>> c7678b3 (Update stale name references to Compose HTML):src/jsMain/kotlin/com/varabyte/kobweb/site/components/sections/home/CliSection.kt
            Modifier.lineHeight(1.5).siteText(SiteTextSize.NORMAL).textAlign(TextAlign.Center),
>>>>>>> 3f2898e (Making progress on showing a sidebar):src/jsMain/kotlin/com/varabyte/kobweb/site/components/sections/home/CliSection.kt
            MutedSpanTextVariant
        )

        Box(
            Modifier.padding(top = 2.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Video(attrs = {
                width(900.px)
                height(432.px)
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

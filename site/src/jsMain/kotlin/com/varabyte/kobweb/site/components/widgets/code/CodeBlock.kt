package com.varabyte.kobweb.site.components.widgets.code

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

val CodeBlockStyle by ComponentStyle {
    // For some reason I'm not smart enough to figure out, code blocks are messing up the layout on mobile - they lay
    // themselves out too wide and break out of the central column. Here, we just constrain them to whatever the
    // view width is, until we are on desktop and the column is no longer so small as to cause an issue.
    base {
        Modifier
            .maxWidth(90.vw).fillMaxWidth()
            .borderRadius(10.px)
            .overflow { x(Overflow.Auto) }
            .siteText(SiteTextSize.SMALL)
    }
    Breakpoint.MD { Modifier.maxWidth(100.percent) }
}


/**
 * Creates a code block that is colored by highlight.js
 */
// Note: To enable this widget to work, we needed to add highlight.js support to this project. See the kobweb
// block in our build.gradle.kts file to see how this was done.
@Composable
fun CodeBlock(code: String, modifier: Modifier = Modifier, lang: String? = null) {
    Pre(Modifier.fillMaxWidth().classNames("wtf").toAttrs()) {
        Code(attrs = CodeBlockStyle.toModifier().then(modifier).classNames(lang?.let { "language-$it"} ?: "nohighlight").toAttrs()) {
            Text(code)
        }
    }
}

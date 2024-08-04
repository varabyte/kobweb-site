package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.descendants
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.widgets.DynamicToc
import com.varabyte.kobweb.site.components.widgets.getHeadings
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

val ArticleStyle = CssStyle {
    descendants("ul", "ol", "menu") {
        Modifier
            .listStyle(ListStyleType.Unset)
            .paddingInline(2.cssRem, 2.cssRem)
    }
    // This is an alternative to scrollPadding on the html
//    descendants("h2", "h3", "h4", "h5", "h6") {
//        Modifier.scrollMargin(top = 5.5.cssRem)
//    }
}

@Composable
fun DocsLayout(title: String? = null, content: @Composable () -> Unit) {
    PageLayout(title?.let { "Docs: $it" } ?: "Docs") {
        Row(
            Modifier
                .margin(leftRight = autoLength)
                .maxWidth(80.cssRem)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Div(
                Modifier
                    .position(Position.Sticky)
                    .top(5.cssRem)
                    .toAttrs()
            ) {
                SideBar(
                    Modifier
                        .width(18.cssRem)
                        .fillMaxHeight()
                )
            }
            var mainElement by remember { mutableStateOf<HTMLElement?>(null) }
            Main(
                ArticleStyle.toModifier()
                    .padding(2.cssRem)
                    .fillMaxWidth()
                    .toAttrs {
                        ref { mainElement = it; onDispose { } }
                    }
            ) {
                Article {
                    content()
                }
            }
            Div(
                Modifier
                    .position(Position.Sticky)
                    .top(5.cssRem)
                    .toAttrs()
            ) {
                if (mainElement == null) return@Div
                DynamicToc(
                    headings = mainElement!!.getHeadings(),
                    modifier = Modifier.width(16.cssRem)
                )
            }
        }
    }
}

@Composable
fun SideBar(modifier: Modifier = Modifier) {
    Ul(
        Modifier
            .listStyle(ListStyleType.None)
            .then(modifier)
            .toAttrs()
    ) {
        Li { Text("Concepts") }
        Li { Text("Components") }
        Li { Text("Derived Values") }
    }
}

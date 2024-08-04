package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.selectors.descendants
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.widgets.DynamicToc
import com.varabyte.kobweb.site.components.widgets.getHeadings
import com.varabyte.kobweb.site.model.listing.ArticleHandle
import com.varabyte.kobweb.site.model.listing.SITE_LISTING
import com.varabyte.kobweb.site.model.listing.findArticle
import com.varabyte.kobwebx.markdown.markdown
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

fun PageContext.RouteInfo.toArticleHandle(): ArticleHandle? {
    return SITE_LISTING.findArticle(path)
}

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
fun DocsLayout(content: @Composable () -> Unit) {
    val ctx = rememberPageContext()

    val articleHandle = ctx.markdown?.let { ctx.route.toArticleHandle() }
    val title = if (articleHandle != null) {
        "Docs - ${articleHandle.category.title} - ${articleHandle.article.title}"
    } else "Docs"

    PageLayout(title) {
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
                    .minWidth(0.px)
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
                    .displayIfAtLeast(Breakpoint.LG)
                    .position(Position.Sticky)
                    .top(5.cssRem)
                    .toAttrs()
            ) {
                DynamicToc(
                    headings = mainElement?.getHeadings().orEmpty(),
                    modifier = Modifier.width(16.cssRem)
                )
            }
        }
    }
}


@Composable
fun SideBar(modifier: Modifier = Modifier) {
    //ListingSideBar()
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

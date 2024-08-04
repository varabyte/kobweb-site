package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.selectors.descendants
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.sections.listing.ListingSideBar
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
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

val ArticleStyle = CssStyle {
    descendants("ul", "ol", "menu") {
        Modifier
            .listStyle(ListStyleType.Unset)
            .paddingInline(2.cssRem, 2.cssRem)
    }
}

fun PageContext.RouteInfo.toArticleHandle(): ArticleHandle? {
    return SITE_LISTING.findArticle(path)
}

@Composable
fun DocsLayout(content: @Composable () -> Unit) {
         val ctx = rememberPageContext()

        val articleHandle = ctx.markdown?.let { ctx.route.toArticleHandle() }
        val title = if (articleHandle != null) {
            "Docs - ${articleHandle.category.title} - ${articleHandle.article.title}"
        } else "Docs"

//        PageLayout(title) {
//            Row(Modifier.fillMaxWidth().maxWidth(800.px).gap(1.cssRem).fillMaxHeight().siteText(SiteTextSize.NORMAL)) {
//                Column(Modifier.fillMaxSize()) {
//                    if (articleHandle != null) {
//                        H1(Modifier.align(Alignment.CenterHorizontally).toAttrs()) {
//                            Text(articleHandle.article.title)
//                        }
//                    }
//                    content()
//                }
//            }
//        }

    PageLayout(title) {
        Row(
            Modifier
                .margin(leftRight = autoLength)
                .maxWidth(80.cssRem)
                .fillMaxSize()
                .siteText(SiteTextSize.NORMAL)
            ,
            horizontalArrangement = Arrangement.Center,
        ) {
            Div(
                Modifier
                    .position(Position.Sticky)
                    .top(5.cssRem)
                    .toAttrs()
            ) {
                ListingSideBar()
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

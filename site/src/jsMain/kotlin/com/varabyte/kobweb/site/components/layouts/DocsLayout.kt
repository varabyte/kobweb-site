package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.layout.HorizontalDivider
import com.varabyte.kobweb.silk.components.navigation.LinkVars
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssLayer
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.common.SmoothColorTransitionDurationVar
import com.varabyte.kobweb.silk.style.selectors.descendants
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.vars.color.BorderColorVar
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.site.components.sections.*
import com.varabyte.kobweb.site.components.sections.listing.ListingSidebar
import com.varabyte.kobweb.site.components.sections.listing.MobileLocalNav
import com.varabyte.kobweb.site.components.widgets.DynamicToc
import com.varabyte.kobweb.site.components.widgets.getHeadings
import com.varabyte.kobweb.site.model.listing.ArticleHandle
import com.varabyte.kobweb.site.model.listing.SITE_LISTING
import com.varabyte.kobweb.site.model.listing.findArticle
import com.varabyte.kobwebx.markdown.markdown
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

fun PageContext.RouteInfo.toArticleHandle(): ArticleHandle? {
    return SITE_LISTING.findArticle(path)
}

@CssLayer("component-styles") // Allow variants to override these styles
val ArticleStyle = CssStyle {
    base {
        Modifier
            .padding(top = 3.cssRem, leftRight = 1.25.cssRem, bottom = 2.cssRem)
    }
    Breakpoint.MD {
        Modifier.padding(2.cssRem)
    }

    descendants("ul", "ol", "menu") {
        Modifier
            .listStyle(ListStyleType.Revert)
            .paddingInline(2.cssRem)
    }
    descendants("table") {
        Modifier
            .display(DisplayStyle.Block)
            .width(Width.MaxContent)
            .maxWidth(100.percent)
            .overflow(Overflow.Auto)
            .margin(bottom = 1.cssRem)
    }
    descendants("table td", "table th") {
        Modifier
            .border(1.px, LineStyle.Solid, BorderColorVar.value())
            .padding(6.px, 13.px)
    }
    descendants("table tr:nth-child(2n)") {
        Modifier
            .backgroundColor(colorMode.toPalette().background.shifted(colorMode, 0.05f))
    }
    descendants("code") {
        Modifier
            .whiteSpace(WhiteSpace.BreakSpaces)
            .overflowWrap(OverflowWrap.BreakWord)
            .padding(2.px, 4.px)
            .borderRadius(4.px)
            .backgroundColor(
                colorMode.toPalette().background.shifted(colorMode, if (colorMode.isLight) 0.1f else 0.18f)
            )
            .transition(Transition.of("background-color", SmoothColorTransitionDurationVar.value()))
    }
    descendants("pre") {
        Modifier
            .borderRadius(0.5.cssRem)
    }
    descendants("pre > code") {
        Modifier
            // Alternatively, don't set white-space for "code" above. OR modify the above selector to only apply to
            // "code" blocks not inside "pre" block
            .whiteSpace(WhiteSpace.Inherit)
    }

    // Use the default color for visited links, otherwise internal links look out of place
    // Many docs site, but not all (e.g. MDN), also do this
    descendants("a:any-link") {
        Modifier.color(LinkVars.DefaultColor.value())
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
        MobileLocalNav()
        Row(
            Modifier
                .margin(leftRight = autoLength) // Centers content
                .fillMaxSize()
                .maxWidth(80.cssRem),
            horizontalArrangement = Arrangement.Center,
        ) {
            Div(
                Modifier
                    .displayIfAtLeast(Breakpoint.MD)
                    .position(Position.Sticky)
                    .top(5.cssRem)
                    .toAttrs()
            ) {
                ListingSidebar(
                    SITE_LISTING,
                    Modifier
                        .padding(top = 2.cssRem, left = 2.cssRem)
                        .width(15.cssRem)
                        .fillMaxHeight()
                )
            }
            var mainElement by remember { mutableStateOf<HTMLElement?>(null) }
            Main(
                ArticleStyle.toModifier()
                    .minWidth(0.px)
                    .fillMaxWidth()
                    .toAttrs {
                        ref { mainElement = it; onDispose { } }
                    }
            ) {
                Article {
                    if (articleHandle != null) {
                        H1 {
                            Text(articleHandle.article.title)
                        }
                    }
                    content()
                    HorizontalDivider(Modifier.fillMaxWidth().margin(bottom = 1.5.cssRem).opacity(0.75))
                    if (articleHandle == null) return@Article
                    val articleIndex = articleHandle.subcategory.articles.indexOf(articleHandle.article)
                    val subCategoryIndex = articleHandle.category.subcategories.indexOf(articleHandle.subcategory)
                    val categoryIndex = SITE_LISTING.indexOf(articleHandle.category)

                    val prev = articleHandle.subcategory.articles.getOrNull(articleIndex - 1)
                        ?: articleHandle.category.subcategories.getOrNull(subCategoryIndex - 1)?.articles?.first()
                        ?: SITE_LISTING.getOrNull(categoryIndex - 1)?.subcategories?.first()?.articles?.first()
                    val next = articleHandle.subcategory.articles.getOrNull(articleIndex + 1)
                        ?: articleHandle.category.subcategories.getOrNull(subCategoryIndex + 1)?.articles?.first()
                        ?: SITE_LISTING.getOrNull(categoryIndex + 1)?.subcategories?.first()?.articles?.first()

                    PaginationNav1(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav2(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav3(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav4(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav5(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav6(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav7(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav8(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                    PaginationNav9(prev, next)
                    HorizontalDivider(Modifier.fillMaxWidth().margin(topBottom = 1.25.cssRem).opacity(0.75))
                }
            }
            Div(
                Modifier
                    .displayIfAtLeast(Breakpoint.LG)
                    .position(Position.Sticky)
                    .padding(top = 2.cssRem)
                    .top(5.cssRem)
                    .toAttrs()
            ) {
                SpanText("On this page", Modifier.fontWeight(FontWeight.Bold))

                // Should `IntersectionObserver.Options` implement equals() so that it doesn't have to be remembered?
                val options = remember {
                    val top = 64 // Height of the top nav bar
                    val bottom = top + 125
                    val height = document.documentElement!!.clientHeight
                    IntersectionObserver.Options(rootMargin = "-${top}px 0% ${bottom - height}px")
                }
                DynamicToc(
                    headings = mainElement?.getHeadings().orEmpty(),
                    intersectionObserverOptions = options,
                    modifier = Modifier
                        .width(16.cssRem)
                        .margin(top = 0.25.cssRem)
                )
            }
        }
    }
}

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
import com.varabyte.kobweb.site.components.sections.PaginationNav
import com.varabyte.kobweb.site.components.sections.listing.ListingSidebar
import com.varabyte.kobweb.site.components.sections.listing.MobileLocalNav
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
import com.varabyte.kobweb.site.components.widgets.DynamicToc
import com.varabyte.kobweb.site.components.widgets.getHeadings
import com.varabyte.kobweb.site.model.listing.*
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
            .padding(top = 3.cssRem, leftRight = 1.cssRem, bottom = 2.cssRem)
            .siteText(SiteTextSize.NORMAL)
    }
    Breakpoint.MD {
        Modifier.padding(2.cssRem)
    }

    descendants("ul", "ol", "menu") {
        Modifier
            .listStyle(ListStyleType.Revert)
            .paddingInline(start = 1.5.cssRem)
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
            .fontSize(90.percent)
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

    descendants(*((2..6).map { level -> "h$level" }.toTypedArray())) {
        // By making the header full width, it means when the user mouses over the entire line they'll see the link
        Modifier.fillMaxWidth()

        // This is an alternative to scrollPadding on the html
//        Modifier.scrollMargin(top = 5.5.cssRem)

    }
}

@Composable
fun DocsLayout(content: @Composable () -> Unit) {
    val ctx = rememberPageContext()

    val articleHandle = ctx.markdown?.let { ctx.route.toArticleHandle() }
    val title = buildList {
        if (articleHandle != null) {
            articleHandle.article.title.takeIf { it.isNotEmpty() }?.let { add(it) }
            articleHandle.subcategory.title.takeIf { it.isNotEmpty() }?.let { add(it) }
            articleHandle.category.title.takeIf { it.isNotEmpty() }?.let { add(it) }
        }

        if (this.isEmpty()) {
            add("Docs")
        }
    }.joinToString(" - ")

    PageLayout(title) {
        MobileLocalNav()
        Row(
            Modifier
                .margin(leftRight = autoLength) // Centers content
                .padding(bottom = 1.cssRem)
                // The following is a reasonably large width which gives our content about as much room to grow on wide
                // monitors as you would get in a GitHub README
                .maxWidth(90.cssRem),
            horizontalArrangement = Arrangement.Center,
        ) {
            val topOffset = 5.cssRem
            Div(
                Modifier
                    .displayIfAtLeast(Breakpoint.MD)
                    .position(Position.Sticky)
                    .top(topOffset)
                    .height(100.vh - topOffset)
                    .toAttrs()
            ) {
                ListingSidebar(
                    SITE_LISTING,
                    Modifier
                        .padding(top = 2.cssRem, left = 1.5.cssRem)
                        .width(16.cssRem)
                        .fillMaxHeight()
                        .overflow { y(Overflow.Auto) }
                        .overscrollBehavior(OverscrollBehavior.Contain)
                )
            }
            var mainElement by remember { mutableStateOf<HTMLElement?>(null) }
            Main(
                ArticleStyle.toModifier()
                    // Ensure height is greater than sidebar because otherwise sidebar isn't properly positioned
                    .minHeight(100.vh - (topOffset / 2))
                    .minWidth(0.px)
                    .fillMaxWidth()
                    .toAttrs {
                        ref { mainElement = it; onDispose { } }
                    }
            ) {
                Article {
                    if (articleHandle != null) {
                        H1 {
                            Text(articleHandle.article.titleOrFallback)
                        }
                    }
                    content()

                    if (articleHandle == null) return@Article

                    val (prev, next) = SITE_LISTING.findArticleNeighbors(articleHandle)
                    PaginationNav(prev, next, Modifier.margin(top = 3.cssRem))
                }
            }
            Div(
                Modifier
                    .displayIfAtLeast(Breakpoint.LG)
                    .position(Position.Sticky)
                    .padding(top = 2.cssRem)
                    .top(topOffset)
                    .toAttrs()
            ) {
                // Should `IntersectionObserver.Options` implement equals() so that it doesn't have to be remembered?
                val options = remember {
                    val top = 64 // Height of the top nav bar
                    val bottom = top + 125
                    val height = document.documentElement!!.clientHeight
                    IntersectionObserver.Options(rootMargin = "-${top}px 0% ${bottom - height}px")
                }
                val headings = remember(mainElement) {
                    mainElement?.getHeadings().orEmpty()
                }
                if (headings.isNotEmpty()) {
                    SpanText("On this page", Modifier.fontWeight(FontWeight.Bold))
                }
                DynamicToc(
                    headings = headings,
                    intersectionObserverOptions = options,
                    modifier = Modifier
                        .width(16.cssRem)
                        .margin(top = 0.25.cssRem)
                        .maxHeight(70.vh)
                        .overflow { y(Overflow.Auto) }
                        .overscrollBehavior(OverscrollBehavior.Contain)
                )
            }
        }
    }
}

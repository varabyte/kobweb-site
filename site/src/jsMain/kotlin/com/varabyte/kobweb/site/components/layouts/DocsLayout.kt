package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.RouteInfo
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaSquareArrowUpRight
import com.varabyte.kobweb.silk.components.layout.DividerVars
import com.varabyte.kobweb.silk.components.layout.HorizontalDivider
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.LinkVars
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssLayer
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.common.SmoothColorTransitionDurationVar
import com.varabyte.kobweb.silk.style.selectors.descendants
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.vars.color.BorderColorVar
import com.varabyte.kobweb.silk.style.vars.size.FontSizeVars
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.site.components.sections.PaginationNav
import com.varabyte.kobweb.site.components.sections.listing.ListingLinkVariant
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
import org.w3c.dom.HTMLHeadingElement

fun RouteInfo.toArticleHandle(): ArticleHandle? {
    return SITE_LISTING.findArticle(path)
}

@CssLayer("component-styles") // Allow variants to override these styles
val ArticleStyle = CssStyle {
    base {
        Modifier
            .padding(leftRight = 1.cssRem, bottom = 2.cssRem)
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
    descendants("code:not(pre *)") {
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
    }
}

@Composable
private fun EditPageLink(pageRoute: String, modifier: Modifier = Modifier) {
    val activeArticle = SITE_LISTING.findArticle(pageRoute)?.article ?: return

    val githubPageLink = remember {
        val githubLinkBase =
            "https://github.com/varabyte/kobweb-site/edit/main/site/src/jsMain/resources/markdown"

        "${githubLinkBase}/${activeArticle.filePath}"
    }
    Link(
        githubPageLink,
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant).then(ListingLinkVariant),
        modifier = Modifier.fontSize(95.percent).then(modifier)
    ) {
        Row(
            Modifier.gap(0.4.cssRem),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FaGithub()
            SpanText("Edit this page")
            FaSquareArrowUpRight()
        }
    }
}

@InitRoute
fun initDocsLayout(ctx: InitRouteContext) {
    val articleHandle = ctx.markdown?.let { ctx.route.toArticleHandle() }
    ctx.data.add(PageLayoutData(
        title = articleHandle?.article?.titleOrFallback ?: "Docs",
        description = ctx.markdown?.frontMatter?.get("description")?.singleOrNull(),
    ))
}

@Composable
@Layout(".components.layouts.PageLayout")
fun DocsLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val articleHandle = ctx.markdown?.let { ctx.route.toArticleHandle() }
    val breadcrumbs = articleHandle?.article?.breadcrumbs

    // We surface the parent title for the page as a meta tag so that the search engine we use can surface it as useful
    // metadata.
    if (breadcrumbs != null) {
        DisposableEffect(breadcrumbs) {
            val head = document.head!!
            val metaName = "algolia-docsearch-lvl0"
            val meta = head.querySelector("meta[name='$metaName']") ?: document.createElement("meta").apply {
                setAttribute("name", metaName)
                head.appendChild(this)
            }
            // Although we originally intended to render breadcrumbs as a full path, e.g. "Concepts > Presentation",
            // we'll stick with a simple single breadcrumb for now, e.g. "Presentation", as it subjectively looks
            // cleaner. We can always revisit this decision and change the code back to
            // `breadcrumbs.joinToString(" > ")` if we change our minds.
            meta.setAttribute("content", breadcrumbs.lastOrNull().orEmpty())
            onDispose { head.removeChild(meta) }
        }
    }

    MobileLocalNav()
    Row(
        Modifier
            .margin(leftRight = autoLength) // Centers content
            .padding(bottom = 1.cssRem)
            // The following is a reasonably large width which gives our content about as much room to grow on wide
            // monitors as you would get in a GitHub README
            .maxWidth(85.cssRem),
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
            var headings by remember(ctx.route.path) { mutableStateOf(emptyList<HTMLHeadingElement>()) }
            // Fetch headings only once elements are added to the DOM
            registerRefScope(ref(mainElement, ctx.route.path) {
                headings = mainElement?.getHeadings().orEmpty()
            })
            val options = run {
                val top = 64 // Height of the top nav bar
                val bottom = top + 125
                val height = document.documentElement!!.clientHeight
                IntersectionObserver.Options(rootMargin = "-${top}px 0% ${bottom - height}px")
            }
            Column(Modifier.fontSize(FontSizeVars.SM.value()).gap(0.25.cssRem)) {
                if (headings.isNotEmpty()) {
                    SpanText("On this page", Modifier.fontWeight(FontWeight.Bold))
                }
                DynamicToc(
                    headings = headings,
                    intersectionObserverOptions = options,
                    modifier = Modifier
                        .width(16.cssRem)
                        .maxHeight(70.vh)
                        .overflow { y(Overflow.Auto) }
                        .overscrollBehavior(OverscrollBehavior.Contain)
                        .scrollbarWidth(ScrollbarWidth.Thin)
                )

                if (headings.isNotEmpty()) {
                    HorizontalDivider(Modifier.setVariable(DividerVars.Length, 100.percent))
                }

                EditPageLink(pageRoute = ctx.route.path, Modifier.margin(top = 0.4.cssRem))
            }
        }
    }
}

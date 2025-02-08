package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.extendedBy
import com.varabyte.kobweb.silk.style.selectors.anyLink
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.site.model.listing.Article
import com.varabyte.kobweb.site.model.listing.Category
import com.varabyte.kobweb.site.model.listing.Subcategory
import com.varabyte.kobweb.site.model.listing.titleOrSubcategoryTitle
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLElement

val ListingIndentVar by StyleVariable<Int>()

// TODO: is this nicer if it's a cssRule inside DynamicTocStyle?
val ListingElementStyle = CssStyle.base {
    Modifier
        .padding {
            left(calc { num(ListingIndentVar.value()) * 1.cssRem })
            topBottom(0.5.cssRem)
        }
}

val ListingLinkVariant = UndecoratedLinkVariant.extendedBy {
    anyLink {
        Modifier.color(SilkTheme.palettes[colorMode].color.shifted(colorMode.opposite, 0.15f))
    }
    hover {
        Modifier.color(SilkTheme.palettes[colorMode].color.shifted(colorMode, 0.15f))
    }
}

// This needs to be global so that it can be saved between different pages, which each recreate the sidebar
private var SidebarScroll: Double? = null

@Composable
fun ListingSidebar(
    categories: List<Category>,
    modifier: Modifier = Modifier,
) {
    val ctx = rememberPageContext()
    var navElement: HTMLElement? by remember { mutableStateOf(null) }
    Nav(modifier.toAttrs()) {
        registerRefScope(ref { element ->
            navElement = element
            if (SidebarScroll != null) {
                element.scrollTop = SidebarScroll!!
                // if the user clicks a link and then presses `tab`, the focus should move to the next link
                document
                    .querySelector("nav a[href=\"${ctx.route.path}\"]")
                    .unsafeCast<HTMLElement?>()?.focus()
            }
        })
        Ul {
            categories.forEach { category ->
                Li {
                    SpanText(
                        text = category.title,
                        modifier = Modifier
                            .fontSize(115.percent)
                            .fontWeight(FontWeight.Bold)
                    )
                    category.subcategories.forEach { subcategory ->
                        SubcategoryContent(
                            subcategory,
                            Modifier.margin(leftRight = 0.125.cssRem),
                            onLinkClick = { SidebarScroll = navElement!!.scrollTop }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubcategoryContent(subcategory: Subcategory, modifier: Modifier = Modifier, onLinkClick: () -> Unit) {
    val ctx = rememberPageContext()

    @Composable
    fun LinkFor(article: Article, modifier: Modifier = Modifier) {
        Link(
            path = article.route,
            text = article.titleOrSubcategoryTitle,
            modifier = modifier
                .onClick { onLinkClick() }
                .display(DisplayStyle.Block)
                .thenIf(article.route == ctx.route.path) {
                    Modifier
                        .color(Colors.DodgerBlue)
                        .fontWeight(FontWeight.Bold)
                },
            variant = ListingLinkVariant
        )
    }

    Li(ListingElementStyle.toModifier().fontSize(0.875.cssRem).then(modifier).toAttrs()) {
        val firstArticle = subcategory.articles.first()
        val subcategoryModifier = Modifier.fontWeight(FontWeight.Bold).margin(bottom = 0.5.cssRem)
        if (firstArticle.title.isNotEmpty()) {
            SpanText(text = subcategory.title, subcategoryModifier)
        } else {
            LinkFor(firstArticle, subcategoryModifier)
        }

        Ul(
            Modifier
                .margin(left = 0.25.cssRem)
                .padding(leftRight = 0.75.cssRem)
                .borderLeft(1.px, LineStyle.Solid, Colors.Gray.copyf(alpha = 0.5f))
                .toAttrs()
        ) {
            subcategory.articles.forEach { article ->
                if (article.title.isEmpty()) return@forEach // Link already added to parent category

                Li(ListingElementStyle.toModifier().fillMaxWidth().toAttrs()) {
                    LinkFor(article, Modifier.padding(left = 0.5.cssRem))
                }
            }
        }
    }
}

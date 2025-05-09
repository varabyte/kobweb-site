package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ScrollbarWidth
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.PageContext
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
import com.varabyte.kobweb.site.model.listing.titleOrSubcategory
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Ul

val ListingIndentVar by StyleVariable<Int>()

// TODO: is this nicer if it's a cssRule inside DynamicTocStyle?
val ListingElementStyle = CssStyle.base {
    Modifier
        .padding {
            left(calc { num(ListingIndentVar.value()) * 1.cssRem })
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

@Composable
private fun LinkFor(ctx: PageContext, article: Article, title: String, onLinkClick: () -> Unit, modifier: Modifier = Modifier) {
    Link(
        path = article.route,
        text = title,
        modifier = modifier
            .onClick { onLinkClick() }
            .display(DisplayStyle.Block)
            .borderLeft(if (article.title.isNotEmpty()) 1.px else 0.px, LineStyle.Solid, Colors.Transparent)
            .thenIf(article.route == ctx.route.path) {
                Modifier
                    .borderLeft { color(Color.currentColor) }
                    .color(Colors.DodgerBlue)
                    .fontWeight(FontWeight.Bold)
            },
        variant = ListingLinkVariant
    )
}

@Composable
fun ListingSidebar(
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onLinkClick: () -> Unit = {},
) {
    val ctx = rememberPageContext()
    Nav(modifier.scrollbarWidth(ScrollbarWidth.Thin).toAttrs()) {
        Ul {
            categories.forEach { category ->
                Li {
                    val titleShouldBeLink = category.subcategories.firstOrNull()?.let { firstSubcategory ->
                        firstSubcategory.title.isEmpty() && firstSubcategory.articles.firstOrNull()?.title == ""
                    } ?: false

                    val titleModifier = Modifier
                        .fontSize(115.percent)
                        .fontWeight(FontWeight.Bold)
                    if (!titleShouldBeLink) {
                        SpanText(
                            text = category.title,
                            modifier = titleModifier,
                        )
                    } else {
                        LinkFor(
                            ctx,
                            category.subcategories.first().articles.first(),
                            title = category.title,
                            modifier = Modifier
                                .fontSize(115.percent)
                                .fontWeight(FontWeight.Bold),
                            onLinkClick = onLinkClick,
                        )
                    }
                    category.subcategories.forEach { subcategory ->
                        SubcategoryContent(
                            ctx,
                            subcategory,
                            onLinkClick,
                            Modifier.margin(leftRight = 0.125.cssRem, topBottom = 0.5.cssRem),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubcategoryContent(ctx: PageContext, subcategory: Subcategory, onLinkClick: () -> Unit, modifier: Modifier = Modifier) {
    @Composable
    fun LinkFor(ctx: PageContext, article: Article, modifier: Modifier = Modifier) {
        LinkFor(ctx, article, article.titleOrSubcategory, onLinkClick, modifier)
    }

    Li(ListingElementStyle.toModifier().fontSize(0.875.cssRem).then(modifier).toAttrs()) {
        val firstArticle = subcategory.articles.first()
        val subcategoryModifier = Modifier.fontWeight(FontWeight.Bold).margin(bottom = 0.5.cssRem)
        if (firstArticle.title.isNotEmpty()) {
            SpanText(text = subcategory.title, subcategoryModifier)
        } else {
            LinkFor(ctx, firstArticle, subcategoryModifier)
        }

        Ul(
            Modifier
                .margin(left = 0.25.cssRem)
                .padding(right = 0.75.cssRem)
                .borderLeft(1.px, LineStyle.Solid, Colors.Gray.copyf(alpha = 0.5f))
                .toAttrs()
        ) {
            subcategory.articles.forEach { article ->
                if (article.title.isEmpty()) return@forEach // Link already added to parent category

                Li(
                    ListingElementStyle.toModifier()
                        .fillMaxWidth()
                        .toAttrs()
                ) {
                    LinkFor(
                        ctx,
                        article,
                        Modifier
                            .padding { left(1.25.cssRem); topBottom(0.25.cssRem) }
                            .margin { left((-1).px); topBottom(0.5.cssRem) },
                    )
                }
            }
        }
    }
}

package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.browser.util.setTimeout
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.functions.calc
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
import com.varabyte.kobweb.site.model.listing.Category
import com.varabyte.kobweb.site.model.listing.Subcategory
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLElement
import kotlin.time.Duration.Companion.milliseconds

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

@Composable
fun ListingSidebar(
    categories: List<Category>,
    modifier: Modifier = Modifier,
) {
    Nav(modifier.toAttrs()) {
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
                        SubcategoryContent(subcategory, Modifier.margin(leftRight = 0.125.cssRem))
                    }
                }
            }
        }
    }
}

@Composable
private fun SubcategoryContent(subcategory: Subcategory, modifier: Modifier = Modifier) {
    val ctx = rememberPageContext()
    Li(ListingElementStyle.toModifier().fontSize(0.875.cssRem).then(modifier).toAttrs()) {
        SpanText(text = subcategory.title)

        Ul(
            Modifier
                .margin(top = 0.5.cssRem, left = 0.25.cssRem)
                .padding(leftRight = 0.75.cssRem)
                .borderLeft(1.px, LineStyle.Solid, Colors.Gray.copyf(alpha = 0.5f))
                .toAttrs()
        ) {
            subcategory.articles.forEach { article ->
                Li(ListingElementStyle.toModifier().fillMaxWidth().toAttrs()) {
                    Link(
                        path = article.route,
                        text = article.title,
                        Modifier
                            .onClick {
                                // if the user clicks a link and then presses `tab`, the focus
                                // should move to the next link
                                // + hack to try to wait until page is loaded
                                window.setTimeout(50.milliseconds) {
                                    document
                                        .querySelector("nav a[href=\"${ctx.route.path}\"]")
                                        .unsafeCast<HTMLElement?>()?.focus()
                                }
                            }
                            .padding(left = 0.5.cssRem)
                            .display(DisplayStyle.Block)
                            .thenIf(article.route == ctx.route.path) {
                                Modifier
                                    .color(Colors.DodgerBlue)
                                    .fontWeight(FontWeight.Bold)
                            },
                        variant = ListingLinkVariant
                    )
                }
            }
        }
    }
}

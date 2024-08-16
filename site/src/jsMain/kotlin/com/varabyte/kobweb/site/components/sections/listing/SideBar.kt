package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.layouts.toArticleHandle
import com.varabyte.kobweb.site.components.style.ClickableStyle
import com.varabyte.kobweb.site.model.listing.SITE_LISTING
import com.varabyte.kobweb.site.util.focusable
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Hr

val ListingSideBarStyle = CssStyle.base {
    Modifier.fillMaxHeight().padding(20.px).minWidth(200.px).width(10.percent).userSelect(UserSelect.None)
}

sealed interface ListingStyleKind : ComponentKind

val ListingStyle = CssStyle<ListingStyleKind>(extraModifier = Modifier.focusable()) {
    base { Modifier.margin(0.5.cssRem) }
}

val ListingCategoryVariant = ListingStyle.addVariant {
    base { Modifier.fontWeight(FontWeight.Bold) }
}

val ListingSubcategoryVariant = ListingStyle.addVariant {
    base { Modifier.textDecorationLine(TextDecorationLine.Underline) }
}

val ListingArticleVariant = ListingStyle.addVariant {
    base { Modifier.fontStyle(FontStyle.Italic) }
}

@Composable
fun ListingSideBar() {
    val ctx = rememberPageContext()
    var selectedCategory by remember {
        mutableStateOf(ctx.route.toArticleHandle()?.category ?: SITE_LISTING.first())
    }

    Column(ListingSideBarStyle.toModifier()) {
        SITE_LISTING.forEach { category ->
            SpanText(
                category.title,
                ClickableStyle.toModifier().then(ListingStyle.toModifier(ListingCategoryVariant))
                    .onClick { selectedCategory = category })
        }
        Hr(Modifier.fillMaxWidth().toAttrs())
        Column(Modifier.overflow { y(Overflow.Auto) }) {
            selectedCategory.subcategories.forEach { subcategory ->
                if (subcategory.title.isNotEmpty()) {
                    SpanText(subcategory.title.uppercase(), ListingStyle.toModifier(ListingSubcategoryVariant))
                }
                subcategory.articles.forEach { article ->
                    SpanText(
                        article.title,
                        ClickableStyle.toModifier()
                            .then(ListingStyle.toModifier(ListingArticleVariant))
                            .onClick { ctx.router.tryRoutingTo(article.route) }
                    )
                }
            }
        }
    }
}

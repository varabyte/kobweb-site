package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.functions.min
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.rowClasses
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.ArrowBackIcon
import com.varabyte.kobweb.silk.components.icons.ArrowForwardIcon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
import com.varabyte.kobweb.site.model.listing.Article
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

// Inspired mostly by https://playwright.dev/docs/intro & https://docs.astro.build/en/install-and-setup/

val PaginationNavBorderColor by StyleVariable<CSSColorValue>()
val PaginationNavItemStyle = CssStyle {
    base {
        Modifier
            .setVariable(PaginationNavBorderColor, if (colorMode.isDark) Colors.DimGray else Colors.LightGray)
    }
    hover {
        Modifier
            .setVariable(PaginationNavBorderColor, Colors.DodgerBlue)
    }
}

@Composable
fun PaginationNav(prev: Article?, next: Article?, modifier: Modifier = Modifier) {
    @Composable
    fun NavItem(article: Article, label: String, modifier: Modifier = Modifier, icon: @Composable () -> Unit) {
        Link(
            article.route,
            PaginationNavItemStyle.toModifier()
                .then(modifier)
                .rowClasses(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                )
                .padding(0.5.cssRem, 0.75.cssRem)
                .gap(0.5.cssRem)
                .borderRadius(0.4.cssRem)
                .border(1.px, LineStyle.Solid, PaginationNavBorderColor.value())
                .transition(Transition.of("border-color", 0.25.s))
                .overflowWrap(OverflowWrap.Anywhere),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            icon()
            Div {
                SpanText(
                    label,
                    Modifier
                        .fontWeight(FontWeight.SemiBold)
                        .display(DisplayStyle.Block)
                        .siteText(SiteTextSize.TINY)
                )
                SpanText(
                    article.title,
                    Modifier
                        .color(Colors.DodgerBlue)
                        .fontWeight(FontWeight.Bold)
                        .siteText(SiteTextSize.SMALL)
                        .transition(Transition.of("color", 0.25.s))
                )
            }
        }
    }

    Div(
        Modifier
            .gap(1.cssRem)
            .grid {
                columns {
                    repeat(autoFit) {
                        minmax(min(16.cssRem, 100.percent), 1.fr)
                    }
                }
            }.then(modifier).toAttrs()
    ) {
        if (prev != null) {
            NavItem(
                prev,
                label = "Previous",
            ) { ArrowBackIcon() }
        } else Div()
        if (next != null) {
            NavItem(
                next,
                label = "Next",
                modifier = Modifier
                    .flexDirection(FlexDirection.RowReverse) // Make icon appear after text
                    .textAlign(TextAlign.End)
            ) { ArrowForwardIcon() }
        } else Div()
    }
}

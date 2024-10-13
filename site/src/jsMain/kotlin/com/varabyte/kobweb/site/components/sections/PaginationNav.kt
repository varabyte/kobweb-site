package com.varabyte.kobweb.site.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.rowClasses
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.icons.ArrowBackIcon
import com.varabyte.kobweb.silk.components.icons.ArrowForwardIcon
import com.varabyte.kobweb.silk.components.icons.ChevronLeftIcon
import com.varabyte.kobweb.silk.components.icons.ChevronRightIcon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.site.model.listing.Article
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

// NOTE: Only designed for desktop & dark mode

val TextColor by StyleVariable<CSSColorValue>(Colors.DodgerBlue)
val BorderColor by StyleVariable<CSSColorValue>(Colors.White.copyf(alpha = 0.5f))
val BackgroundVar by StyleVariable<CSSColorValue>()
val BackgroundColoredVar by StyleVariable<CSSColorValue>()
val PaginationNavStyle = CssStyle {
    hover {
        Modifier
            .setVariable(TextColor, Colors.DodgerBlue.shifted(colorMode, 0.1f))
            .setVariable(BorderColor, Colors.DodgerBlue)
            .setVariable(BackgroundVar, Color.rgb(39, 39, 42))
            .setVariable(BackgroundColoredVar, Colors.DodgerBlue.copyf(alpha = 0.2f))
    }
}

@Composable
fun PaginationNav1(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, label: String) {
        Link(
            article.route,
            PaginationNavStyle.toModifier(),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            SpanText(label, Modifier.display(DisplayStyle.Block).fontSize(0.8125.cssRem).lineHeight(2))
            SpanText(
                article.title,
                Modifier
                    .color(TextColor.value())
                    .fontWeight(FontWeight.Bold)
                    .transition(Transition.of("color", 0.25.s))
            )
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween)) {
        if (prev != null) {
            NavItem(prev, "Previous")
        }
        if (next != null) {
            NavItem(next, "Next")
        }
    }
}

@Composable
fun PaginationNav2(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, label: String, prev: Boolean) {
        Link(
            article.route,
            PaginationNavStyle.toModifier(),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            SpanText(
                label,
                Modifier
                    .fontWeight(FontWeight.SemiBold)
                    .display(DisplayStyle.Block)
                    .fontSize(0.8125.cssRem)
                    .lineHeight(2)
                    .thenIf(!prev, Modifier.textAlign(TextAlign.End))
            )
            SpanText(
                article.title,
                Modifier
                    .color(TextColor.value())
                    .fontWeight(FontWeight.Bold)
                    .transition(Transition.of("color", 0.25.s))
            )
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween)) {
        if (prev != null) {
            NavItem(prev, "Previous", prev = true)
        }
        if (next != null) {
            NavItem(next, "Next", prev = false)
        }
    }
}

@Composable
fun PaginationNav3(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, prev: Boolean) {
        val label = if (prev) "Previous" else "Next"
        Link(
            article.route,
            PaginationNavStyle.toModifier(),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            SpanText(
                label,
                Modifier
                    .fontWeight(FontWeight.SemiBold)
                    .color(SilkTheme.palette.color.toRgb().copyf(alpha = 0.8f))
                    .display(DisplayStyle.Block)
                    .fontSize(0.8125.cssRem)
                    .lineHeight(2)
                    .thenIf(!prev, Modifier.textAlign(TextAlign.End))
            )
            Row(
                Modifier.gap(0.3.cssRem).color(TextColor.value()).fontSize(115.percent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (prev) {
                    ChevronLeftIcon(Modifier.margin(top = 4.px, left = (-4).px))
                }
                SpanText(
                    article.title,
                    Modifier
                        .fontWeight(FontWeight.Bold)
                        .transition(Transition.of("color", 0.25.s))
                )
                if (!prev) {
                    ChevronRightIcon(Modifier.margin(top = 4.px, right = (-4).px))
                }
            }
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween)) {
        if (prev != null) {
            NavItem(prev, prev = true)
        }
        if (next != null) {
            NavItem(next, prev = false)
        }
    }
}

@Composable
fun PaginationNav4(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, prev: Boolean) {
        val label = if (prev) "Previous" else "Next"
        Link(
            article.route,
            PaginationNavStyle.toModifier(),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            SpanText(
                label,
                Modifier
                    .display(DisplayStyle.Block)
                    .fontSize(0.8125.cssRem)
                    .lineHeight(2)
                    .thenIf(!prev, Modifier.textAlign(TextAlign.End))
            )
            Row(
                Modifier.gap(0.3.cssRem).color(TextColor.value()).fontSize(115.percent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (prev) {
                    ChevronLeftIcon(Modifier.margin(top = 4.px, left = (-4).px))
                }
                SpanText(
                    article.title,
                    Modifier
                        .fontWeight(FontWeight.Bold)
                        .transition(Transition.of("color", 0.25.s))
                )
                if (!prev) {
                    ChevronRightIcon(Modifier.margin(top = 4.px, right = (-4).px))
                }
            }
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween)) {
        if (prev != null) {
            NavItem(prev, prev = true)
        }
        if (next != null) {
            NavItem(next, prev = false)
        }
    }
}

@Composable
fun PaginationNav5(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, prev: Boolean) {
        val label = if (prev) "Previous" else "Next"
        Link(
            article.route,
            PaginationNavStyle.toModifier().borderRadius(0.4.cssRem)
                .transition(Transition.of("border-color", 0.25.s))
                .padding(0.75.cssRem, 1.cssRem)
                .border(1.px, LineStyle.Solid, BorderColor.value())
                .flexGrow(1)
                .thenIf(!prev, Modifier.textAlign(TextAlign.End)),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            SpanText(
                label,
                Modifier
                    .display(DisplayStyle.Block)
                    .fontSize(0.8125.cssRem)
                    .lineHeight(2)
            )
            Row(
                Modifier.fillMaxWidth().gap(0.3.cssRem).color(TextColor.value()).fontSize(115.percent),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (prev) Arrangement.Start else Arrangement.End
            ) {
                if (prev) {
                    ChevronLeftIcon(Modifier.margin(top = 4.px, left = (-4).px))
                }
                SpanText(
                    article.title,
                    Modifier
                        .fontWeight(FontWeight.Bold)
                        .transition(Transition.of("color", 0.25.s))
                )
                if (!prev) {
                    ChevronRightIcon(Modifier.margin(top = 4.px, right = (-4).px))
                }
            }
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween).gap(1.cssRem)) {
        if (prev != null) {
            NavItem(prev, prev = true)
        }
        if (next != null) {
            NavItem(next, prev = false)
        }
    }
}

@Composable
fun PaginationNav6(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, prev: Boolean) {
        val label = if (prev) "Previous" else "Next"
        Link(
            article.route,
            PaginationNavStyle.toModifier().borderRadius(0.4.cssRem)
                .rowClasses(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (prev) Arrangement.Start else Arrangement.End
                )
                .gap(.5.cssRem)
                .transition(Transition.of("border-color", 0.25.s))
                .padding(0.5.cssRem, 1.cssRem)
                .border(1.px, LineStyle.Solid, BorderColor.value())
                .flexGrow(1)
                .thenIf(!prev, Modifier.textAlign(TextAlign.End)),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            if (prev) {
                ArrowBackIcon(Modifier.margin(top = 4.px, left = (-4).px))
            }
            Div {
                SpanText(
                    label,
                    Modifier
                        .display(DisplayStyle.Block)
                        .fontSize(0.875.cssRem)
                        .lineHeight(2)
                )
                SpanText(
                    article.title,
                    Modifier
                        .fontWeight(FontWeight.Bold)
                        .transition(Transition.of("color", 0.25.s))
                )
            }
            if (!prev) {
                ArrowForwardIcon(Modifier.margin(top = 4.px, right = (-4).px))
            }
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween).gap(1.cssRem)) {
        if (prev != null) {
            NavItem(prev, prev = true)
        }
        if (next != null) {
            NavItem(next, prev = false)
        }
    }
}

@Composable
fun PaginationNav7(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, prev: Boolean) {
        val label = if (prev) "Previous" else "Next"
        Link(
            article.route,
            PaginationNavStyle.toModifier().borderRadius(0.4.cssRem)
                .rowClasses(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (prev) Arrangement.Start else Arrangement.End
                )
                .gap(.5.cssRem)
                .transition(Transition.of("border-color", 0.25.s))
                .padding(0.5.cssRem, 1.cssRem)
                .border(1.px, LineStyle.Solid, BorderColor.value())
                .flexGrow(1)
                .thenIf(!prev, Modifier.textAlign(TextAlign.End)),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            if (prev) {
                ArrowBackIcon(Modifier.margin(top = 4.px, left = (-4).px))
            }
            Div {
                SpanText(
                    label,
                    Modifier
                        .fontWeight(FontWeight.SemiBold)
                        .display(DisplayStyle.Block)
                        .fontSize(0.875.cssRem)
                        .lineHeight(2)
                )
                SpanText(
                    article.title,
                    Modifier
                        .color(TextColor.value())
                        .fontSize(115.percent)
                        .fontWeight(FontWeight.Bold)
                        .transition(Transition.of("color", 0.25.s))
                )
            }
            if (!prev) {
                ArrowForwardIcon(Modifier.margin(top = 4.px, right = (-4).px))
            }
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween).gap(1.cssRem)) {
        if (prev != null) {
            NavItem(prev, prev = true)
        }
        if (next != null) {
            NavItem(next, prev = false)
        }
    }
}


@Composable
fun PaginationNav8(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, prev: Boolean) {
        Link(
            article.route,
            PaginationNavStyle.toModifier().borderRadius(0.5.cssRem - 2.px)
                .rowClasses(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (prev) Arrangement.Start else Arrangement.End
                )
                .backgroundColor(BackgroundVar.value())
                .fontWeight(500)
                .fontSize(0.875.cssRem)
                .lineHeight(1.25.cssRem)
                .gap(0.5.cssRem)
                .transition(Transition.of("border-color", 0.25.s))
                .padding(0.5.cssRem, 1.cssRem)
                .border(1.px, LineStyle.Solid, Color.rgb(67, 67, 72)) //Color("#27272a")
                .thenIf(!prev, Modifier.textAlign(TextAlign.End)),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            if (prev) {
                ChevronLeftIcon(Modifier.size(1.cssRem))
            }
            Text(article.title)
            if (!prev) {
                ChevronRightIcon(Modifier.size(1.cssRem))
            }
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween)) {
        if (prev != null) {
            NavItem(prev, prev = true)
        }
        if (next != null) {
            NavItem(next, prev = false)
        }
    }
}


@Composable
fun PaginationNav9(
    prev: Article?,
    next: Article?,
) {
    @Composable
    fun NavItem(article: Article, prev: Boolean) {
        Link(
            article.route,
            PaginationNavStyle.toModifier().borderRadius(0.5.cssRem - 2.px)
                .rowClasses(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (prev) Arrangement.Start else Arrangement.End
                )
                .color(Colors.DodgerBlue)
                .backgroundColor(BackgroundColoredVar.value())
                .fontWeight(500)
                .fontSize(1.cssRem)
                .lineHeight(1.25.cssRem)
                .gap(0.5.cssRem)
                .transition(Transition.of("border-color", 0.25.s))
                .padding(0.5.cssRem, 1.cssRem)
                .thenIf(!prev, Modifier.textAlign(TextAlign.End)),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            if (prev) {
                ChevronLeftIcon(Modifier.size(1.cssRem))
            }
            Text(article.title)
            if (!prev) {
                ChevronRightIcon(Modifier.size(1.cssRem))
            }
        }
    }

    Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween)) {
        if (prev != null) {
            NavItem(prev, prev = true)
        }
        if (next != null) {
            NavItem(next, prev = false)
        }
    }
}

package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.setTimeout
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonSize
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.icons.ChevronDownIcon
import com.varabyte.kobweb.silk.components.icons.ChevronRightIcon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.selectors.active
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.widgets.CollapsibleHeightBox
import com.varabyte.kobweb.site.components.widgets.DynamicTocElementStyle
import com.varabyte.kobweb.site.components.widgets.DynamicTocLinkVariant
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

@Composable
fun NavSideBar(
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
                        SubcategoryContent(subcategory)
                    }
                }
            }
        }
    }
}

val UnsetButtonSize = ButtonSize(
    fontSize = "unset".unsafeCast<CSSLengthValue>(),
    height = "unset".unsafeCast<CSSLengthValue>(),
    horizontalPadding = 0.px
)

val UnstyledButtonVariant = ButtonStyle.addVariant {
    base {
        Modifier
            .color(CSSColor.Unset)
            .backgroundColor(BackgroundColor.Unset)
            .fontWeight(FontWeight.Normal)
            .lineHeight(LineHeight.Unset)
            .borderRadius(0.px)
    }
    hover {
        Modifier.backgroundColor(BackgroundColor.Unset)
    }
    active {
        Modifier.backgroundColor(BackgroundColor.Unset)
    }
}

@Composable
private fun SubcategoryContent(subcategory: Subcategory) {
    val ctx = rememberPageContext()
    Li(DynamicTocElementStyle.toModifier().fontSize(0.875.cssRem).toAttrs()) {
        // TODO: This state should be somehow preserved when navigating between pages
        var isOpen by remember { mutableStateOf(true) }
        Button(
            onClick = {
                if (subcategory.articles.none { it.route == ctx.route.path }) {
                    isOpen = !isOpen
                }
            },
            modifier = Modifier.fillMaxWidth().textAlign(TextAlign.Start),
            variant = UnstyledButtonVariant,
            size = UnsetButtonSize,
        ) {
            SpanText(text = subcategory.title, Modifier.weight(1))
            if (isOpen)
                ChevronDownIcon()
            else
                ChevronRightIcon()
        }
        CollapsibleHeightBox(isOpen, Modifier.fillMaxWidth()) {
            Ul(
                Modifier
                    .fillMaxWidth()
                    .margin(top = 0.5.cssRem, left = 0.25.cssRem)
                    .padding(leftRight = 0.75.cssRem)
                    .borderLeft(1.px, LineStyle.Solid, Colors.Gray.copyf(alpha = 0.5f))
                    .toAttrs()
            ) {
                subcategory.articles.forEach { article ->
                    Li(DynamicTocElementStyle.toModifier().fillMaxWidth().toAttrs()) {
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
                                .onFocusIn { isOpen = true }
                                .display(DisplayStyle.Block)
                                .thenIf(article.route == ctx.route.path) {
                                    Modifier
                                        .color(Colors.DodgerBlue)
                                        .fontWeight(FontWeight.Bold)
                                },
                            variant = DynamicTocLinkVariant
                        )
                    }
                }
            }
        }
    }
}

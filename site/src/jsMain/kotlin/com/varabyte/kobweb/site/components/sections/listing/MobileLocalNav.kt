package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.icons.ChevronDownIcon
import com.varabyte.kobweb.silk.components.icons.ChevronRightIcon
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.*
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.silk.style.selectors.active
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.theme.name
import com.varabyte.kobweb.site.components.sections.NavHeaderBackgroundStyle
import com.varabyte.kobweb.site.components.sections.NavHeaderDarkenedBackgroundStyle
import com.varabyte.kobweb.site.components.sections.NavHeaderHeight
import com.varabyte.kobweb.site.components.sections.navHeaderZIndex
import com.varabyte.kobweb.site.model.listing.SITE_LISTING
import kotlinx.browser.document
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

val MobileNavHeight by StyleVariable<CSSLengthNumericValue>()

private val MaxMobileBreakpoint = Breakpoint.MD

@InitSilk
fun initMobileNavHeight(ctx: InitSilkContext) = with(ctx.stylesheet) {
    registerStyle("html") {
        base { Modifier.setVariable(MobileNavHeight, 2.75.cssRem) }
        MaxMobileBreakpoint { Modifier.setVariable(MobileNavHeight, 0.px) }
    }
}

val UnsizedButtonStyle = ButtonStyle.addVariantBase {
    Modifier
        .height(Height.Inherit)
        .minWidth(MinWidth.Inherit)
        .lineHeight(LineHeight.Inherit)
        .fontSize(100.percent)
        .padding(0.px)
        .margin(0.px)
}

val UnstyledButtonVariant = UnsizedButtonStyle.extendedBy {
    base {
        Modifier
            .backgroundColor(Colors.Transparent) // TODO: should this be BackgroundColor.Unset?
            .fontWeight(FontWeight.Inherit)
    }
    cssRules(hover, active) {
        Modifier.backgroundColor(Colors.Transparent)
    }
}

val FixedNoScrollStyle = CssStyle {
    until(MaxMobileBreakpoint) {
        Modifier.overflow { y(Overflow.Hidden) }
    }
}

@Composable
fun MobileLocalNav() {
    var open by remember { mutableStateOf(false) }
    Column(
        NavHeaderBackgroundStyle.toModifier()
            .displayUntil(MaxMobileBreakpoint)
            .position(Position.Sticky)
            .top(NavHeaderHeight.value())
            .fillMaxWidth()
            .padding(leftRight = 1.cssRem)
            .navHeaderZIndex()
    ) {
        Button(
            onClick = { open = !open },
            modifier = Modifier.fillMaxWidth().height(MobileNavHeight.value()),
            variant = UnstyledButtonVariant
        ) {
            // Even though the button content is a row, this row is needed for vertical alignment with the text & icon
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(topBottom = 0.75.cssRem)
                    .gap(0.5.cssRem)
                    .alignItems(AlignItems.Stretch), // Nicer vertical alignment
            ) {
                if (open) ChevronDownIcon() else ChevronRightIcon()
                SpanText("Menu")
            }
        }
    }
    if (open) {
        DisposableEffect(Unit) {
            // Prevent scrolling under the menu from the top of the screen (which the menu div does not cover)
            document.body?.addClass(FixedNoScrollStyle.name)
            onDispose { document.body?.removeClass(FixedNoScrollStyle.name) }
        }
        Div(
            NavHeaderDarkenedBackgroundStyle.toModifier()
                .displayUntil(MaxMobileBreakpoint)
                .position(Position.Fixed)
                .top(NavHeaderHeight.value() + MobileNavHeight.value())
                .bottom(0.px)
                .fillMaxWidth()
                .padding(1.cssRem)
                .overflow(Overflow.Hidden, Overflow.Auto)
                .navHeaderZIndex()
                .toAttrs()
        ) {
            ListingSidebar(SITE_LISTING, onLinkClick = { open = false })
        }
    }
}

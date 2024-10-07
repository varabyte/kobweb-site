package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.MinWidth
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.icons.ChevronDownIcon
import com.varabyte.kobweb.silk.components.icons.ChevronRightIcon
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.addVariantBase
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.silk.style.cssRules
import com.varabyte.kobweb.silk.style.extendedBy
import com.varabyte.kobweb.silk.style.selectors.active
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.sections.NavHeaderBackgroundStyle
import com.varabyte.kobweb.site.components.sections.NavHeaderDarkenedBackgroundStyle
import com.varabyte.kobweb.site.components.sections.NavHeaderHeight
import com.varabyte.kobweb.site.model.listing.SITE_LISTING
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div

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


@Composable
fun MobileLocalNav() {
    var open by remember { mutableStateOf(false) }
    Column(
        NavHeaderBackgroundStyle.toModifier()
            .position(Position.Fixed)
            .displayUntil(Breakpoint.MD)
            .top(NavHeaderHeight.value())
            .thenIf(open, NavHeaderDarkenedBackgroundStyle.toModifier().bottom(0.px))
            .fillMaxWidth()
            .padding(leftRight = 1.cssRem, topBottom = 0.5.cssRem)
    ) {
        Button(
            onClick = { open = !open },
            modifier = Modifier.fillMaxWidth(),
            variant = UnstyledButtonVariant
        ) {
            // Even though the button content is a row, this row is needed for vertical alignment with the text & icon
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(topBottom = 0.25.cssRem)
                    .gap(0.5.cssRem)
                    .alignItems(AlignItems.Stretch), // Nicer vertical alignment
            ) {
                if (open) ChevronDownIcon() else ChevronRightIcon()
                SpanText("Menu")
            }
        }
        if (open) {
            Div(
                Modifier
                    .fillMaxWidth()
                    .padding(topBottom = 1.cssRem, leftRight = 0.5.cssRem)
                    .overflow(Overflow.Hidden, Overflow.Auto)
                    // Note: this only disables scroll-through when the nav content is actually scrollable,
                    // which is not currently the case, though likely will happen when more content is added
                    .overscrollBehavior(OverscrollBehavior.Contain)
                    .bottom(0.px)
                    .toAttrs()
            ) {
                ListingSidebar(SITE_LISTING)
            }
        }
    }
}

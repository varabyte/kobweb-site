package com.varabyte.kobweb.site.components.sections.listing

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Height
import com.varabyte.kobweb.compose.css.LineHeight
import com.varabyte.kobweb.compose.css.MinWidth
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.icons.HamburgerIcon
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.addVariantBase
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.sections.NavHeaderBackgroundStyle
import com.varabyte.kobweb.site.components.sections.NavHeaderHeight
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

val UnsizedButtonStyle = ButtonStyle.addVariantBase {
    Modifier
        .height(Height.Inherit)
        .minWidth(MinWidth.Inherit)
        .lineHeight(LineHeight.Inherit)
        .fontSize(100.percent)
        .padding(0.px)
        .margin(0.px)
}

val UnstyledButtonVariant = UnsizedButtonStyle.extendedByBase {
    Modifier
        .backgroundColor(Colors.Transparent)
        .fontWeight(FontWeight.Inherit)
}


@Composable
fun MobileLocalNav(onClick: () -> Unit) {
    Row(
        NavHeaderBackgroundStyle.toModifier()
            .displayUntil(Breakpoint.MD)
            .fillMaxWidth()
            .position(Position.Sticky)
            .top(NavHeaderHeight.value())
            .padding(leftRight = 1.cssRem)
    ) {
        Button(
            onClick = { onClick() },
            modifier = Modifier,
            variant = UnstyledButtonVariant
        ) {
            // Even though the button content is already in a row, this row is necessary for proper vertical alignment
            Row(
                Modifier
                    .padding(topBottom = 0.75.cssRem, leftRight = 0.5.cssRem)
                    .gap(0.5.cssRem)
            ) {
                HamburgerIcon()
                SpanText("Menu")
            }
        }
    }
}

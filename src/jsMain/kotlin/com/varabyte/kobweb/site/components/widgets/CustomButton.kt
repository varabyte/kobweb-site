package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import com.varabyte.kobweb.site.components.sections.NAV_ITEM_PADDING
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.px

private val PRIMARY_COLOR_RGB = Color(0,121,242)

private fun getButtonModifier(shape: String, primary: Boolean, modifier: Modifier): Modifier {
    return modifier.then(
        if (shape == "circle") {
            NAV_ITEM_PADDING.clip(Circle(radius = 40))
        } else {
            Modifier.styleModifier { borderRadius(8.px) }
        }
    ).then(
        if (primary) {
            Modifier.background(PRIMARY_COLOR_RGB)
        } else {
            Modifier
        }
    )
}

private fun getButtonTextModifier(primary: Boolean, color: Color): Modifier {
    return Modifier.then(
        if (primary) {
            Modifier.color(Color.White)
        } else {
            Modifier.color(color)
        }
    )
}

@Composable
fun CustomButton(
    path: String,
    text: String,
    shape: String       = "default",
    primary: Boolean    = false,
    modifier: Modifier  = Modifier,
    icon: @Composable () -> Unit
) {
    val ctx = rememberPageContext()
    val invertedColor = SilkTheme.palette.color.inverted()

    Button(
        onClick  = { ctx.router.navigateTo(path) },
        modifier = getButtonModifier(shape, primary, modifier)
    ) {
        Row(
            Modifier.padding(12.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            when (shape) {
                "default" -> Text(text, modifier = getButtonTextModifier(primary, invertedColor))
            }
        }
    }
}
package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.px
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.theme.colors.shifted

enum class ButtonShape {
    RECTANGLE,
    CIRCLE
}

private fun getButtonModifier(shape: ButtonShape): Modifier {
    return if (shape == ButtonShape.CIRCLE) {
        Modifier.clip(Circle(radius = 40))
    } else {
        Modifier.styleModifier { borderRadius(8.px) }
    }
}

val PrimaryButtonVariant = ButtonStyle.addVariant("primary") {
    val backgroundColor = Color.rgb(0, 121, 242)
    base = Modifier
        .background(backgroundColor)
        .color(Colors.White)
    hover = Modifier.background(backgroundColor.lightened())
}

val NormalButtonVariant = ButtonStyle.addVariant("normal") {
    @Suppress("NAME_SHADOWING") // Button colors should be inverted from the rest of the site
    val colorMode = colorMode.opposite()
    base = Modifier
        .background(SilkTheme.palettes[colorMode].background)
        .color(SilkTheme.palettes[colorMode].color)
    hover = Modifier
        .background(SilkTheme.palettes[colorMode].background.shifted(colorMode))
}

/**
 * Create a [Button] which is styled with primary or secondary colors.
 *
 * @param primary If true, use styles that call this button out as one associated with a major action you want to draw
 *   attention to.
 * @param content If set, renders custom content on the button. If both this and [text] is specified, then this
 *   content will be rendered to the left of the text with a bit of padding. This is particularly useful for rendering
 *   logos.
 */
@Composable
fun ThemedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    shape: ButtonShape = ButtonShape.RECTANGLE,
    primary: Boolean = false,
    content: @Composable () -> Unit = {}
) {
    Button(
        onClick,
        modifier.then(getButtonModifier(shape)),
        if (primary) PrimaryButtonVariant else NormalButtonVariant
    ) {
        Row(
            Modifier.padding(12.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
            if (text != null && text.isNotEmpty()) {
                Text(text)
            }
        }
    }
}

/**
 * Create a [ThemedButton] which acts likes a link, navigating to some target URL when clicked on.
 */
@Composable
fun LinkButton(
    path: String,
    modifier: Modifier = Modifier,
    text: String? = null,
    shape: ButtonShape = ButtonShape.RECTANGLE,
    primary: Boolean = false,
    content: @Composable () -> Unit = {}
) {
    val ctx = rememberPageContext()
    ThemedButton(onClick = { ctx.router.navigateTo(path) }, modifier, text, shape, primary, content)
}
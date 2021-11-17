package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.px
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.text.TextStyle

enum class LinkButtonShape {
    RECTANGLE,
    CIRCLE
}

private fun getButtonModifier(shape: LinkButtonShape): Modifier {
    return if (shape == LinkButtonShape.CIRCLE) {
        Modifier.clip(Circle(radius = 40))
    } else {
        Modifier.styleModifier { borderRadius(8.px) }
    }
}

val PrimaryButtonVariant = ButtonStyle.addVariant("primary") {
    base = Modifier.background(Color(0,121,242))
}

val NormalButtonVariant = ButtonStyle.addVariant("normal") { colorMode ->
    base = Modifier.background(SilkTheme.palettes[colorMode.opposite()].background)
}

val PrimaryButtonTextVariant = TextStyle.addVariant("button-primary") {
    base = Modifier.color(Color.White)
}

val NormalButtonTextVariant = TextStyle.addVariant("button-normal") { colorMode ->
    base = Modifier.color(SilkTheme.palettes[colorMode.opposite()].color.darkened())
}

@InitSilk
fun initButtonStyle(ctx: InitSilkContext) {
    ctx.theme.registerComponentVariants(
        PrimaryButtonVariant,
        NormalButtonVariant,
        PrimaryButtonTextVariant,
        NormalButtonTextVariant
    )
}

/**
 * Create a [Button] which acts likes a link, navigating to some target URL when clicked on.
 *
 * @param primary If true, use styles that call this button out as one associated with a major action
 * @param content If set, renders custom content on the button. If both this and [text] is specified, then this
 *   content will be rendered to the left of the text with a bit of padding. This is particularly useful for rendering
 *   logos.
 */
@Composable
fun LinkButton(
    path: String,
    text: String? = null,
    shape: LinkButtonShape = LinkButtonShape.RECTANGLE,
    primary: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val ctx = rememberPageContext()

    Button(
        onClick = { ctx.router.navigateTo(path) },
        modifier = modifier.then(getButtonModifier(shape)),
        variant = if (primary) PrimaryButtonVariant else NormalButtonVariant
    ) {
        Row(
            Modifier.padding(12.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
            if (text != null && text.isNotEmpty()) {
                Text(text, variant = if (primary) PrimaryButtonTextVariant else NormalButtonTextVariant)
            }
        }
    }
}
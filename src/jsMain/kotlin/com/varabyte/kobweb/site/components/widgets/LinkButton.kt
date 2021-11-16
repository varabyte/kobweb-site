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

private fun getButtonModifier(shape: String): Modifier {
    return if (shape == "circle") {
        Modifier.clip(Circle(radius = 40))
    } else {
        Modifier.styleModifier { borderRadius(8.px) }
    }
}

val PrimaryButtonVariant = ButtonStyle.addVariant("primary") {
    base = Modifier.background(Color(0,121,242))
}

val PrimaryButtonTextVariant = TextStyle.addVariant("button-primary") {
    base = Modifier.color(Color.White)
}

val NormalButtonTextVariant = TextStyle.addVariant("button-normal") { colorMode ->
    base = Modifier.color(SilkTheme.palettes[colorMode].color.darkened())
}

@InitSilk
fun initButtonStyle(ctx: InitSilkContext) {
    ctx.theme.registerComponentVariants(PrimaryButtonVariant, PrimaryButtonTextVariant, NormalButtonTextVariant)
}

@Composable
fun LinkButton(
    path: String,
    text: String,
    shape: String = "default",
    primary: Boolean = false,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val ctx = rememberPageContext()

    Button(
        onClick = { ctx.router.navigateTo(path) },
        modifier = modifier.then(getButtonModifier(shape)),
        variant = PrimaryButtonVariant.takeIf { primary }
    ) {
        Row(
            Modifier.padding(12.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            if (text.isNotEmpty()) {
                Text(text, variant = if (primary) PrimaryButtonTextVariant else NormalButtonTextVariant)
            }
        }
    }
}
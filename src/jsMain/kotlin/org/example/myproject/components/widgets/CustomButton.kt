package org.example.myproject.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.background
import com.varabyte.kobweb.compose.ui.padding
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.example.myproject.components.sections.NAV_ITEM_PADDING
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A
private const val PRIMARY_COLOR = "#2FCCB3"

private fun getButtonModifier(shape: String, primary: Boolean, modifier: Modifier): Modifier {
    return modifier.then(
        if (shape == "circle") {
            NAV_ITEM_PADDING.clip(Circle(radius = 40))
        } else {Modifier}
    ).then(
        if (primary) {
            Modifier.background(Color (PRIMARY_COLOR))
        } else {Modifier}
    )
}

@Composable
fun CustomButtonComponent(
    href: String,
    text: String,
    shape: String       = "default",
    primary: Boolean    = false,
    modifier: Modifier  = Modifier,
    icon: @Composable () -> Unit
   ) {
    A(
        attrs = {
            href(href)
        }
    )
    {
        Button(
            onClick  = {  },
            modifier = getButtonModifier(shape, primary, modifier)
        ) {
            Row(
                Modifier.padding(12.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                when (shape) {
                    "default" -> Text(text, modifier = Modifier.padding(left = 6.px))
                }
            }
        }
    }
}


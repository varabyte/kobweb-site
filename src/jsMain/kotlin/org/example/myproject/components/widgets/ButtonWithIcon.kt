package org.example.myproject.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.padding
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A

private val NAV_ITEM_PADDING = Modifier.padding(0.px, 24.px)

@Composable
fun ButtonWithIcon(
    href: String,
    text: String,
    shape: String = "default",
    icon: @Composable () -> Unit) {
    A(
        attrs = {
            href(href)
        }
    ) {
        Button(
            onClick = {  },
            modifier = if (shape == "circle") {
                NAV_ITEM_PADDING.clip(Circle(radius = 40))
            } else {Modifier}
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


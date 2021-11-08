    package org.example.myproject.components.widgets

    import androidx.compose.runtime.*
    import com.varabyte.kobweb.compose.foundation.layout.Row
    import com.varabyte.kobweb.compose.ui.*
    import com.varabyte.kobweb.silk.components.forms.Button
    import com.varabyte.kobweb.silk.components.navigation.LinkKey
    import com.varabyte.kobweb.silk.components.navigation.LinkState
    import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
    import com.varabyte.kobweb.silk.components.text.Text
    import com.varabyte.kobweb.silk.components.toModifier
    import com.varabyte.kobweb.silk.theme.SilkTheme
    import com.varabyte.kobweb.silk.theme.shapes.Circle
    import com.varabyte.kobweb.silk.theme.shapes.clip
    import org.example.myproject.components.sections.NAV_ITEM_PADDING
    import org.jetbrains.compose.web.css.Color
    import org.jetbrains.compose.web.css.borderRadius
    import org.jetbrains.compose.web.css.px
    import org.jetbrains.compose.web.dom.A

    private const val PRIMARY_COLOR = "#0079f2"

    private fun getButtonModifier(shape: String, primary: Boolean, modifier: Modifier): Modifier {
        return modifier.then(
            if (shape == "circle") {
                NAV_ITEM_PADDING.clip(Circle(radius = 40))
            } else {
                Modifier.styleModifier { borderRadius(8.px) }
            }
        ).then(
            if (primary) {
                Modifier.background(Color (PRIMARY_COLOR))
            } else {
                Modifier
            }
        )
    }

    private fun getButtonTextModifier(primary: Boolean): Modifier {
        return Modifier.then(
            if (primary) {
                Modifier.color(Color.white)
            } else {
                Modifier
            }
        )
    }

    @Composable
    fun CustomButton(
        href: String,
        text: String,
        shape: String       = "default",
        primary: Boolean    = false,
        modifier: Modifier  = Modifier,
        icon: @Composable () -> Unit
    ) {
        val state by remember { mutableStateOf(LinkState.DEFAULT) }

        A(
            href = href,
            attrs = SilkTheme.componentStyles[LinkKey].toModifier(state, UndecoratedLinkVariant).asAttributeBuilder()
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
                        "default" -> Text(text, modifier = getButtonTextModifier(primary))
                    }
                }
            }
        }
    }


package org.example.myproject.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.example.myproject.components.widgets.CustomButton
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.Color.black
import org.jetbrains.compose.web.css.selectors.attr
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Img

val NAV_ITEM_PADDING = Modifier.padding(0.px, 12.px)

@Composable
private fun NavLink(path: String, text: String) {
    Link(
        path,
        text,
        // Intentionally invert the header colors
        NAV_ITEM_PADDING.color(SilkTheme.palette.background),
        UndecoratedLinkVariant
    )
}

@Composable
private fun HomeLogo() {
    A(
        attrs = {
            href("/")
        }
    ) {
        Box(
            Modifier.padding(4.px)
        ) {
            Img(
                "https://storage.googleapis.com/kobweb-example-cdn/logo.png",
                attrs = {
                    style {
                        height(32.px)
                    }
                }
            )
        }
    }
}

@Composable
private fun ThemeSwitch(color: Color) {
    var colorMode by rememberColorMode()

    Button(
        onClick = { colorMode = colorMode.opposite() },
        NAV_ITEM_PADDING.clip(Circle())
    ) {
        Box(Modifier.padding(8.px)) {
            when (colorMode) {
                ColorMode.LIGHT -> FaMoon(color = color)
                ColorMode.DARK -> FaSun(color = color)
            }
        }
    }
}

@Composable
fun NavHeader() {
    val buttonIconColor = SilkTheme.palette.color.inverted()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(rgba(0,0,0,.65))
            .styleModifier {
                position(Position.Sticky)
                top(0.percent)
                attr("z-index", "1000")
            }
    ) {
        Row(
            Modifier.fillMaxWidth(70.percent).padding(1.em),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeLogo()
            Spacer()
            CustomButton("https://discord.gg/5NZ2GKV5Cs", text="", shape = "circle") {
                FaDiscord(color = buttonIconColor)
            }
            CustomButton("https://github.com/varabyte/kobweb", text="", shape = "circle") {
                FaGithub(color = buttonIconColor)
            }
            ThemeSwitch(buttonIconColor)
        }
    }
}
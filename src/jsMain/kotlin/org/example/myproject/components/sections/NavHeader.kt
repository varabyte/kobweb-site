package org.example.myproject.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Img

private val NAV_ITEM_PADDING = Modifier.padding(0.px, 24.px)

@Composable
private fun NavLink(path: String, text: String) {
    Link(
        path,
        text,
        // Intentionally invert the header colors (here, "primary" instead of "onPrimary")
        NAV_ITEM_PADDING.color(SilkTheme.palette.primary),
        UndecoratedLinkVariant,
    )
}

@Composable
fun NavHeader() {
    var colorMode by rememberColorMode()
    val palette = SilkTheme.palette
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.px)
            // Intentionally invert the header colors (here, setting the background to "onPrimary" instead of "primary")
            .background(palette.onPrimary),
    ) {
        Row(
            Modifier.fillMaxSize(80.percent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            A(
                attrs = {
                    href("/")
                }
            ) {
                Box(
                    Modifier.padding(4.px).height(36.px).width(84.px)
                ) {
                    Img(
                        "https://storage.googleapis.com/kobweb-example-cdn/Group%2043.png",
                        attrs = {
                            style {
                                height(36.px)
                            }
                        }
                    )
                }
            }
            Spacer()
            NavLink("/", "Home")
            NavLink("/docs", "Docs")
            NavLink("/examples", "Examples")
            NavLink("/blog", "Blog")
            Spacer()
            A(
                attrs = {
                    href("https://discord.gg/5NZ2GKV5Cs")
                    target(ATarget.Blank)
                }
            ) {
                Button(
                    onClick = {  },
                    modifier = NAV_ITEM_PADDING.clip(Circle())
                ) {
                    Box(Modifier.padding(4.px)) {
                        FaDiscord()
                    }
                }
            }
            A(
                attrs = {
                    href("https://github.com/varabyte/kobweb")
                    target(ATarget.Blank)
                }
            ) {
                Button(
                onClick = {  },
                modifier = NAV_ITEM_PADDING.clip(Circle())
                ) {

                    Box(Modifier.padding(4.px)) {
                        FaGithub()
                    }
                }
            }
            Button(
                onClick = { colorMode = colorMode.opposite() },
                NAV_ITEM_PADDING.clip(Circle())
            ) {
                Box(Modifier.padding(4.px)) {
                    when (colorMode) {
                        ColorMode.LIGHT -> FaSun()
                        ColorMode.DARK -> FaMoon()
                    }
                }
            }
        }
    }
}
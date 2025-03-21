package com.varabyte.kobweb.site.components.widgets.button

import Res
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionProperty
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.site.components.sections.navHeaderZIndex
import com.varabyte.kobweb.site.util.Language
import com.varabyte.kobweb.site.util.defaultLanguage
import com.varabyte.kobweb.site.util.localList
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

@Composable
fun LanguageSwitcherButton(
    currentLanguageCode: String,
    showMenu: Boolean,
    onLanguageClick: () -> Unit,
    onLanguageSelect: (String) -> Unit,

    ) {
    val colorMode = ColorMode.current
    val colorPalette = colorMode.toPalette()

    val currentLanguage = remember(currentLanguageCode) {
        Res.localList.find { it.code == currentLanguageCode }?.displayName ?: Res.defaultLanguage
    }

    DisposableEffect(showMenu) {
        // console.log("Language Switcher DisposableEffect called! showMenu: $showMenu")
        if (showMenu) {
            val clickListener: (Event) -> Unit = { event ->
                val target = event.target as? Element
                val menuElement = document.getElementById("language-menu")
                val buttonElement = document.getElementById("language-button")
                if (target != null && menuElement != null && buttonElement != null) {
                    val isOutsideMenu = !menuElement.contains(target)
                    val isOutsideButton = !buttonElement.contains(target)
                    //console.log("Language Switcher isOutsideMenu: ${isOutsideMenu}; isOutsideButton: ${isOutsideButton}")
                    if (isOutsideMenu && isOutsideButton) {
                        event.stopPropagation()
                        buttonElement.dispatchEvent(MouseEvent("click"))
                        // console.log("Language Switcher onLanguageClick called!")
                    }
                }
            }
            document.addEventListener("click", clickListener)
            //console.log("Language Switcher addEventListener")
            onDispose {
                document.removeEventListener("click", clickListener)
                //console.log("Language Switcher removeEventListener")
            }
        } else {
            onDispose {
                //console.log("Language Switcher onDispose")
            }
        }
    }

    Box(
        modifier = Modifier
            .position(Position.Relative)
            .margin(right = 0.5.cssRem),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                onLanguageClick()
            },
            modifier = Modifier
                .id("language-button")  // 添加ID
                .padding(0.25.cssRem)
        ) {
            Box(
                modifier = IconStyle.toModifier()
                    .size(30.8.px)
                    .border(1.px, LineStyle.Solid, colorPalette.color)
                    .borderRadius(50.percent)
                    .padding(0.8.px)
                    .display(DisplayStyle.Flex)
                    .justifyContent(JustifyContent.Center)
                    .alignItems(AlignItems.Center)
                    .transition(Transition.of(property = "background-color", duration = 200.ms)),
                contentAlignment = Alignment.Center
            ) {
                Text(currentLanguage.take(1))
            }
        }

        if (showMenu) {
            Column(
                modifier = Modifier
                    .id("language-menu")
                    .position(Position.Absolute)
                    .top(100.percent)
                    .right(0.px)
                    .backgroundColor(colorPalette.background)
                    .borderRadius(4.px)
                    .boxShadow(0.px, 2.px, 4.px, 0.px, rgba(0, 0, 0, 0.2))
                    .padding(0.5.cssRem)
                    .minWidth(100.px)
                    .navHeaderZIndex()
            ) {
                Res.localList.forEach { language ->
                    LanguageOption(
                        language = language,
                        isSelected = language.code == currentLanguageCode,
                        onSelect = {
                            onLanguageSelect(language.code)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val colorMode = ColorMode.current
    val colorPalette = colorMode.toPalette()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(leftRight = 0.25.cssRem, topBottom = 0.5.cssRem)
            .cursor(Cursor.Pointer)
            .backgroundColor(
                if (isSelected) colorPalette.color.toRgb().copyf(alpha = 0.1f) else Colors.Transparent
            )
            .borderRadius(4.px)
            .transition(Transition.of(property = TransitionProperty.All, duration = 300.ms))
            .onClick { onSelect() },
        contentAlignment = Alignment.CenterStart
    ) {
        SpanText(text = language.displayName, modifier = Modifier.fontSize(18.px).fontWeight(FontWeight.Bold))
    }
}
package org.example.myproject.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextAlign.Companion.Center
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant.style
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.components.text.TextStyle
import org.example.myproject.components.layouts.PageLayout
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.P

@Page
@Composable
fun HomePage() {
    PageLayout("") {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =  Arrangement.Center,
            modifier = Modifier.width(760.px)
        ) {
            Box (contentAlignment = Alignment.Center) {
                Text(
                    text = "Modern framework for full stack web apps in Kotlin",
                    modifier = Modifier.fontSize(64.px).fontWeight(FontWeight.Bold).color(Color("#111111")).styleModifier {
                        textAlign(Center)
                    },
                )
                P {
                    Text(
                        text = "Create production ready full stack web apps in a modern, concise and type safe programming language Kotlin. Kobweb includes everything you need to build modern static websites, as well as web applications faster.",
                        modifier = Modifier.color(Color("#666666")).styleModifier {
                            textAlign(Center)
                        }
                    )
                }
            }
        }
    }
}
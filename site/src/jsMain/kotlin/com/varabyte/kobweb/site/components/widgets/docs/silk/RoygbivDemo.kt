package com.varabyte.kobweb.site.components.widgets.docs.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.setVariable
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

// We specify the initial color of the rainbow here, since the variable
// won't otherwise be set until the user clicks a button.
val roygbivColor by StyleVariable<CSSColorValue>(Colors.Red)

val RoygbivDemoStyle = CssStyle.base {
    Modifier.width(12.cssRem).height(7.cssRem).backgroundColor(roygbivColor.value())
}

@Composable
fun RoygbivDemo() {
    val roygbiv = remember {
        listOf(
            Colors.Red,
            Colors.Orange,
            Colors.Yellow,
            Colors.Green,
            Colors.Blue,
            Colors.Indigo,
            Colors.Violet,
        )
    }

    var roygbivElement: HTMLElement? by remember { mutableStateOf(null) }
    Box(
        RoygbivDemoStyle.toModifier(),
        contentAlignment = Alignment.Center,
        ref = ref { roygbivElement = it }) {
        Button(onClick = {
            roygbivElement!!.setVariable(roygbivColor, roygbiv.random())
        }) {
            Text("Click me")
        }
    }
}

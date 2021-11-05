package org.example.myproject.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.padding
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.Text
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A

@Composable
fun ButtonWithIcon(href: String, text: String, icon: @Composable () -> Unit) {
    A(
        attrs = {
            href(href)
            target(ATarget.Blank)
        }
    ) {
        Button(
            onClick = {  },
        ) {
            Row(
                Modifier.padding(12.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Text(text, modifier = Modifier.padding(left = 6.px))
            }
        }
    }
}
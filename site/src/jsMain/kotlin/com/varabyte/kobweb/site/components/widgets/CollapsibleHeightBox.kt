package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.gridTemplateRows
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.transition
import org.jetbrains.compose.web.css.fr
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px

@Composable
fun CollapsibleHeightBox(
    open: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        Modifier
            .gridTemplateRows { minmax(0.px, if (open) 1.fr else 0.fr) }
            .overflow(Overflow.Hidden)
            .transition(Transition.of("grid-template-rows", 200.ms))
    ) {
        Box(modifier, content = content) // needed if inner element has padding
    }
}

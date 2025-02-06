package com.varabyte.kobweb.site.components.widgets.navigation

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.FaBook
import com.varabyte.kobweb.silk.components.navigation.Link
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
private fun _DocsLink(text: String, path: String, surroundedBy: Pair<Char, Char>? = null) {
    Span(Modifier.whiteSpace(WhiteSpace.NoWrap).toAttrs()) {
        surroundedBy?.let { (before, _) ->
            Text("$before")
        }
        Link(path) {
            FaBook()
            Text(" $text")
        }
        surroundedBy?.let { (_, after) ->
            Text("$after")
        }
    }
}

@Composable
fun DocsLink(text: String, path: String) {
    _DocsLink(text, path)
}

@Composable
fun DocsAside(text: String, path: String) {
    _DocsLink(text, path, surroundedBy = '(' to ')')
}


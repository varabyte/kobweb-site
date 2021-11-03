package org.example.myproject.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.Text
import org.example.myproject.components.layouts.PageLayout
import org.jetbrains.compose.web.dom.P

@Page
@Composable
fun DocsPage() {
    PageLayout("Docs") {
        Text("This is a skeleton app used to showcase a basic site made using Kobweb")
        P()
        Link("/", "Go Home")
    }
}
package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.gridRow
import com.varabyte.kobweb.compose.ui.modifiers.gridTemplateRows
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.site.Constants
import com.varabyte.kobweb.site.components.sections.Footer
import com.varabyte.kobweb.site.components.sections.NavHeader
import io.github.skeptick.libres.strings.getCurrentLanguageCode
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.css.fr
import org.jetbrains.compose.web.dom.Div

@Composable
fun PageLayout(title: String, description: String? = null, content: @Composable () -> Unit) {
    LaunchedEffect(title) {
        document.title = "$title | Kobweb"
    }

    LaunchedEffect(Unit) {
        // See kobweb config in build.gradle.kts which sets up Prism
        js("Prism.highlightAll()")
    }

    LaunchedEffect(description) {
        val head = document.head!!
        if (description != null) {
            val meta = head.querySelector("meta[name='description']") ?: document.createElement("meta").apply {
                setAttribute("name", "description")
                head.appendChild(this)
            }
            meta.setAttribute("content", description)
        } else {
            head.querySelector("meta[name='description']")?.let { head.removeChild(it) }
        }
    }

    val localLanguageCode = remember {
        localStorage.getItem(Constants.APP_LOCALE_KEY)
    }
    val pathname by remember { mutableStateOf(window.location.pathname) }
    LaunchedEffect(localLanguageCode) {
        if (isNotRootPath(pathname)) {
            console.info("pathName: $pathname")
            val pathCode = extractLanguageCode(pathname)
            console.info("pathCode: $pathCode")
            val newPath = if (localLanguageCode.isNullOrBlank() || localLanguageCode == "en") {
                if (pathCode.isNotBlank()) {
                    pathname.replaceFirst("/$pathCode/", "/")
                } else {
                    pathname
                }
            } else {
                if (pathCode.isBlank()) {
                    "/$localLanguageCode$pathname"
                } else {
                    pathname.replaceFirst("/$pathCode/", "/${localLanguageCode}/")
                }
            }

            if (newPath.isNotBlank() && newPath != pathname) {
                window.history.pushState(null, "", newPath)
                window.location.reload()
            }
        }
    }

    // Create a box with two rows: the main content (fills as much space as it can) and the footer (which reserves
    // space at the bottom). "auto" means the use the height of the row. "1fr" means give the rest of the space to
    // that row. Since this box is set to *at least* 100%, the footer will always appear at least on the bottom but
    // can be pushed further down if the first row grows beyond the page.
    Box(
        Modifier.fillMaxSize().gridTemplateRows { size(1.fr); size(auto) },
        contentAlignment = Alignment.TopCenter
    ) {
        Div(Modifier.fillMaxSize().align(Alignment.TopCenter).toAttrs()) {
            NavHeader()
            content()
        }
        // Associate the footer with the row that will get pushed off the bottom of the page if it can't fit.
        Footer(Modifier.gridRow(2, 3))
    }
}

private fun isNotRootPath(pathname: String): Boolean {
    return pathname != "/"
}

private fun extractLanguageCode(pathname: String): String {
    val regex = Regex("/(zh|en)/")
    val matchResult = regex.find(pathname)
    return matchResult?.groupValues?.get(1) ?: ""
}
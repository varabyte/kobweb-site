package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.browser.util.invokeLater
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.Router
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.js.collections.JsArray
import kotlin.js.collections.toList
import kotlin.js.json

@Composable
fun Search() {
    val ctx = rememberPageContext()
    val colorMode = ColorMode.current
    // Styles handled in build.gradle.kts
    LaunchedEffect(colorMode) {
        // Algolia DocSearch uses the `data-theme` attribute to determine the color mode for its default styles
        document.documentElement?.setAttribute("data-theme", colorMode.name.lowercase())
    }
    Div {
        DisposableEffect(Unit) {
            initAlgoliaSearch(scopeElement, ctx.router)
            onDispose { }
        }
    }
}


@JsModule("@docsearch/js")
private external fun docsearch(options: dynamic)

@JsModule("preact")
private external object Preact {
    fun createElement(
        type: String,
        props: dynamic,
        vararg children: dynamic
    ): dynamic
}

// See https://docsearch.algolia.com/docs/api (and thank you Algolia!)
@OptIn(ExperimentalJsCollectionsApi::class, ExperimentalJsExport::class)
private fun initAlgoliaSearch(element: HTMLElement, router: Router) {
    fun kobwebNavigate(url: String) {
        // The invokeLater prevents wrong scroll position - maybe a kobweb bug?
        window.invokeLater { router.navigateTo(url) }
    }
    docsearch(
        json(
            "container" to element,
            "appId" to "X21XB42TEV",
            "apiKey" to "34b8a0edc48e894f0181756e01d54e63",
            "indexName" to "kobweb-varabyte",
            "transformItems" to { items: JsArray<dynamic> ->
                items.toList().map { item ->
                    // The search API returns an absolute URL, but we make it relative so that Kobweb
                    // treats it as internal navigation.
                    // Note: In the live site, Kobweb would treat it as internal anyway due to matching
                    // domains, but this standardizes the experience in the localhost/preview sites.
                    item.url = item.url.unsafeCast<String>().removePrefix("https://kobweb.varabyte.com")
                    item
                }.asJsReadonlyArrayView()
            },
            "hitComponent" to { data: dynamic ->
                // Replace the default component with a custom anchor element that uses Kobweb's router
                val url = data.hit.url
                Preact.createElement(
                    "a", json(
                        "href" to url,
                        "onClick" to { event: Event ->
                            event.preventDefault()
                            kobwebNavigate(url)
                        }),
                    data.children
                )
            },
            "navigator" to json(
                //https://www.algolia.com/doc/ui-libraries/autocomplete/core-concepts/keyboard-navigation/#usage
                "navigate" to { data: dynamic ->
                    kobwebNavigate(data.itemUrl)
                }
            )
        )
    )
}

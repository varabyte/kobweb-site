package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.setVariable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.Router
import com.varabyte.kobweb.silk.style.CssLayer
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.style.vars.animation.TransitionDurationVars
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.document
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.js.collections.JsArray
import kotlin.js.collections.toList
import kotlin.js.json

private object DocSearchVars {
    val Background = StyleVariable.PropertyValue<CSSColorValue>("docsearch-searchbox-background")
    val FocusBackground = StyleVariable.PropertyValue<CSSColorValue>("docsearch-searchbox-focus-background")
    val Primary = StyleVariable.PropertyValue<CSSColorValue>("docsearch-primary-color")
}

// Default Algolia DocSearch styles are added in build.gradle.kts, here we make some adjustments
@CssLayer("") // Don't use a layer to take priority over default styles
val SearchStyle = CssStyle {
    base {
        Modifier
            .setVariable(DocSearchVars.FocusBackground, DocSearchVars.Background.value())
            .thenIf(
                colorMode.isDark,
                Modifier.setVariable(DocSearchVars.Background, Color.rgb(25, 25, 25))
            )
    }
    cssRule(" .DocSearch-Button") {
        Modifier
            .borderRadius(4.px)
            .transition(Transition.of("box-shadow", TransitionDurationVars.Normal.value()))
    }
    cssRule(" .DocSearch-Search-Icon") {
        Modifier.size(16.px)
    }
    cssRule(" .DocSearch-Button-Placeholder") {
        Modifier.transition(Transition.of("color", TransitionDurationVars.Normal.value()))

    }
    // Styles to fit in with other nav buttons on mobile
    val upToMdBreakpoint = Breakpoint.ZERO..<Breakpoint.MD
    upToMdBreakpoint {
        Modifier.setVariable(DocSearchVars.Background, Colors.Transparent)
    }
    cssRule(upToMdBreakpoint.toCSSMediaQuery(), " .DocSearch-Search-Icon") {
        Modifier.size(24.px)
    }
    cssRule(upToMdBreakpoint.toCSSMediaQuery(), " .DocSearch-Button") {
        Modifier
            .margin(0.px)
            .padding(0.px)
    }
}

@Composable
fun Search() {
    val ctx = rememberPageContext()
    val colorMode = ColorMode.current
    LaunchedEffect(colorMode) {
        // Algolia DocSearch uses the `data-theme` attribute to determine the color mode for its default styles
        document.documentElement?.setAttribute("data-theme", colorMode.name.lowercase())
        document.documentElement.unsafeCast<HTMLElement>().setVariable(DocSearchVars.Primary, Colors.DodgerBlue)
    }
    Div(SearchStyle.toAttrs()) {
        DisposableEffect(Unit) {
            initAlgoliaSearch(scopeElement, ctx.router)
            // We need to dispose the search element so that exit hooks are run and listeners are unregistered
            // See also: https://github.com/algolia/docsearch/issues/1363
            onDispose {
                Preact.render(null, scopeElement)
            }
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

    fun render(
        element: dynamic,
        container: HTMLElement
    )
}

// See https://docsearch.algolia.com/docs/api (and thank you Algolia!)
@OptIn(ExperimentalJsCollectionsApi::class, ExperimentalJsExport::class)
private fun initAlgoliaSearch(element: HTMLElement, router: Router) {
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
                            router.navigateTo(url)
                        }),
                    data.children
                )
            },
            "navigator" to json(
                //https://www.algolia.com/doc/ui-libraries/autocomplete/core-concepts/keyboard-navigation/#usage
                "navigate" to { data: dynamic ->
                    router.navigateTo(data.itemUrl)
                }
            )
        )
    )
}

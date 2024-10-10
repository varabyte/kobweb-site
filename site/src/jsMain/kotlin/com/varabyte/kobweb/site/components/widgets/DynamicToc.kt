package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.vars.size.FontSizeVars
import com.varabyte.kobweb.site.components.sections.listing.ListingElementStyle
import com.varabyte.kobweb.site.components.sections.listing.ListingIndentVar
import com.varabyte.kobweb.site.components.sections.listing.ListingLinkVariant
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.s
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.*

fun HTMLElement.getHeadings(
    minHeaderLevel: Int = 2,
    maxHeaderLevel: Int = 4,
): List<HTMLHeadingElement> {
    require(minHeaderLevel in 1..6) { "minHeaderLevel must be in range 1..6, got $minHeaderLevel" }
    require(maxHeaderLevel in 1..6) { "maxHeaderLevel must be in range 1..6, got $maxHeaderLevel" }
    require(maxHeaderLevel >= minHeaderLevel) { "maxHeaderLevel must be >= minHeaderLevel, got $minHeaderLevel > $maxHeaderLevel" }

    return this
        .querySelectorAll((minHeaderLevel..maxHeaderLevel).joinToString { "h$it" })
        .asList()
        .unsafeCast<List<HTMLHeadingElement>>()
}

val DynamicTocStyle = CssStyle.base {
    Modifier
        .listStyle(ListStyleType.None)
        .padding(0.px)
        .fontSize(FontSizeVars.SM.value())
}


@Composable
fun DynamicToc(
    headings: List<HTMLHeadingElement>,
    intersectionObserverOptions: IntersectionObserver.Options? = null,
    modifier: Modifier = Modifier,
) {
    var currentId by remember { mutableStateOf<String?>(null) }
    TocContent(headings, currentId, modifier)
    DisposableEffect(headings, intersectionObserverOptions) {
        val headingsInRange = mutableListOf<String>()

        val observer = IntersectionObserver(intersectionObserverOptions) { entries ->
            entries.forEach { entry ->
                if (entry.isIntersecting) {
                    headingsInRange.add(entry.target.id)
                } else {
                    headingsInRange.remove(entry.target.id)
                }
            }

            if (headingsInRange.isNotEmpty()) {
                currentId = headings.first { it.id in headingsInRange }.id
            } else {
                val nextHeading = entries.firstOrNull { it.boundingClientRect.top > it.rootBounds.top }?.target
                if (nextHeading != null) {
                    currentId = headings.getOrNull(headings.indexOf(nextHeading) - 1)?.id
                } else if (currentId == null) {
                    // Handle case where page starts past the last heading
                    val lastEntry = entries.last()
                    if (lastEntry.boundingClientRect.top <= lastEntry.rootBounds.top) {
                        currentId = lastEntry.target.id
                    }
                }
            }
        }
        headings.forEach { heading ->
            observer.observe(heading)
        }
        onDispose {
            headings.forEach { heading ->
                observer.unobserve(heading)
            }
        }
    }
}


@Composable
fun TocContent(
    headings: List<HTMLHeadingElement>,
    currentId: String? = null,
    modifier: Modifier = Modifier,
) {
    Ul(
        DynamicTocStyle.toModifier()
            .then(modifier)
            .toAttrs()
    ) {
        headings.forEach { heading ->
            Li(
                ListingElementStyle.toModifier()
                    .setVariable(ListingIndentVar, heading.tagName[1].digitToInt() - 2)
                    .toAttrs()
            ) {
                Link(
                    path = "#${heading.id}",
                    text = heading.textContent!!,
                    modifier = Modifier
                        .transition(Transition.of("color", 0.2.s, TransitionTimingFunction.Ease))
                        .thenIf(heading.id == currentId) {
                            Modifier
                                .color(Colors.DodgerBlue)
                                .fontWeight(FontWeight.Bold)
                        },
                    variant = ListingLinkVariant,
                )
            }
        }
    }
}

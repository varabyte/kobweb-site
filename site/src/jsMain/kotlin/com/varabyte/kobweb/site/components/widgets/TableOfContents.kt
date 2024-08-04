package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.extendedBy
import com.varabyte.kobweb.silk.style.selectors.anyLink
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.vars.size.FontSizeVars
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.shifted
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
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
val DynamicTocIndentVar by StyleVariable<Int>()

// TODO: is this nicer if it's a cssRule inside DynamicTocStyle?
val DynamicTocElementStyle = CssStyle.base {
    Modifier
        .padding {
            left(calc { num(DynamicTocIndentVar.value()) * 1.cssRem })
            topBottom(0.5.cssRem)
        }
}

val DynamicTocLinkVariant = UndecoratedLinkVariant.extendedBy {
    anyLink {
        Modifier.color(SilkTheme.palettes[colorMode].color.shifted(colorMode.opposite, 0.15f))
    }
    hover {
        Modifier.color(SilkTheme.palettes[colorMode].color.shifted(colorMode, 0.15f))
    }
}

@Composable
fun DynamicToc(headings: List<HTMLHeadingElement>, modifier: Modifier = Modifier) {
    var currentId by remember { mutableStateOf<String?>(null) }
    DisposableEffect(headings) {
        // TODO: potentially make these numbers more scientific?
        val top = 72 // Height of the top nav bar
        val bottom = top + 72 // This should be small enough such that two headings don't fit at the same time
        val height = document.documentElement!!.clientHeight
        val intersectionObserver = IntersectionObserver(
            options = IntersectionObserver.Options(
                rootMargin = "-${top}px 0% ${bottom - height}px",
            )
        ) { entries ->
            val lastIntersecting = entries.lastOrNull { it.isIntersecting }
            if (lastIntersecting != null) {
                currentId = lastIntersecting.target.id
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
        // Ensure all elements are loaded & positioned before we start observing
        window.runWhenLoaded {
            headings.forEach { heading ->
                intersectionObserver.observe(heading)
            }
        }
        onDispose {
            headings.forEach { heading ->
                intersectionObserver.unobserve(heading)
            }
        }
    }
    SpanText("On this page", Modifier.fontWeight(FontWeight.Bold).lineHeight(150.percent))
    Ul(
        DynamicTocStyle.toModifier()
            .then(modifier)
            .toAttrs()
    ) {
        headings.forEach { heading ->
            Li(
                DynamicTocElementStyle.toModifier()
                    .setVariable(DynamicTocIndentVar, heading.tagName[1].digitToInt() - 2)
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
                    variant = DynamicTocLinkVariant
                )
            }
        }
    }
}

private fun Window.runWhenLoaded(block: () -> Unit) {
    if (document.readyState == DocumentReadyState.COMPLETE) {
        block()
    } else {
        addEventListener("load", { block() })
    }
}

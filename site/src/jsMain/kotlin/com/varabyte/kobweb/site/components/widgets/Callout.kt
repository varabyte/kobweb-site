package com.varabyte.kobweb.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxShadow
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.style.*
import com.varabyte.kobweb.silk.style.selectors.descendants
import com.varabyte.kobweb.silk.style.selectors.focusWithin
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

// This file contains a PoC for a Callout widget
// It's quite possible that the entire APi design may need to be changed

enum class CalloutType {
    // GitHub call-outs
    NOTE,
    TIP,
    IMPORTANT,
    WARNING,
    CAUTION,

    // Custom call-outs (inspired by https://squidfunk.github.io/mkdocs-material/reference/admonitions/#supported-types)
    INFO,
    SUCCESS,
    FAILURE,
    QUOTE;
}

object CalloutVars {
    val BorderColor by StyleVariable<CSSColorValue>()

    object AccentColor {
        // TODO: these colors were chosen somewhat arbitrarily, take another look at them
        private val NoteColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.DodgerBlue)
        private val TipColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.SeaGreen)
        private val ImportantColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.DarkOrchid)
        private val WarningColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.Orange)
        private val CautionColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.Red)
        private val InfoColor by StyleVariable<CSSColorValue>(defaultFallback = NoteColor.value())
        private val SuccessColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.MediumSeaGreen)
        private val FailureColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.OrangeRed)
        private val QuoteColor by StyleVariable<CSSColorValue>(defaultFallback = Colors.Gray)
        operator fun get(type: CalloutType): StyleVariable<CSSColorValue, CSSColorValue> {
            return when (type) {
                CalloutType.NOTE -> NoteColor
                CalloutType.TIP -> TipColor
                CalloutType.IMPORTANT -> ImportantColor
                CalloutType.WARNING -> WarningColor
                CalloutType.CAUTION -> CautionColor
                CalloutType.INFO -> InfoColor
                CalloutType.SUCCESS -> SuccessColor
                CalloutType.FAILURE -> FailureColor
                CalloutType.QUOTE -> QuoteColor
            }
        }
    }
}

sealed interface CalloutKind : ComponentKind

val CalloutStyle = CssStyle<CalloutKind> {

}

@Composable
private fun Callout(
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<CalloutKind>? = null,
    header: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Div(CalloutStyle.toModifier(variant).then(modifier).toAttrs()) {
        header?.invoke()
        content()
    }
}

// TODO: Consider not using Fa icons, SVG instead? (either hand-build or "data:image" string?)
//  - compatible with non-Fa icon users
//  - won't have to wait (as long) for them to load in (probably)
@Composable
fun CalloutType.toLabelContent(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {
    val type = this
    P(
        Modifier
            .fontWeight(FontWeight.Medium)
            .margin { top(0.px) }
            .then(modifier)
            .toAttrs()
    ) {
        when (type) {
            CalloutType.NOTE -> {
                FaCircleInfo(iconModifier)
                Text("Note")
            }

            CalloutType.TIP -> {
                FaLightbulb(iconModifier)
                Text("Tip")
            }

            CalloutType.IMPORTANT -> {
                FaMessage(iconModifier)
                Text("Important")
            }

            CalloutType.WARNING -> {
                FaTriangleExclamation(iconModifier)
                Text("Warning")
            }

            CalloutType.CAUTION -> {
                FaTriangleExclamation(
                    iconModifier
                ) // Couldn't find anything better
                Text("Caution")
            }

            CalloutType.INFO -> {
                FaCircleInfo(iconModifier)
                Text("Info")
            }

            CalloutType.SUCCESS -> {
                FaCheck(iconModifier)
                Text("Success")
            }

            CalloutType.FAILURE -> {
                FaXmark(iconModifier)
                Text("Failure")
            }

            CalloutType.QUOTE -> {
                FaQuoteRight(iconModifier)
                Text("Quote")
            }
        }
    }
}

val GitHubCalloutVariant = CalloutStyle.addVariant {
    base {
        Modifier
            .borderLeft(0.25.em, LineStyle.Solid, CalloutVars.BorderColor.value())
            .padding(0.5.cssRem, 1.cssRem)
            .margin(bottom = 1.cssRem)
    }
    // HACK(?) to target the "content" `P {...}` generated by markdown
    descendants(":last-child") {
        Modifier.margin(0.px)
    }
}

@Composable
fun GitHubStyleCallout(
    type: CalloutType,
    modifier: Modifier = Modifier,
    accentColor: CSSColorValue = CalloutVars.AccentColor[type].value(),
    header: @Composable () -> Unit = {
        type.toLabelContent(Modifier.color(accentColor), iconModifier = Modifier.margin(right = 0.5.cssRem))
    },
    content: @Composable () -> Unit,
) {
    Callout(
        Modifier
            .setVariable(CalloutVars.BorderColor, accentColor)
            .then(modifier),
        GitHubCalloutVariant,
        header = header,
        content = content,
    )
}

@Composable
fun GitHubStyleCallout(
    type: CalloutType,
    text: String,
    modifier: Modifier = Modifier,
    accentColor: CSSColorValue = CalloutVars.AccentColor[type].value(),
) {
    GitHubStyleCallout(type, modifier, accentColor) {
        P { Text(text) } // TODO: this P might not be needed? But then the Hack in GitHubCalloutVariant breaks...
    }
}

// This is used for padding + negative margin to fill the background color beyond the "real" border
// But perhaps there is a better way to do that
private val MkDocsInlinePadding = 0.75.cssRem
val MkDocsCalloutVariant = CalloutStyle.addVariant {
    // The numbers below are not exact copies of MkDocs, they are roughly adapted for MkDocs's weird font scaling
    base {
        Modifier
            .border(1.px, LineStyle.Solid, CalloutVars.BorderColor.value())
            .borderRadius(0.25.cssRem)
            .padding(leftRight = MkDocsInlinePadding)
            .margin(topBottom = 1.25.em)
            .transition(Transition.of("box-shadow", 125.ms))
            .boxShadow(
                BoxShadow.of(
                    offsetY = 0.25.cssRem,
                    blurRadius = 0.625.cssRem,
                    color = Color.rgba(0x0000000d)
                ),
                BoxShadow.of(
                    blurRadius = 1.px,
                    color = Color.rgba(0x0000001a)
                )
            )
        // TODO: break-inside for printing?
    }
    focusWithin {
        Modifier
            .boxShadow(
                spreadRadius = 0.25.cssRem,
                // Same caniuse caveat as below
                color = "rgb(from ${CalloutVars.BorderColor.value()} r g b / 0.1)".unsafeCast<CSSColorValue>()
            )
    }
}

@Composable
fun MkDocsStyleCallout(
    type: CalloutType,
    modifier: Modifier = Modifier,
    accentColor: CSSColorValue = CalloutVars.AccentColor[type].value(),
    header: @Composable () -> Unit = {
        type.toLabelContent(
            Modifier
                .margin(topBottom = 0.px, leftRight = -MkDocsInlinePadding)
                .padding(topBottom = 0.5.cssRem, leftRight = MkDocsInlinePadding + 0.375.cssRem)
                // TODO: 89% caniuse score
                //  Could also use Kobweb's color for this logic, but then we can't use StyleVars
                .backgroundColor("rgb(from ${CalloutVars.BorderColor.value()} r g b / 0.2)".unsafeCast<CSSColorValue>()),
            iconModifier = Modifier
                .color(CalloutVars.BorderColor.value())
                .margin(right = 0.75.cssRem)
        )
    },
    content: @Composable () -> Unit,
) {
    Callout(
        Modifier
            .setVariable(CalloutVars.BorderColor, accentColor)
            .then(modifier),
        MkDocsCalloutVariant,
        header = header,
        content = content,
    )
}

@Composable
fun MkDocsStyleCallout(
    type: CalloutType,
    text: String,
    modifier: Modifier = Modifier,
    accentColor: CSSColorValue = CalloutVars.AccentColor[type].value(),
) {
    MkDocsStyleCallout(type, modifier, accentColor) {
        P { Text(text) }
    }
}

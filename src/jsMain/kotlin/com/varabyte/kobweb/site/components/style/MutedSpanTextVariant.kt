package com.varabyte.kobweb.site.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.theme.toSilkPalette

// For occasional text that we want to de-emphasize a bit, e.g. for subtitles.
// Note: We used to use the opacity style for this, but that caused an issue with the way blur was calculated, for the
// header glass effect, so we use alpha here instead.
val MutedSpanTextVariant by SpanTextStyle.addVariantBase {
    Modifier.color(colorMode.toSilkPalette().color.toRgb().copyf(alpha = 0.7f))
}
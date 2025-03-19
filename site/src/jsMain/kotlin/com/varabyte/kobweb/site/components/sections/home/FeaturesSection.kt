package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.CSSPosition
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.functions.RadialGradient
import com.varabyte.kobweb.compose.css.functions.radialGradient
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.site.components.style.MutedSpanTextVariant
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.dividerBoxShadow
import com.varabyte.kobweb.site.components.style.siteText
import com.varabyte.kobweb.site.components.widgets.Section
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H2

fun Modifier.background(colorMode: ColorMode) =
    this.then(when (colorMode) {
        ColorMode.DARK -> Modifier.backgroundImage(
            radialGradient(RadialGradient.Shape.Circle, Color.rgb(41, 41, 46), Color.rgb(25, 25, 28), CSSPosition.Top)
        )
        ColorMode.LIGHT -> Modifier.backgroundColor(Colors.White)
    })


private class Feature(val heading: String, val desc: String)

val FeatureItemStyle = CssStyle.base {
    Modifier.margin(18.px)
}

@Composable
private fun FeatureItem(feature: Feature) {
    Box(
        FeatureItemStyle.toModifier()
            .borderRadius(12.px)
            .background(ColorMode.current)
            .padding(2.em)
            .dividerBoxShadow()
    ) {
        Column {
            SpanText(feature.heading, Modifier.fontWeight(FontWeight.Bold).margin(bottom = 0.75.cssRem))
            SpanText(feature.desc, Modifier.siteText(SiteTextSize.SMALL), MutedSpanTextVariant)
        }
    }
}

@Composable
fun FeaturesSection() {
    val features = remember {
        listOf(
            Feature(Res.string.page_routing, Res.string.page_routing_description),
            Feature(Res.string.live_reloading, Res.string.live_reloading_description),
            Feature(Res.string.light_and_dark_ui, Res.string.light_and_dark_ui_description),
            Feature(Res.string.component_library, Res.string.component_library_description),
            Feature(Res.string.component_styling, Res.string.component_styling_description),
            Feature(Res.string.seo_friendly, Res.string.seo_friendly_description),
            Feature(Res.string.server_api_routes, Res.string.server_api_routes_description),
            Feature(Res.string.markdown_support, Res.string.markdown_support_description),
            Feature(Res.string.font_awesome_md_icons, Res.string.font_awesome_md_icons_description),
            Feature(Res.string.compose_extensions, Res.string.compose_extensions_description),
            Feature(Res.string.java_script_ecosystem, Res.string.java_script_ecosystem_description),
            Feature(Res.string.open_source, Res.string.open_source_description),
        )
    }

    Section {
        H2 {
            SpanText(
                Res.string.why_kobweb,
                Modifier.textAlign(TextAlign.Center)
            )
        }
        SpanText(
            Res.string.why_kobweb_subtitle,
            Modifier.siteText(SiteTextSize.NORMAL).textAlign(TextAlign.Center),
            MutedSpanTextVariant
        )

        SimpleGrid(numColumns(1, md = 3)) {
            features.forEach { feature -> FeatureItem(feature) }
        }
    }
}

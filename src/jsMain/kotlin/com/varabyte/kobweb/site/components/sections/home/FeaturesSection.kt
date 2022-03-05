package com.varabyte.kobweb.site.components.sections.home

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.site.components.style.boxShadow
import com.varabyte.kobweb.site.components.widgets.Section
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

private fun getBackgroundColor(colorMode: ColorMode): String {
    return when (colorMode) {
        ColorMode.DARK -> "radial-gradient(circle at top, rgba(41,41,46,1) 0%, rgba(25,25,28,1) 100%)"
        ColorMode.LIGHT -> "#ffffff"
    }
}

private class Feature(val heading: String, val desc: String)

val FeatureItemStyle = ComponentStyle.base("feature-item") {
    Modifier.margin(18.px)
}

@Composable
private fun FeatureItem(feature: Feature) {
    val colorMode by rememberColorMode()

    Box (
        FeatureItemStyle.toModifier().then(Modifier
            .borderRadius(12.px)
            .background(getBackgroundColor(colorMode))
            .padding(2.em)
            .boxShadow(colorMode)
        )
    ) {
        Column {
            Text(feature.heading, Modifier.fontWeight(FontWeight.Bold).margin(bottom = 0.75.cssRem))
            Text(feature.desc, Modifier.lineHeight(1.5).opacity(70.percent))
        }
    }
}

@Composable
fun FeaturesSection() {
    val features = remember {
        listOf(
            Feature("Page Routing", "Annotate a composable method with @Page to make it a route"),
            Feature("Server API Routes", "Annotate methods with @Api to generate server API endpoints"),
            Feature("Live Reloading", "An environment built from the ground up around live reloading"),
            Feature("Light and Dark UI", "Built-in support for multiple color modes"),
            Feature("Component library", "Silk is a UI layer included with Kobweb and built upon Compose for Web"),
            Feature("Component styling", "Powerful and simple API for defining and overriding styles"),
            Feature("Shared Types", "Share rich Kotlin class types between client and server"),
            Feature("Markdown support", "Out-of-the-box Markdown support"),
            Feature("SEO-friendly", "Supports static site exports for improved SEO"),
            Feature("Font Awesome", "Silk includes support for Font Awesome icons"),
            Feature("Compose extensions", "Adds familiar Modifier, Box, Row, and Columns concepts to Compose for Web"),
            Feature("Open source", "An open source project built with a friendly license and a welcoming community"),
        )
    }

    Section {
        H2 {
            Text(
                "Why Kobweb?",
                Modifier.textAlign(TextAlign.Center)
            )
        }
        Text(
            "Build your Compose for Web apps quicker and easier",
            Modifier
                .lineHeight(1.5)
                .fontSize(1.25.cssRem)
                .opacity(70.percent)
                .textAlign(TextAlign.Center)
        )

        SimpleGrid(numColumns(1, md = 3)) {
            features.forEach { feature -> FeatureItem(feature) }
        }
    }
}

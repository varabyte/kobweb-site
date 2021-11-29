package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.site.components.layouts.PageLayout
import com.varabyte.kobweb.site.components.sections.CliSection
import com.varabyte.kobweb.site.components.sections.CtaSection
import com.varabyte.kobweb.site.components.sections.FeaturesSection
import com.varabyte.kobweb.site.components.sections.HeroSection
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.percent

@Page
@Composable
fun HomePage() {
    PageLayout("Home") {
        Box(
            Modifier.width(100.percent),
            contentAlignment = Alignment.Center,
        ){
            HeroSection()
            FeaturesSection()
            CliSection()
            CtaSection()
        }
    }
}
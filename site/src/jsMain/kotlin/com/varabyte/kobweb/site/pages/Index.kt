package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.site.components.layouts.PageLayout
import com.varabyte.kobweb.site.components.sections.home.CliSection
import com.varabyte.kobweb.site.components.sections.home.CtaSection
import com.varabyte.kobweb.site.components.sections.home.FeaturesSection
import com.varabyte.kobweb.site.components.sections.home.HeroSection
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun HomePage() {
    PageLayout("Home") {
        Column(
            Modifier.width(100.percent),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            HeroSection()
            FeaturesSection()
            CliSection()
            CtaSection()
        }
    }
}
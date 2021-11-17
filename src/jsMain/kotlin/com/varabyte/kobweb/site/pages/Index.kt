package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.site.components.layouts.PageLayout
import CliSection
import FeaturesSection
import HeroSection
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.padding
import com.varabyte.kobweb.compose.ui.width
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent

@Page
@Composable
fun HomePage() {
    PageLayout("Home") {
        Box(
            Modifier.padding(bottom = 2.cssRem).width(100.percent),
            contentAlignment = Alignment.Center,
        ){
            HeroSection()
            FeaturesSection()
            CliSection()
        }
    }
}
    package org.example.myproject.pages

    import androidx.compose.runtime.*
    import com.varabyte.kobweb.core.Page
    import org.example.myproject.components.layouts.PageLayout
    import CliSection
    import FeaturesSection
    import HeroSection
    import com.varabyte.kobweb.compose.foundation.layout.Box
    import com.varabyte.kobweb.compose.ui.Alignment
    import com.varabyte.kobweb.compose.ui.Modifier
    import com.varabyte.kobweb.compose.ui.padding
    import org.jetbrains.compose.web.css.cssRem

    @Page
    @Composable
    fun HomePage() {
        PageLayout("") {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 4.cssRem, bottom = 4.cssRem)
            ){
                HeroSection()
                FeaturesSection()
                CliSection()
            }

        }
    }

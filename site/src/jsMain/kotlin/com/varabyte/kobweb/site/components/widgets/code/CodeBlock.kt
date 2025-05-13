package com.varabyte.kobweb.site.components.widgets.code

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.MinWidth
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.thenIfNotNull
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.layer
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.layer.SilkLayer
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.site.components.style.DividerColor
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

fun Modifier.defaultPadding() = padding(1.em)

val CodeBlockStyle = CssStyle.base(extraModifier = { SmoothColorStyle.toModifier() }) {
    Modifier
        .borderRadius(10.px)
        .overflow { x(Overflow.Auto) }
        .siteText(SiteTextSize.CODE)
        .border(1.px, LineStyle.Solid, DividerColor.value())
        .defaultPadding()
}

@InitSilk
fun clearPrismHighlightLinesPadding(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        registerStyleBase("pre[data-line]") {
            // Prism.js adds a bunch of left padding for line numbers that we never show. Override it.
            Modifier.defaultPadding()
        }
    }
}

/**
 * Creates a code block that is colored by PrismJs
 */
// Note: To enable this widget to work, we needed to add PrismJs support to this project. See the kobweb
// block in our build.gradle.kts file to see how this was done.
@Composable
fun CodeBlock(code: String, modifier: Modifier = Modifier, lang: String? = null, highlightLines: String? = null) {
    Pre(CodeBlockStyle.toModifier()
        .thenIfNotNull(highlightLines) { Modifier.attr("data-line", it)}
        .toAttrs()) {
        Code(
            attrs = SmoothColorStyle.toModifier()
                // Set min width so that `diff-highlight` coverts the entire width even when the code is scrollable
                .display(DisplayStyle.Block).minWidth(MinWidth.MaxContent)
                // The above style messes up text sizes inside code blocks that can
                // scroll horizontally (but only on iOS...)
                .styleModifier { property("-webkit-text-size-adjust", 100.percent) }
                .classNames("language-${lang ?: "none"}")
                .thenIf(lang?.startsWith("diff") == true, Modifier.classNames("diff-highlight"))
                .then(modifier)
                .toAttrs()
        ) {
            Text(code)
        }
    }
}

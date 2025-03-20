package com.varabyte.kobweb.site.components.widgets.docs.css

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AnimationIterationCount
import com.varabyte.kobweb.compose.css.functions.LinearGradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text

val WobbleKeyframes = Keyframes {
    from { Modifier.rotate((-5).deg) }
    to { Modifier.rotate(5.deg) }
}

private val MODIFIER_1 = Modifier.padding(topBottom = 0.05.cssRem, leftRight = 1.5.cssRem)
private val MODIFIER_2 = MODIFIER_1.border(1.px, LineStyle.Solid, Colors.Black)
private val MODIFIER_3 = MODIFIER_2.borderRadius(5.px)
private val MODIFIER_4 = MODIFIER_3.boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
private val MODIFIER_5 = MODIFIER_4.backgroundImage(
    linearGradient(Colors.LightBlue, Colors.LightGreen, LinearGradient.Direction.ToRight)
)
// lazy block required to avoid `toAnimation` from throwing; Silk must be initialized first.
private val MODIFIER_6 by lazy {
    MODIFIER_5.animation(
        WobbleKeyframes.toAnimation(
            ColorMode.LIGHT,
            duration = 1.s,
            iterationCount = AnimationIterationCount.Infinite,
            timingFunction = AnimationTimingFunction.EaseInOut,
            direction = AnimationDirection.Alternate,
        )
    )
}

@Composable
private fun CssExampleContent(modifier: Modifier) {
    Box(Modifier
        .size(12.cssRem, 3.cssRem)
        .backgroundColor(Colors.WhiteSmoke)
        .color(Colors.Black),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("WELCOME!!")
        }
    }
}

@Composable
fun CssExample1() = CssExampleContent(MODIFIER_1)

@Composable
fun CssExample2() = CssExampleContent(MODIFIER_2)

@Composable
fun CssExample3() = CssExampleContent(MODIFIER_3)

@Composable
fun CssExample4() = CssExampleContent(MODIFIER_4)

@Composable
fun CssExample5() = CssExampleContent(MODIFIER_5)

@Composable
fun CssExample6() = CssExampleContent(MODIFIER_6)
package com.varabyte.kobweb.site

import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.site.components.layouts.EditPageLink
import org.jetbrains.compose.web.testutils.ComposeWebExperimentalTestsApi
import org.jetbrains.compose.web.testutils.runTest
import org.w3c.dom.HTMLAnchorElement
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private const val EXAMPLE_LINK = "https://kobweb.varabyte.com/docs/concepts/foundation/markdown"

// TODO: IMPORTANT: This code is not complete as the tests couldn't run:
//  IllegalStateException: Failed to convert a CssStyle to a modifier. Double check that the style was declared at the top-level of your file or registered manually via an `@InitSilk` method.

@OptIn(ComposeWebExperimentalTestsApi::class)
class EditPageLinkTest {
    @Test
    fun rendersPenIconAndText() = runTest {
        composition {
            SilkApp {
                EditPageLink(EXAMPLE_LINK)
            }
        }

        val spans = root.getElementsByTagName("span")
        val iconElement = spans.item(0)
        val spanTextElement = spans.item(1)

        assertNotNull(iconElement)
        assertNotNull(spanTextElement)

        assertContains(spanTextElement.textContent!!, "Edit this page", ignoreCase = true)
        assertTrue(iconElement.classList.contains("fa-pen"))
    }

    @Test
    fun generatesGithubEditPageLinkCorrectly() = runTest {
        composition {
            SilkApp {
                EditPageLink(pageRoute = "docs/getting-started/hello-world")
            }
        }

        val linkElement = root.querySelector("a") as HTMLAnchorElement?
        assertNotNull(linkElement)

        val link = linkElement.href

        assertEquals(link, "https://github.com/varabyte/kobweb-site/edit/main/site/src/jsMain/resources/markdown/docs/getting-started/HelloWorld.md")
    }
}

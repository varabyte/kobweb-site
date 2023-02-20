package com.varabyte.kobweb.site.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.site.components.sections.listing.ListingSideBar
import com.varabyte.kobweb.site.components.style.SiteTextSize
import com.varabyte.kobweb.site.components.style.siteText
import com.varabyte.kobweb.site.model.listing.*
import com.varabyte.kobwebx.markdown.markdown
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

fun PageContext.RouteInfo.toArticleInfo(): Pair<Category, Article>? {
    val categorySlug = path.substringBeforeLast('/').substringAfterLast('/')
    val category = SITE_LISTING.find { it.slug == categorySlug } ?: return null
    val subcategorySlug = path.substringAfterLast('/')
    val article = SITE_LISTING.findArticle(categorySlug, subcategorySlug) ?: return null
    return category to article
}

@Composable
fun DocsLayout(content: @Composable () -> Unit) {
    val ctx = rememberPageContext()

    val articleInfo = ctx.markdown?.let { ctx.route.toArticleInfo() }
    val title = if (articleInfo != null) {
        "Docs - ${articleInfo.first.title} - ${articleInfo.second.title}"
    } else "Docs"

    PageLayout(title) {
        Row(Modifier.fillMaxWidth().maxWidth(800.px).gap(1.cssRem).fillMaxHeight().siteText(SiteTextSize.NORMAL)) {
            ListingSideBar()
            Column(Modifier.fillMaxSize()) {
                if (articleInfo != null) {
                    H1(Modifier.align(Alignment.CenterHorizontally).toAttrs()) {
                        Text(articleInfo.second.title)
                    }
                }
                content()
            }
        }
    }
}
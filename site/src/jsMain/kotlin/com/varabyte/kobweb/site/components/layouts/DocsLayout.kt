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

fun PageContext.RouteInfo.toArticleHandle(): ArticleHandle? {
    return SITE_LISTING.findArticle(path)
}

@Composable
fun DocsLayout(content: @Composable () -> Unit) {
    val ctx = rememberPageContext()

    val articleHandle = ctx.markdown?.let { ctx.route.toArticleHandle() }
    val title = if (articleHandle != null) {
        "Docs - ${articleHandle.category.title} - ${articleHandle.article.title}"
    } else "Docs"

    PageLayout(title) {
        Row(Modifier.fillMaxWidth().maxWidth(800.px).gap(1.cssRem).fillMaxHeight().siteText(SiteTextSize.NORMAL)) {
            ListingSideBar()
            Column(Modifier.fillMaxSize()) {
                if (articleHandle != null) {
                    H1(Modifier.align(Alignment.CenterHorizontally).toAttrs()) {
                        Text(articleHandle.article.title)
                    }
                }
                content()
            }
        }
    }
}
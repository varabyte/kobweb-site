package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.navigation.UpdateHistoryMode
import com.varabyte.kobweb.site.model.listing.SITE_LISTING

@Page
@Composable
fun DocsPage(ctx: PageContext) {
    val firstCategory = SITE_LISTING.first()
    val firstArticle = firstCategory.subcategories.first().articles.first()
    ctx.router.tryRoutingTo(firstArticle.route, updateHistoryMode = UpdateHistoryMode.REPLACE)
}
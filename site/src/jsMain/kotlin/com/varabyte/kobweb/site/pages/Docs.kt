package com.varabyte.kobweb.site.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.UpdateHistoryMode
import com.varabyte.kobweb.site.Constants
import com.varabyte.kobweb.site.util.defaultLanguageCode
import com.varabyte.kobweb.site.util.getSiteListing
import kotlinx.browser.localStorage

@Page
@Composable
fun DocsPage() {
    val ctx = rememberPageContext()
    val currentLanguageCode = remember {
        localStorage.getItem(Constants.APP_LOCALE_KEY)
    } ?: Res.defaultLanguageCode
    val firstCategory = getSiteListing(currentLanguageCode).first()
    val firstArticle = firstCategory.subcategories.first().articles.first()
    ctx.router.tryRoutingTo(firstArticle.route, updateHistoryMode = UpdateHistoryMode.REPLACE)
}
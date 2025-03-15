package com.varabyte.kobweb.site.util

import Res
import com.varabyte.kobweb.site.model.listing.Article
import com.varabyte.kobweb.site.model.listing.Category
import com.varabyte.kobweb.site.model.listing.SITE_LISTING_EN
import com.varabyte.kobweb.site.model.listing.SITE_LISTING_ZH

data class Language(val code: String, val displayName: String)

val Res.locales get() = listOf("en", "zh")
val Res.defaultLanguage get() = "English"
val Res.defaultLanguageCode get() = "en"
val Res.localeMap
    get() = mapOf(
        "en" to "English",
        "zh" to "中文",
    )

val Res.localList
    get() = listOf(
        Language("en","English"),
        Language("zh", "中文"),
    )

fun getSiteListing(code: String): List<Category> {
    return when(code) {
        "zh" -> SITE_LISTING_ZH
        else -> SITE_LISTING_EN
    }
}

fun getSiteListing(article: Article): List<Category> {
    return if (article.route.contains("zh")) {
        SITE_LISTING_ZH
    } else {
        SITE_LISTING_EN
    }
}
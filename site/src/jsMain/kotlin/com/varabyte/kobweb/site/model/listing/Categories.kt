package com.varabyte.kobweb.site.model.listing

class Article(val slug: String, val title: String)

class Subcategory(
    val title: String,
    vararg val articles: Article
)

class Category(
    val slug: String,
    val title: String,
    vararg val subcategories: Subcategory
)

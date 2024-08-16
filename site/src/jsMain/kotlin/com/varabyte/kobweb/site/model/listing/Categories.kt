package com.varabyte.kobweb.site.model.listing

class Article(val title: String, val route: String)

class Subcategory(
    val title: String,
    vararg val articles: Article
)

class Category(
    val title: String,
    vararg val subcategories: Subcategory
)

class ArticleHandle(
    val category: Category,
    val subcategory: Subcategory,
    val article: Article,
)

fun List<Category>.findArticle(route: String): ArticleHandle? {
    for (category in this) {
        for (subcategory in category.subcategories) {
            for (article in subcategory.articles) {
                if (article.route == route) {
                    return ArticleHandle(category, subcategory, article)
                }
            }
        }
    }
    return null
}


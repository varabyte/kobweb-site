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


fun List<Category>.findOffsetFrom(articleHandle: ArticleHandle, indexOffset: (Int) -> Int): Article? {
    val articleIndex = articleHandle.subcategory.articles.indexOf(articleHandle.article)
    val subCategoryIndex by lazy { articleHandle.category.subcategories.indexOf(articleHandle.subcategory) }
    val categoryIndex by lazy { this.indexOf(articleHandle.category) }
    return articleHandle.subcategory.articles.getOrNull(indexOffset(articleIndex))
        ?: articleHandle.category.subcategories.getOrNull(indexOffset(subCategoryIndex))?.articles?.first()
        ?: SITE_LISTING.getOrNull(indexOffset(categoryIndex))?.subcategories?.first()?.articles?.first()
}

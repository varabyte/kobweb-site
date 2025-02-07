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

/** Article title or, if set to "", its parent subcategory title which should represent it. */
val Article.titleOrSubcategoryTitle: String get() {
    return title.takeIf { it.isNotEmpty() } ?: SITE_LISTING.findArticle(route)!!.subcategory.title
}

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


fun List<Category>.findArticleNeighbors(articleHandle: ArticleHandle): Pair<Article?, Article?> {
    val articleIndex = articleHandle.subcategory.articles.indexOf(articleHandle.article)
    val subcategoryIndex by lazy { articleHandle.category.subcategories.indexOf(articleHandle.subcategory)  }
    val categoryIndex by lazy { this.indexOf(articleHandle.category) }

    val prev = articleHandle.subcategory.articles.getOrNull(articleIndex - 1)
                ?: articleHandle.category.subcategories.getOrNull(subcategoryIndex - 1)?.articles?.last()
                ?: SITE_LISTING.getOrNull(categoryIndex - 1)?.subcategories?.last()?.articles?.last()

    val next =
            articleHandle.subcategory.articles.getOrNull(articleIndex + 1)
                ?: articleHandle.category.subcategories.getOrNull(subcategoryIndex + 1)?.articles?.first()
                ?: SITE_LISTING.getOrNull(categoryIndex + 1)?.subcategories?.first()?.articles?.first()

    return prev to next
}

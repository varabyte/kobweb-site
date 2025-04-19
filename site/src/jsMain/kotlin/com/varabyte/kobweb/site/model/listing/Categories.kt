package com.varabyte.kobweb.site.model.listing

class Article(val title: String, val route: String, val filePath: String)

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

private class TitleHierarchy(
    val category: String,
    val subcategory: String,
    val article: String,
) {
    companion object {
        fun from(article: Article): TitleHierarchy {
            val listing = SITE_LISTING.findArticle(article.route)!!
            return TitleHierarchy(
                listing.category.title,
                listing.subcategory.title,
                listing.article.title
            )
        }
    }
}

/**
 * The path to this article as a list of strings.
 *
 * For example, ["Concepts", "Presentations"] could be used to generate the final text "Concepts > Presentations"
 */
val Article.breadcrumbs: List<String> get() {
    return with(TitleHierarchy.from(this)) {
        buildList {
            add(category)

            // If the article title is empty, that means the subcategory is essentially the article name, so in that
            // case we shouldn't include the subcategory in the breadcrumbs.
            if (subcategory.isNotEmpty() && article.isNotEmpty()) {
                add(subcategory)
            }
        }
    }
}


/** Article title or, if set to "", its parent subcategory title which should represent it. */
val Article.titleOrSubcategory: String get() {
    return with(TitleHierarchy.from(this)) {
        when {
            article.isNotEmpty() -> article
            else -> subcategory
        }
    }
}

/** Article title, subcategory title, or category title. */
val Article.titleOrFallback: String get() {
    return with(TitleHierarchy.from(this)) {
        when {
            article.isNotEmpty() -> article
            subcategory.isNotEmpty() -> subcategory
            else -> category
        }
    }
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

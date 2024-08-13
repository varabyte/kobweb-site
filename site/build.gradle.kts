import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import kotlinx.html.link
import kotlinx.html.script
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.gradle.configurationcache.extensions.capitalized

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
}

group = "com.varabyte.kobweb.site"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            head.add {
                link {
                    rel = "stylesheet"
                    href = "/highlight.js/styles/dracula.css"
                }
                script {
                    src = "/highlight.js/highlight.min.js"
                }

                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
                link(href = "https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&family=Roboto+Mono&display=swap", rel = "stylesheet")
            }
        }
    }

    markdown {
        handlers {
            val WIDGET_PATH = "com.varabyte.kobweb.site.components.widgets"

            code.set { code ->
                "$WIDGET_PATH.code.CodeBlock(\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\", lang = ${
                    code.info.takeIf { it.isNotBlank() }?.let { "\"$it\"" }
                })"
            }

            inlineCode.set { code ->
                "$WIDGET_PATH.code.InlineCode(\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\")"
            }
        }

        process.set { entries ->
            SiteListingGenerator.generate(this, entries)
        }
    }
}

kotlin {
    configAsKobwebApplication("kobweb-site")
    js {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions.target = "es2015"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.compose.html.core)
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk.core)
                implementation(libs.kobweb.silk.icons.fa)
                implementation(libs.kobwebx.markdown)
             }
        }
    }
}

object SiteListingGenerator {
    class Article(val slug: String)
    class Subcategory(
        val title: String,
        vararg val articles: Article
    ) {
        constructor(vararg articles: Article) : this("", *articles)
    }

    class Category(
        val slug: String,
        val title: String,
        vararg val subcategories: Subcategory
    ) {
        constructor(slug: String, vararg subcategories: Subcategory) :
                this(slug, slug.capitalized(), *subcategories)
    }

    class ArticleMetadata(val title: String)
    class ArticleEntry(val slug: String, val metadata: ArticleMetadata)

    fun List<Category>.find(categorySlug: String, articleSlug: String): Article? {
        return this.asSequence()
            .filter { it.slug == categorySlug }
            .flatMap { it.subcategories.asSequence() }
            .flatMap { it.articles.asSequence() }
            .firstOrNull { it.slug == articleSlug }
    }

    private val SITE_LISTING = buildList {
        add(
            Category(
                "guides",
                Subcategory(
                    "First Steps",
                    Article("getting-started"),
                    Article("getting-kobweb"),
                    Article("installing-kobweb"),
                ),
            )
        )

        add(
            Category(
                "widgets",
                Subcategory(
                    "Forms",
                    Article("button"),
                ),
                Subcategory(
                    "Overlay",
                    Article("tooltip"),
                ),
            )
        )

        add(
            Category(
                "tutorials",
                Subcategory(
                    Article("create-first-site")
                ),
            )
        )
    }

    fun generate(scope: MarkdownBlock.ProcessScope, entries: List<MarkdownEntry>) {
        scope._generate(entries.filter { it.route.startsWith("/docs/") })
    }

    @Suppress("FunctionName") // Underscore avoids ambiguity error
    private fun MarkdownBlock.ProcessScope._generate(entries: List<MarkdownEntry>) {
        val requiredFields = listOf("title")
        val discoveredMetadata = mutableMapOf<String, MutableList<ArticleEntry>>()

        entries.forEach { entry ->
            val (title) = requiredFields
                .map { key -> entry.frontMatter[key]?.singleOrNull() }
                .takeIf { values -> values.all { it != null } }
                ?.requireNoNulls()
                ?: run {
                    println("Skipping ${entry.route} in the listing as it is missing required frontmatter fields (one of $requiredFields)")
                    return@forEach
                }

            val articleFile = File(entry.filePath)
            val categorySlug = articleFile.parentFile.name
            discoveredMetadata.getOrPut(categorySlug) { mutableListOf() }
                .add(
                    ArticleEntry(
                        entry.route.substringAfterLast('/'),
                        ArticleMetadata(title)
                    ).also { article ->
                        if (SITE_LISTING.find(categorySlug, article.slug) == null) {
                            throw GradleException(
                                "${entry.route} needs an entry (slug: \"${article.slug}\") in `SITE_LISTING`."
                            )
                        }
                    }
                )
        }

        SITE_LISTING.forEach { category ->
            category.subcategories.forEach { subcategory ->
                subcategory.articles.forEach { article ->
                    if (discoveredMetadata[category.slug]?.any { entry -> entry.slug == article.slug } != true) {
                        throw GradleException(
                            "`SITE_LISTING` contains entry for \"${category.slug}/${article.slug}\" but no found article satisfies it."
                        )
                    }
                }
            }
        }

        generateKotlin("com/varabyte/kobweb/site/model/listing/SiteListing.kt", buildString {
            val indent = "   "
            appendLine(
                """
                    package com.varabyte.kobweb.site.model.listing

                    // DO NOT EDIT THIS FILE BY HAND! IT IS GETTING AUTO-GENERATED BY GRADLE
                    // Instead, edit the SITE_LISTING constant in `build.gradle.kts` and re-run the task.

                    val SITE_LISTING = buildList {
                    """.trimIndent()
            )

            SITE_LISTING.forEach { category ->
                appendLine("${indent}add(")
                appendLine("${indent}${indent}Category(")
                appendLine("${indent}${indent}${indent}\"${category.slug}\",")
                appendLine("${indent}${indent}${indent}\"${category.title}\",")
                category.subcategories.forEach { subcategory ->
                    appendLine("${indent}${indent}${indent}Subcategory(")
                    appendLine("${indent}${indent}${indent}${indent}\"${subcategory.title}\",")
                    subcategory.articles.forEach { article ->
                        val metadata =
                            discoveredMetadata.getValue(category.slug).first { it.slug == article.slug }.metadata
                        appendLine("${indent}${indent}${indent}${indent}Article(\"${article.slug}\", \"${metadata.title}\"),")
                    }
                    appendLine("${indent}${indent}${indent}),")
                }
                appendLine("${indent}${indent})")
                appendLine("${indent})")
            }

            appendLine(
                """
                    }

                    fun List<Category>.findArticle(categorySlug: String, articleSlug: String): Article? {
                       return this.asSequence()
                          .filter { it.slug == categorySlug }
                          .flatMap { it.subcategories.asSequence() }
                          .flatMap { it.articles.asSequence() }
                          .firstOrNull { it.slug == articleSlug }
                    }
                    """.trimIndent()
            )
        })
    }
}

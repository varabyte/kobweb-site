import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import kotlinx.html.link
import kotlinx.html.script
import org.commonmark.node.Text
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
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
                link(
                    href = "https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&family=Roboto+Mono&display=swap",
                    rel = "stylesheet"
                )
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

            // Automatically convert GitHub-style callouts into a custom widget
            // TODO: this likely has broken edge cases, but it's good enough for now
            blockquote.set { blockQuote ->
                blockQuote.firstChild.firstChild?.let { firstChild ->
                    firstChild as Text
                    val regex = "\\[!(.*)\\] ".toRegex()
                    val match = regex.find(firstChild.literal) ?: return@let null
                    firstChild.literal = firstChild.literal.substringAfter(match.value)
                    """
                    com.varabyte.kobweb.site.components.widgets.GitHubStyleCallout(
                        type = com.varabyte.kobweb.site.components.widgets.CalloutType.${match.groupValues[1]},
                    )
                    """.trimIndent()
                } ?: "com.varabyte.kobweb.compose.dom.GenericTag(\"blockquote\")"
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
        commonMain.dependencies {
            implementation(libs.compose.runtime)
        }

        jsMain.dependencies {
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk.core)
            implementation(libs.kobweb.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
        }
    }
}

object SiteListingGenerator {
    private const val DOCS_PREFIX = "docs/"

    fun generate(scope: MarkdownBlock.ProcessScope, entries: List<MarkdownEntry>) {
        scope._generate(entries.filter { it.filePath.startsWith(DOCS_PREFIX) })
    }

    private fun MarkdownEntry.toPath() = "/" + filePath.removePrefix(DOCS_PREFIX).removeSuffix(".md")

    private data class RouteParts(
        val category: String,
        val subcategory: String,
        val slug: String,
    )

    private fun MarkdownEntry.toRouteParts() = with(this.toPath().split('/').dropWhile { it.isEmpty() }) {
        require(this.size == 3) { "Expected category, subcategory, and slug; got \"${this.joinToString("/")}\"" }
        RouteParts(
            category = get(0),
            subcategory = get(1),
            slug = get(2).camelCaseToKebabCase()
        )
    }

    @Suppress("DEPRECATION") // The suggestion to replace `capitalize` with is awful
    private fun String.convertSlugToTitle() = split('-').joinToString(" ") { it.capitalize() }

    @Suppress("FunctionName") // Underscore avoids ambiguity error
    private fun MarkdownBlock.ProcessScope._generate(entries: List<MarkdownEntry>) {
        val optionalFields = listOf("follows")

        // e.g. "/guides/first-steps/GettingStarted.md"
        // Should end up being exactly one match; if more, someone is missing metadata in their markdown file
        val initialArticles = mutableListOf<MarkdownEntry>()
        // e.g. "/guides/first-steps/GettingStarted.md" -> "/guides/first-steps/InstallingKobweb.md"
        // e.g. "/guides/first-steps/" -> "/widgets/forms/Button.md"
        val followingMap = mutableMapOf<String, MutableList<MarkdownEntry>>()

        entries.forEach { entry ->
            val (follows) = optionalFields.map { key -> entry.frontMatter[key]?.singleOrNull() }

            if (follows != null) {
                // "/a/b/c.md" -> pass through
                // "z.md" -> "/a/b/z.md" (assume sibling of current entry)
                followingMap.getOrPut(
                    if (follows.contains('/')) {
                        follows
                    } else {
                        "${entry.toPath().substringBeforeLast('/')}/$follows"
                    }
                ) { mutableListOf() }.add(entry)
            } else {
                initialArticles.add(entry)
            }
        }

        if (initialArticles.size != 1) {
            println("There should only be one starting article, but one of these articles are missing a `follows` frontmatter value: ${initialArticles.joinToString { it.toPath() }}")
        }

        val orderedArticleList = mutableListOf<MarkdownEntry>()
        initialArticles.lastOrNull()?.let { initialArticle ->
            val nextEntries = mutableListOf(initialArticle)
            while (nextEntries.isNotEmpty()) {
                val nextEntry = nextEntries.removeAt(0)
                orderedArticleList.add(nextEntry)
                val nextPath = nextEntry.toPath()
                val followedBy = followingMap[nextPath] ?: followingMap[nextPath.substringBeforeLast('/') + "/"]
                if (followedBy == null) {
                    println("No article is following \"$nextPath\". This is OK if it is the last article in the docs.")
                    continue
                }
                if (followedBy.size != 1) {
                    println("Only one article should ever follow another. For \"$nextPath\", found multiple (so please fix one): ${followedBy.map { it.toPath() }}")
                }

                nextEntries.addAll(followedBy)
            }
        }

        generateKotlin("com/varabyte/kobweb/site/model/listing/SiteListing.kt", buildString {
            val indent = "\t"
            appendLine(
                """
                    package com.varabyte.kobweb.site.model.listing

                    // DO NOT EDIT THIS FILE BY HAND! IT IS GETTING AUTO-GENERATED BY GRADLE
                    // Instead, edit the SITE_LISTING constant in `build.gradle.kts` and re-run the task.

                    val SITE_LISTING = buildList {
                    """.trimIndent()
            )


            val routePartsMap = orderedArticleList.associateWith { it.toRouteParts() }
            val articleTree = mutableMapOf<String, MutableMap<String, MutableList<MarkdownEntry>>>()

            orderedArticleList.forEach { entry ->
                val routeParts = routePartsMap.getValue(entry)
                articleTree
                    .getOrPut(routeParts.category) { mutableMapOf() }
                    .getOrPut(routeParts.subcategory) { mutableListOf() }
                    .add(entry)
            }


            articleTree.forEach { (category, rest) ->
                appendLine("${indent}add(")
                appendLine("${indent}${indent}Category(")
                appendLine("${indent}${indent}${indent}\"${category.convertSlugToTitle()}\",")

                rest.forEach { (subcategory, articles) ->
                    appendLine("${indent}${indent}${indent}Subcategory(")
                    appendLine("${indent}${indent}${indent}${indent}\"${subcategory.convertSlugToTitle()}\",")
                    articles.forEach { article ->
                        val routeParts = routePartsMap.getValue(article)
                        val title = article.frontMatter["title"]?.singleOrNull() ?: routeParts.slug.convertSlugToTitle()
                        appendLine("${indent}${indent}${indent}${indent}Article(\"$title\", \"${article.route}\"),")
                    }
                    appendLine("${indent}${indent}${indent}),")
                }

                appendLine("${indent}${indent})")
                appendLine("${indent})")
            }

            appendLine(
                """
                    }
                """.trimIndent()
            )
        })
    }
}

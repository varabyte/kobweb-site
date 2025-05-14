import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import com.varabyte.kobweb.common.text.isSurrounded
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.handlers.SilkCalloutBlockquoteHandler
import kotlinx.html.link
import kotlinx.html.script

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
                    href = "/prism/prism.css"
                }
                script {
                    src = "/prism/prism.js"
                }
                link {
                    rel = "stylesheet"
                    href = "https://cdn.jsdelivr.net/npm/@docsearch/css@3"
                }
            }
        }
    }

    markdown {
        defaultLayout.set(".components.layouts.DocsLayout")
        imports.addAll(
            ".components.widgets.filesystem.Folders",
            ".components.widgets.navigation.DocsLink",
            ".components.widgets.navigation.DocsAside",
        )

        handlers {
            val WIDGET_PATH = "com.varabyte.kobweb.site.components.widgets"

            code.set { code ->
                var lang: String? = null
                var lines: String? = null
                var label: String? = null
                var editingLabel = false

                code.info.split(" ").filter { it.isNotBlank() }.forEach { infoPart ->
                    if (editingLabel) {
                        label += " "
                        if (infoPart.endsWith("\"")) {
                            label += infoPart.removeSuffix("\"")
                            editingLabel = false
                        } else {
                            label += infoPart
                        }
                    } else {
                        if (infoPart.isSurrounded("\"")) {
                            label = infoPart.removeSurrounding("\"")
                        } else if (infoPart.startsWith("\"")) {
                            label = infoPart.removePrefix("\"")
                            editingLabel = true
                        } else if (infoPart.first().isDigit()) {
                            lines = infoPart
                        } else {
                            lang = infoPart
                        }
                    }
                }

                buildString {
                    append("$WIDGET_PATH.code.CodeBlock(\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\"")
                    if (lang != null) {
                        append(", lang = \"$lang\"")
                    }
                    if (lines != null) {
                        append(", highlightLines = \"$lines\"")
                    }
                    if (label != null) {
                        append(", label = \"$label\"")
                    }
                    append(")")
                }
            }

            inlineCode.set { code ->
                "$WIDGET_PATH.code.InlineCode(\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\")"
            }

            val baseHeadingHandler = heading.get()
            heading.set { heading ->
                // Convert a heading to include its ID
                // e.g. <h2>My Heading</h2> becomes <h2 id="my-heading">My Heading</h2>
                val result = baseHeadingHandler.invoke(this, heading)
                // ID guaranteed to be created as side effect of base handler
                val id = data.getValue(MarkdownHandlers.DataKeys.HeadingIds).getValue(heading)

                // HoverLink is a widget that will show a link icon (linking back to the header) on hover
                // This is a useful way to let people share a link to a specific header
                heading.appendChild(KobwebCall(".components.widgets.navigation.HoverLink(\"#$id\")"))

                result
            }

            blockquote.set(SilkCalloutBlockquoteHandler(labels = mapOf("QUOTE" to "")))
        }

        process.set { entries ->
            SiteListingGenerator.generate(this, entries)
        }
    }
}

kotlin {
    configAsKobwebApplication("kobweb-site")
    js {
        compilerOptions.target = "es2015"
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk.core)
            implementation(libs.kobweb.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
            implementation(npm("@docsearch/js", "3.9.0"))
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
        require(this.size == 2 || this.size == 3) {
            "Expected category, subcategory (optional), and slug; got \"${this.joinToString("/")}\""
        }
        RouteParts(
            category = get(0),
            subcategory = if (this.size == 3) get(1) else "",
            slug = last().camelCaseToKebabCase()
        )
    }

    // https://en.wikipedia.org/wiki/Title_case
    // We'll go case by case for now but we can improve this later if necessary
    private val LOWERCASE_TITLE_WORDS = setOf(
        "a",
        "an",
        "and",
        "at",
        "is",
        "the",
        "to",
        "us",
        "with",
    )

    @Suppress("DEPRECATION") // The suggestion to replace `capitalize` with is awful
    private fun String.convertSlugToTitle() = split('-')
        .joinToString(" ") { word ->
            if (word in LOWERCASE_TITLE_WORDS) word.lowercase() else word.capitalize()
        }
        .takeIf { it != "Index" } ?: ""

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
            println("e: There should only be one starting article, but one of these articles are missing a `follows` frontmatter value: ${initialArticles.map { it.toPath() }}")
        }

        val orderedArticleList = mutableListOf<MarkdownEntry>()
        initialArticles.lastOrNull()?.let { initialArticle ->
            val nextEntries = mutableListOf(initialArticle)
            while (nextEntries.isNotEmpty()) {
                val nextEntry = nextEntries.removeAt(0)
                orderedArticleList.add(nextEntry)
                val nextPath = nextEntry.toPath()
                val followedBy = followingMap[nextPath] ?: followingMap[nextPath.substringBeforeLast('/') + "/"]
                if (followedBy == null) continue
                if (followedBy.size != 1) {
                    println("e: Only one article should ever follow another. For \"$nextPath\", found multiple (so please fix one): ${followedBy.map { it.toPath() }}")
                }

                nextEntries.addAll(followedBy)
            }
        }

        (entries.toSet() - orderedArticleList).forEach { orphanedEntry ->
            println("e: Orphaned markdown file (probably a bad `Follows` value): ${orphanedEntry.toPath()}.md")
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

            val articleTree = orderedArticleList
                .map { it to it.toRouteParts() }
                .groupBy { it.second.category }

            println("Article tree:\n")
            articleTree.forEach { (category, rest) ->
                appendLine("${indent}add(")
                appendLine("${indent}${indent}Category(")
                appendLine("${indent}${indent}${indent}\"${category.convertSlugToTitle()}\",")
                println("- ${category.convertSlugToTitle()}")

                rest.groupBy { it.second.subcategory }.forEach { (subcategory, articles) ->
                    appendLine("${indent}${indent}${indent}Subcategory(")
                    appendLine("${indent}${indent}${indent}${indent}\"${subcategory.convertSlugToTitle()}\",")
                    if (subcategory.isNotEmpty()) {
                        println("${indent}- ${subcategory.convertSlugToTitle()}")
                    }

                    articles.forEach { (article, routeParts) ->
                        val title = article.frontMatter["title"]?.singleOrNull() ?: routeParts.slug.convertSlugToTitle()
                        appendLine("${indent}${indent}${indent}${indent}Article(\"$title\", \"${article.route}\"),")
                        if (title.isNotEmpty()) {
                            if (subcategory.isNotEmpty()) print(indent)
                            println("${indent}- $title")
                        }
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

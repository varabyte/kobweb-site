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
        val filePath: String,
        val category: String,
        val subcategory: String,
        val slug: String,
    )

    private fun MarkdownEntry.toRouteParts() = with(this.toPath().split('/').dropWhile { it.isEmpty() }) {
        require(this.size == 2 || this.size == 3) {
            "Expected category, subcategory (optional), and slug; got \"${this.joinToString("/")}\""
        }
        RouteParts(
            filePath = this@toRouteParts.filePath,
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
        val pathsOrdered = listOf(
            "getting-started/WhatIsKobweb.md",
            "getting-started/Ide.md",
            "getting-started/Videos.md",
            "getting-started/GettingKobweb.md",
            "getting-started/KobwebProject.md",
            "getting-started/GradleAndMavenArtifacts.md",

            "concepts/foundation/Index.md",
            "concepts/foundation/ProjectStructure.md",
            "concepts/foundation/Routing.md",
            "concepts/foundation/ApplicationRoot.md",
            "concepts/foundation/Layouts.md",
            "concepts/foundation/Exporting.md",
            "concepts/foundation/ApplicationGlobals.md",
            "concepts/foundation/PersistingState.md",
            "concepts/foundation/PageMetadata.md",
            "concepts/foundation/BasePath.md",
            "concepts/foundation/Markdown.md",
            "concepts/foundation/Workers.md",
            "concepts/foundation/Redirects.md",

            "concepts/presentation/Index.md",
            "concepts/presentation/StylingHtmlElements.md",
            "concepts/presentation/Silk.md",
            "concepts/presentation/LearningCss.md",
            "concepts/presentation/CssLayers.md",
            "concepts/presentation/CssNumericValue.md",

            "concepts/server/Index.md",
            "concepts/server/Fullstack.md",
            "concepts/server/KobwebServerPlugins.md",

            "guides/Index.md",
            "guides/Debugging.md",
            "guides/CustomFonts.md",
            "guides/SharingDataObjects.md",
            "guides/ExistingProject.md",
            "guides/ExistingBackend.md",
            "guides/GeneratingCode.md",
            "guides/GitHubWorkflowExport.md",

            "community/Index.md",
            "community/ConnectingWithUs.md",
            "community/SubmittingIssuesAndFeedback.md",
            "community/SupportingTheProject.md",
            "community/Contributors.md",
            "community/Articles.md",
            "community/Testimonials.md",
        ).map { "docs/$it" }

        val sourcePathToEntry = entries.associateBy { it.filePath }.toMutableMap()

        generateKotlin("com/varabyte/kobweb/site/model/listing/SiteListing.kt", buildString {
            val indent = "\t"
            appendLine(
                """
                    package com.varabyte.kobweb.site.model.listing

                    // DO NOT EDIT THIS FILE BY HAND! IT IS GETTING AUTO-GENERATED BY GRADLE
                    // Instead, edit the logic in `site/build.gradle.kts` and re-run the task.

                    val SITE_LISTING = buildList {
                    """.trimIndent()
            )

            println("Article tree:\n")

            fun closeSubcategory() {
                appendLine("${indent}${indent}${indent}),")
            }

            fun closeCategory() {
                appendLine("${indent}${indent})")
                appendLine("${indent})")
            }

            pathsOrdered
                .mapNotNull { filePath ->
                    sourcePathToEntry.remove(filePath).also {
                        if (it == null) {
                            throw GradleException("e: $filePath specified in `build.gradle.kts` but not found in the markdown files")
                        }
                    }
                }
                .let { orderedEntries ->
                    var currCategory: String? = null
                    var currSubcategory: String? = null

                    println("Article tree:\n")
                    orderedEntries.forEach { entry ->
                        val routeParts = entry.toRouteParts()

                        val newCategory = routeParts.category != currCategory
                        val newSubcategory = newCategory || routeParts.subcategory != currSubcategory

                        if (currSubcategory != null && newSubcategory) {
                            closeSubcategory()
                        }
                        if (currCategory != null && newCategory) {
                            closeCategory()
                        }

                        if (newCategory) {
                            currCategory = routeParts.category
                            appendLine("${indent}add(")
                            appendLine("${indent}${indent}Category(")
                            appendLine("${indent}${indent}${indent}\"${routeParts.category.convertSlugToTitle()}\",")
                            println("- ${routeParts.category.convertSlugToTitle()}")
                        }

                        if (newSubcategory) {
                            currSubcategory = routeParts.subcategory
                            appendLine("${indent}${indent}${indent}Subcategory(")
                            appendLine("${indent}${indent}${indent}${indent}\"${routeParts.subcategory.convertSlugToTitle()}\",")
                            if (routeParts.subcategory.isNotEmpty()) {
                                println("${indent}- ${routeParts.subcategory.convertSlugToTitle()}")
                            }
                        }

                        val title = entry.frontMatter["title"]?.singleOrNull() ?: routeParts.slug.convertSlugToTitle()
                        appendLine("${indent}${indent}${indent}${indent}Article(\"$title\", \"${entry.route}\", \"${routeParts.filePath}\"),")
                        if (title.isNotEmpty()) {
                            if (routeParts.subcategory.isNotEmpty()) print(indent)
                            println("${indent}- $title")
                        }
                    }
                }

            closeSubcategory()
            closeCategory()

            appendLine(
                """
                    }
                """.trimIndent()
            )

            sourcePathToEntry.keys.forEach { filePath ->
                throw GradleException("e: $filePath exists but not specified in `build.gradle.kts`. Either add it or delete the file.")
            }
        })
    }
}

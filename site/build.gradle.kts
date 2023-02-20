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

//        process.set { entries ->
//            generateDocs(entries)
//        }
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

class MarkdownVisitor : AbstractVisitor() {
    private val _frontMatter = mutableMapOf<String, List<String>>()
    val frontMatter: Map<String, List<String>> = _frontMatter

    override fun visit(customBlock: CustomBlock) {
        if (customBlock is YamlFrontMatterBlock) {
            val yamlVisitor = YamlFrontMatterVisitor()
            customBlock.accept(yamlVisitor)
            _frontMatter.putAll(yamlVisitor.data)
        }
    }
}

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

private val SITE_LISTING = buildList {
    add(
        Category(
            "guides",
            Subcategory(
                "First Steps",
                Article("gettingstarted"),
                Article("gettingkobweb"),
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
                Article("createfirstsite")
            ),
        )
    )
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

fun MarkdownBlock.ProcessScope.generateDocs(entries: List<MarkdownEntry>) {
    val entries = entries.filter { it.route.startsWith("/docs/") }
    println("HELLO WITH ${entries.size} ENTRIES")
}

val generateSiteListingTask = task("generateSiteListing") {
    group = "kobweb site"
    val LISTINGS_INPUT_DIR = "src/jsMain/resources/markdown/docs"
    val LISTINGS_OUTPUT_FILE = "src/jsMain/kotlin/com/varabyte/kobweb/site/model/listing/SiteListing.kt"

    val discoveredMetadata = mutableMapOf<String, MutableList<ArticleEntry>>()

    inputs.dir(LISTINGS_INPUT_DIR)
    outputs.file(layout.projectDirectory.file(LISTINGS_OUTPUT_FILE))

    doLast {
        val parser = kobweb.markdown.features.createParser()
        val root = file(LISTINGS_INPUT_DIR)
        fileTree(root).forEach { mdArticle ->
            val rootNode = parser.parse(mdArticle.readText())
            val visitor = MarkdownVisitor()

            rootNode.accept(visitor)

            val fm = visitor.frontMatter
            val requiredFields = listOf("title")
            val (title) = requiredFields
                .map { key -> fm[key]?.singleOrNull() }
                .takeIf { values -> values.all { it != null } }
                ?.requireNoNulls()
                ?: run {
                    println("Skipping $mdArticle in the listing as it is missing required frontmatter fields (one of $requiredFields)")
                    return@forEach
                }

            val categorySlug = mdArticle.parentFile.name
            discoveredMetadata.getOrPut(categorySlug) { mutableListOf() }
                .add(
                    ArticleEntry(
                        mdArticle.nameWithoutExtension.lowercase(),
                        ArticleMetadata(title)
                    ).also { entry ->
                        if (SITE_LISTING.find(categorySlug, entry.slug) == null) {
                            throw GradleException(
                                "$mdArticle needs an entry (slug: \"${entry.slug}\") in `SITE_LISTING`."
                            )
                        }
                    }
                )
        }

        project.layout.projectDirectory.file(LISTINGS_OUTPUT_FILE).asFile.let { siteListing ->
            val indent = "   "

            siteListing.parentFile.mkdirs()
            siteListing.writeText(buildString {
                appendLine(
                    """
                    package com.varabyte.kobweb.site.model.listing

                    // DO NOT EDIT THIS FILE BY HAND! IT GETS AUTO-GENERATED BY `./gradlew $name`
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
                            val metadata = discoveredMetadata.getValue(category.slug).first { it.slug == article.slug }.metadata
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

            println("Generated ${siteListing.absolutePath}")
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
    }
}

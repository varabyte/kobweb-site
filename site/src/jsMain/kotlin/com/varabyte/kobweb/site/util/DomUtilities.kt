package com.varabyte.kobweb.site.util

import org.w3c.dom.*

fun HTMLCollection.walk(onEach: (Element) -> Unit) {
    asList().forEach { child ->
        onEach(child)
        child.children.walk(onEach)
    }
}

fun NodeList.walk(onEach: (Node) -> Unit) {
    asList().forEach { node ->
        onEach(node)
        node.childNodes.walk(onEach)
    }
}

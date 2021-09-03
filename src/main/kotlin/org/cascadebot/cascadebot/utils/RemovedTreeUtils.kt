package org.cascadebot.cascadebot.utils

import org.cascadebot.cascadebot.data.database.DataHandler

fun DataHandler.RemovedTree.ifContainsAndLast(key: String, consumer: (DataHandler.RemovedTree, Boolean) -> Unit) {
    val tree = this.getChild(key);
    if (tree != null) {
        consumer(tree, tree.children.size == 0)
    }
}

fun DataHandler.RemovedTree.ifContains(key: String, consumer: (DataHandler.RemovedTree) -> Unit) {
    val tree = this.getChild(key);
    if (tree != null) {
        consumer(tree)
    }
}
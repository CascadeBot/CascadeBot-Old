package org.cascadebot.cascadebot.utils

import java.util.Collections

class ChangeList<T>(val baseCollection: Set<T>) {

    val addedItems
        get() = added.toSet()
    val removedItems
        get() = removed.toSet()

    private val added = Collections.synchronizedSet<T>(HashSet())
    private val removed = Collections.synchronizedSet<T>(HashSet())

    fun add(element: T): Boolean {
        if (!removed.remove(element)) {
            added.add(element)
        }
        return !baseCollection.contains(element)
    }

    fun remove(element: T): Boolean {
        if (!added.remove(element)) {
            removed.add(element)
        }
        return baseCollection.contains(element)
    }

    fun applyChanges(): Set<T> {
        val baseElements = baseCollection.toMutableSet()
        baseElements.addAll(added)
        baseElements.removeAll(removed)
        return baseElements
    }

}
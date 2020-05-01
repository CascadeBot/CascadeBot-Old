package org.cascadebot.cascadebot.utils

import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.Set
import kotlin.collections.toMutableSet
import kotlin.collections.toSet

class ChangeList<T>() {

    val addedItems
        get() = added.toSet()
    val removedItems
        get() = removed.toSet()

    private val added = Collections.synchronizedSet<T>(HashSet())
    private val removed = Collections.synchronizedSet<T>(HashSet())

    fun add(element: T): Boolean {
        if (!removed.remove(element)) {
            return added.add(element)
        }
        return true
    }

    fun remove(element: T): Boolean {
        if (!added.remove(element)) {
            return removed.add(element)
        }
        return true
    }

    fun applyChanges(baseCollection: Set<T>): Set<T> {
        val baseElements = baseCollection.toMutableSet()
        baseElements.addAll(added)
        baseElements.removeAll(removed)
        return baseElements
    }

}
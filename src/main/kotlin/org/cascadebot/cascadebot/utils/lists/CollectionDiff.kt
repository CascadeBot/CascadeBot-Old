/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.lists

class CollectionDiff<T>(originalList: Collection<T>, newList: Collection<T>) {

    private val _added: MutableList<T> = ArrayList()

    private val _removed: MutableList<T> = ArrayList()

    val added
        get() = _added.toList()

    val removed
        get() = _removed.toList()

    init {
        val addedDiff = newList.toMutableList()

        for (item in originalList) {
            val iter = addedDiff.iterator()
            while (iter.hasNext()) {
                if (iter.next()?.equals(item) == true) {
                    iter.remove()
                    break
                }
            }
        }

        _added.addAll(addedDiff)

        val removedDiff = originalList.toMutableList()

        for (item in newList) {
            val iter = removedDiff.iterator()
            while (iter.hasNext()) {
                if (iter.next()?.equals(item) == true) {
                    iter.remove()
                    break
                }
            }
        }

        _removed.addAll(removedDiff)
    }

}
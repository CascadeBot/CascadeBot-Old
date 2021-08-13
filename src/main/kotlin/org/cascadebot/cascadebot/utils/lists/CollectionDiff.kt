/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.lists

import org.cascadebot.cascadebot.utils.diff.Diff

class CollectionDiff<T>(originalList: Collection<T>, newList: Collection<T>, comparator: Comparator<T>?): Diff {

    constructor(originalList: Collection<T>, newList: Collection<T>): this(originalList, newList, null)

    private val _added: MutableList<T> = ArrayList()

    private val _removed: MutableList<T> = ArrayList()

    private val _inBoth: MutableList<T> = ArrayList()

    val added
        get() = _added.toList()

    val removed
        get() = _removed.toList()

    val inBoth
        get() = _inBoth.toList()

    init {
        val addedDiff = newList.toMutableList()

        for (item in originalList) {
            val iter = addedDiff.iterator()
            while (iter.hasNext()) {
                if (comparator == null) {
                    if (iter.next() == item) {
                        iter.remove()
                        break
                    }
                } else {
                    if (comparator.compare(iter.next(), item) == 0) {
                        iter.remove()
                        break
                    }
                }
            }
        }

        _added.addAll(addedDiff)

        val removedDiff = originalList.toMutableList()

        for (item in newList) {
            val iter = removedDiff.iterator()
            while (iter.hasNext()) {
                if (comparator == null) {
                    if (iter.next() == item) {
                        iter.remove()
                        break
                    }
                } else {
                    if (comparator.compare(iter.next(), item) == 0) {
                        iter.remove()
                        break
                    }
                }
            }
        }

        _removed.addAll(removedDiff)

        // TODO account for duplicated
        val both = originalList.toMutableList()
        both.retainAll(newList)

        _inBoth.addAll(both)

    }

}
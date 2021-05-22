/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.lists

class CollectionDiff<T>(originalList: Collection<T>, newList: Collection<T>) {

    val added: MutableList<T> = ArrayList()
    val removed: MutableList<T> = ArrayList()

    init {
        for (obj in originalList) {
            if (!newList.contains(obj)) {
                removed.add(obj)
            }
        }
        for (obj in newList) {
            if (!originalList.contains(obj)) {
                added.add(obj)
            }
        }
    }

}
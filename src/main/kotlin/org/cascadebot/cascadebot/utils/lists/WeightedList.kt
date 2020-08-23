/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.lists

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.random.Random

class WeightedList<T : Any>(seed: Long? = null) {

    @Transient
    internal val random: Random = if (seed == null) Random.Default else Random(seed)

    @Transient
    private val lock = ReentrantReadWriteLock()

    private val internalList: MutableList<WeightPair<T>> = mutableListOf()

    val totalWeight
        get() = lock.read { internalList.sumBy { it.weight } }

    @JvmOverloads
    fun add(item: T, weight: Int = 1) {
        val existingItem = internalList.find { it.item == item }
        if (existingItem != null) {
            remove(item)
            val newWeight = existingItem.weight + weight
            // Create a new pair with the existing weight + the new weight
            lock.write {
                internalList.add(WeightPair(item, newWeight))
            }
        } else {
            lock.write {
                internalList.add(WeightPair(item, weight))
            }
        }
    }

    fun remove(item: T) {
        lock.write {
            internalList.removeIf { it.item == item }
        }
    }

    fun remove(position: Int): T? {
        return lock.write {
            internalList.removeAt(position).item
        }
    }

    fun getItemWeight(position: Int): Int {
        return lock.read { internalList[position].weight }
    }

    fun setItemWeight(position: Int, weight: Int) {
        lock.write { internalList[position] = internalList[position].copy(weight = weight) }
    }

    operator fun get(position: Int): WeightPair<T> {
        return lock.read { internalList[position] }
    }

    operator fun set(position: Int, value: T) {
        lock.write { internalList[position] = internalList[position].copy(item = value) }
    }

    fun get(obj: T): WeightPair<T>? {
        return lock.read { internalList.find { it.item == obj } }
    }

    fun indexOf(obj: T): Int {
        lock.read {
            internalList.forEachIndexed { index, pair -> if (pair.item == obj) return index }
        }
        return -1
    }

    val items: List<T>
        get() = lock.read { internalList.mapNotNull { it.item }.toList() }

    val itemsAndWeighting: List<WeightPair<T>>
        get() = lock.read { internalList.toList() }

    val size: Int
        get() = lock.read { internalList.size }

    // Gets a number between 1 and totalWeight (Inclusive)
    val randomItem: T?
        get() = lock.read {
            if (totalWeight == 0) return@read null

            // Gets a number between 1 and totalWeight (Inclusive)
            var selection: Int = random.nextInt(totalWeight) + 1
            for (weightedItem: WeightPair<T> in internalList) {
                selection -= weightedItem.weight
                if (selection <= 0) {
                    return weightedItem.item
                }
            }
            null
        }

    fun getItemProportion(position: Int): Double {
        return lock.read {
            getItemWeight(position).toDouble() / totalWeight.toDouble()
        }
    }

    fun clear() {
        lock.write {
            internalList.clear()
        }
    }

    data class WeightPair<T>(val item: T?, val weight: Int) {

        @Suppress("unused") // Needed for MongoDB
        private constructor() : this(null, 0)

    }
}

fun <T : Any> WeightedList<List<T>>.randomListItem(): T? {
    return this.randomItem?.random(this.random)
}

/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.lists

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.random.Random

/**
 * A weighted list that weights items based on a **positive non-zero** number. This is useful when having
 * a list of options that need to be randomly picked with a higher weighting (or preference) towards
 * some options.
 *
 * Each item's proportion is based on its weight as a proportion of the sum of the overall weights.
 * For example, if a item has a weight of 1 and the sum of the weights is 10, the item's proportion
 * is 0.1 or 10%.
 *
 * Based on this proportion, the random item will pick an item approximately relating to the item's
 * proportion (Also could be called the "chance" that the item would be picked)
 *
 * If all items have a weight of 1, then all items will have the same chance of being picked.
 *
 * This list is internally backed by a [MutableList].
 *
 * @param T The type to store in the list.
 * @param seed The seed to use for the Random number generator. If this is null, the [Random.Default]
 * generator is used.
 */
class WeightedList<T : Any>(seed: Long? = null) {

    @Transient
    internal val random: Random = if (seed == null) Random.Default else Random(seed)

    @Transient
    private val lock = ReentrantReadWriteLock()

    private val internalList: MutableList<WeightPair<T>> = mutableListOf()

    /**
     * The sum of all the weights on each object in this list.
     *
     * Returns 0 if there are no items in the list.
     */
    val totalWeight
        get() = lock.read { internalList.sumBy { it.weight } }

    /**
     * Adds an item to this weighted list. If the item is already present in the list,
     * the weight specified (or 1 if not specified) is added to the weight of the item
     * already in the list.
     *
     * @param item The item to add to the list.
     * @param weight The weight for the item. The default value for this is 1.
     */
    @JvmOverloads
    fun add(item: T, weight: Int = 1) {
        require(weight > 0) { "Weight should be 1 or greater" }
        val existingItem = internalList.find { it.item == item }
        if (existingItem != null) {
            remove(item)
            // Create a new pair with the existing weight + the new weight
            val newWeight = existingItem.weight + weight
            lock.write {
                internalList.add(WeightPair(item, newWeight))
            }
        } else {
            lock.write {
                internalList.add(WeightPair(item, weight))
            }
        }
    }

    /**
     * Removes an item from the list.
     *
     * @param item The item to remove from the list.
     */
    fun remove(item: T) {
        lock.write {
            internalList.removeIf { it.item == item }
        }
    }

    /**
     * Removes an item from the list at a specific position.
     *
     * @param position The position at which to remove the item.
     * @return The item that was removed from the list.
     * @throws IndexOutOfBoundsException if the position does not exist in this list.
     */
    fun remove(position: Int): T? {
        return lock.write {
            internalList.removeAt(position).item
        }
    }

    /**
     * Returns the weight for an item at the position.
     *
     * @param position The position of the item.
     * @return The weight of the item at the position.
     * @throws IndexOutOfBoundsException if the position does not exist in this list.
     */
    fun getItemWeight(position: Int): Int {
        return lock.read { internalList[position].weight }
    }

    /**
     * Sets the weight of an item at the position.
     *
     * @param position The position of the item.
     * @param weight The weight to set on the item
     * @throws IndexOutOfBoundsException if the position does not exist in this list.
     */
    fun setItemWeight(position: Int, weight: Int) {
        require(weight > 0) { "Weight should be 1 or greater" }
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

package org.cascadebot.cascadebot.utils

import java.util.Random
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class WeightedList<T : Any> {

    companion object {
        val random: Random = Random()
    }

    private val internalList: MutableList<Pair<T, Int>> = mutableListOf()
    private val lock = ReentrantReadWriteLock()
    val totalWeight
        get() = lock.read { internalList.sumBy { it.second } }

    @JvmOverloads
    fun add(item: T, weight: Int = 1) {
        val existingItem = internalList.find { it.first == item }
        if (existingItem != null) {
            remove(item)
            val newWeight = existingItem.second + weight
            // Create a new pair with the existing weight + the new weight
            internalList.add(Pair(item, newWeight))
        } else {
            internalList.add(Pair(item, weight))
        }
    }

    fun remove(item: T) {
        lock.write {
            internalList.removeIf { it.first == item }
        }
    }

    fun remove(position: Int) {
        lock.write {
            internalList.removeAt(position)
        }
    }

    fun getItemWeight(position: Int): Int {
        return lock.read { internalList[position].second }
    }

    fun setItemWeight(position: Int, weight: Int) {
        lock.write { internalList[position] = internalList[position].copy(second = weight) }
    }

    operator fun get(position: Int): Pair<T, Int> {
        return lock.read { internalList[position] }
    }

    operator fun set(position: Int, value: T) {
        lock.write { internalList[position] = internalList[position].copy(first = value) }
    }

    fun get(obj: T): Pair<T, Int>? {
        return lock.read { internalList.find { it.first == obj }}
    }

    fun indexOf(obj: T): Int {
        lock.read {
            internalList.forEachIndexed { index, pair -> if (pair.first == obj) return index }
        }
        return -1
    }

    val items: List<T>
        get() = lock.read { internalList.map { it.first }.toList() }

    val itemsAndWeighting: List<Pair<T, Int>>
        get() = lock.read { internalList.toList() }

    val size: Int
        get() = lock.read { internalList.size}

    // Gets a number between 1 and totalWeight (Inclusive)
    val randomItem: T?
        get() = lock.read {
            if (totalWeight == 0) return@read null

            // Gets a number between 1 and totalWeight (Inclusive)
            var selection: Int = random.nextInt(totalWeight) + 1
            for (weightedItem: Pair<T, Int> in internalList) {
                selection -= weightedItem.second
                if (selection <= 0) {
                    return weightedItem.first
                }
            }
            null
        }

    fun getItemProportion(position: Int): Double {
        return lock.read {
            getItemWeight(position).toDouble() / totalWeight.toDouble()
        }
    }

}
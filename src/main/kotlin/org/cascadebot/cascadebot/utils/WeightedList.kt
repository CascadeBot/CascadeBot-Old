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
    private var _totalWeight = 0;
    var totalWeight
        set(newWeight) = lock.write { _totalWeight = newWeight }
        get() = lock.read { _totalWeight }

    @JvmOverloads
    fun add(item: T, weight: Int = 1) {
        totalWeight += lock.write {
            val existingItem = internalList.find { it.first == item }
            return@write if (existingItem != null) {
                remove(item)
                val newWeight = existingItem.second + weight
                // Create a new pair with the existing weight + the new weight
                internalList.add(Pair(item, newWeight))
                newWeight
            } else {
                internalList.add(Pair(item, weight))
                weight
            }
        }
    }

    fun remove(item: T) {
        lock.write {
            val iterator = internalList.iterator()
            while (iterator.hasNext()) {
                val nextPair = iterator.next()
                if (nextPair.first == item) {
                    totalWeight -= nextPair.second
                    iterator.remove()
                }
            }
        }
    }

    fun remove(position: Int) {
        lock.write {
            val pair = internalList.removeAt(position)
            totalWeight -= pair.second
        }
    }

    fun getItemWeight(position: Int): Int {
        return lock.read { internalList[position].second }
    }

    operator fun get(position: Int): T {
        return lock.read { internalList[position].first }
    }

    val items: List<T>
        get() = lock.read { internalList.map { it.first }.toList() }

    val itemsAndWeighting: List<Pair<T, Int>>
        get() = lock.read { internalList.toList() }

    // Gets a number between 1 and totalWeight (Inclusive)
    val randomItem: T?
        get() = lock.read {
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

    fun size(): Int {
        return lock.read { internalList.size }
    }
}
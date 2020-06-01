package org.cascadebot.cascadebot.utils

import org.cascadebot.cascadebot.utils.lists.WeightedList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.math.abs

class WeightedListTest {

    @Test
    fun `Test adding same items`() {
        val list = WeightedList<String>()
        list.add("hi", 2)
        list.add("hi", 3)
        assertEquals(1, list.items.size)
        assertEquals(5, list.getItemWeight(0))
    }

    @Test
    fun `Test item proportion - 2 items`() {

        val proportions = mapOf(
                Pair(Pair(2, 2), Pair(0.5, 0.5)),
                Pair(Pair(2, 3), Pair(2.0 / 5.0, 3.0 / 5.0)),
                Pair(Pair(2, 6), Pair(0.25, 0.75)),
                Pair(Pair(2, 8), Pair(0.2, 0.8)),
                Pair(Pair(4, 6), Pair(0.4, 0.6))
        )

        for ((items, outputs) in proportions) {
            val list = WeightedList<String>()
            list.add("item1", items.first)
            list.add("item2", items.second)
            assertEquals(2, list.items.size)
            assertEquals(outputs.first, list.getItemProportion(0))
            assertEquals(outputs.second, list.getItemProportion(1))
        }

    }

    @Test
    fun `Test item proportion - 3 items`() {

        val proportions = mapOf(
                Pair(Triple(2, 2, 2), Triple(1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0)),
                Pair(Triple(2, 3, 3), Triple(0.25, 3.0 / 8.0, 3.0 / 8.0)),
                Pair(Triple(3, 4, 3), Triple(0.3, 0.4, 0.3)),
                Pair(Triple(1, 1, 1), Triple(1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0)),
                Pair(Triple(4, 4, 8), Triple(0.25, 0.25, 0.5))
        )

        for ((items, outputs) in proportions) {
            val list = WeightedList<String>()
            list.add("item1", items.first)
            list.add("item2", items.second)
            list.add("item3", items.third)
            assertEquals(3, list.items.size)
            assertEquals(outputs.first, list.getItemProportion(0))
            assertEquals(outputs.second, list.getItemProportion(1))
            assertEquals(outputs.third, list.getItemProportion(2))
        }

    }

    @DisplayName("Test random selection")
    @ParameterizedTest(name = "Repeat count of {0} with threshold of {1}")
    @CsvSource(
        "100,0.05",
        "1000,0.025",
        "10000,0.01",
        "100000,0.005"
    )
    fun `Test random selection`(repeatCount: Int, threshold: Double) {
        val list = WeightedList<String>()
        list.add("10%", 1)
        list.add("90%", 9)

        val outputItems = mutableListOf<String>()

        repeat(repeatCount) {
            list.randomItem?.let { randomItem -> outputItems.add(randomItem) }
        }

        val ten = outputItems.count { it == "10%" }
        val ninety = outputItems.count { it == "90%" }

        val tenPercentage = ten.toDouble() / repeatCount.toDouble()
        val ninetyPercentage = ninety.toDouble() / repeatCount.toDouble()

        assert(abs(0.1 - tenPercentage) <= threshold)
        assert(abs(0.9 - ninetyPercentage) <= threshold)
    }


}
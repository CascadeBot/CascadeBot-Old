/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import groovy.util.GroovyTestCase.assertEquals
import org.cascadebot.cascadebot.utils.lists.CollectionDiff
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionDiffTest {

    @Test
    fun `Test added item`() {
        val original = listOf(1, 2, 3, 4)
        val new = listOf(1, 2, 3, 4, 5)

        val collectionDiff = CollectionDiff(original, new)

        assertEquals(1, collectionDiff.added.size)
        assertTrue(collectionDiff.added[0] == 5)

        assertEquals(0, collectionDiff.removed.size)
    }

    @Test
    fun `Test removed item`() {
        val original = listOf(1, 2, 3, 4, 5)
        val new = listOf(1, 2, 3, 4)

        val collectionDiff = CollectionDiff(original, new)

        assertEquals(1, collectionDiff.removed.size)
        assertTrue(collectionDiff.removed[0] == 5)

        assertEquals(0, collectionDiff.added.size)
    }

    @Test
    fun `Test add item duplicate`() {
        val original = listOf(1, 2, 3, 4)
        val new = listOf(1, 2, 3, 4, 1)

        val collectionDiff = CollectionDiff(original, new)

        assertEquals(1, collectionDiff.added.size)
        assertTrue(collectionDiff.added[0] == 1)

        assertEquals(0, collectionDiff.removed.size)
    }

    @Test
    fun `Test remove item duplicate`() {
        val original = listOf(1, 2, 1, 3, 4)
        val new = listOf(1, 2, 3, 4)

        val collectionDiff = CollectionDiff(original, new)

        assertEquals(1, collectionDiff.removed.size)
        assertTrue(collectionDiff.removed[0] == 1)

        assertEquals(0, collectionDiff.added.size)
    }

    @Test
    fun `Test add and remove`() {
        val original = listOf(1, 2, 3, 4)
        val new = listOf(1, 2, 5, 3)


        val collectionDiff = CollectionDiff(original, new)

        assertEquals(1, collectionDiff.removed.size)
        assertTrue(collectionDiff.removed[0] == 4)

        assertEquals(1, collectionDiff.added.size)
        assertTrue(collectionDiff.added[0] == 5)
    }

    @Test
    fun `Test add and remove duplicated`() {
        val original = listOf(1, 2, 4, 5, 3, 4)
        val new = listOf(1, 2, 5, 4, 3, 5)


        val collectionDiff = CollectionDiff(original, new)

        assertEquals(1, collectionDiff.removed.size)
        assertTrue(collectionDiff.removed[0] == 4)

        assertEquals(1, collectionDiff.added.size)
        assertTrue(collectionDiff.added[0] == 5)
    }

}
package org.cascadebot.cascadebot.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class ChangeListTest {

    @Test
    fun `remove() removes from added list`() {
        val changeList = ChangeList<String>(mutableSetOf())
        changeList.add("element")
        changeList.remove("element")

        assertTrue(changeList.addedItems.isEmpty())
        assertTrue(changeList.removedItems.isEmpty())
    }

    @Test
    fun `add() adds to removed list`() {
        val changeList = ChangeList<String>(mutableSetOf())
        changeList.remove("element")
        changeList.add("element")

        assertTrue(changeList.addedItems.isEmpty())
        assertTrue(changeList.removedItems.isEmpty())
    }

    @Test
    fun `Remove from base collection`() {
        val changeList = ChangeList(mutableSetOf("element", "different"))
        changeList.remove("element")
        assertTrue(changeList.removedItems.contains("element"))
        assertEquals(changeList.applyChanges(), setOf("different"))
    }

    @Test
    fun `Add to base collection`() {
        val changeList = ChangeList(mutableSetOf("different"))
        changeList.add("element")
        assertTrue(changeList.addedItems.contains("element"))
        assertEquals(changeList.applyChanges(), setOf("element", "different"))
    }


}
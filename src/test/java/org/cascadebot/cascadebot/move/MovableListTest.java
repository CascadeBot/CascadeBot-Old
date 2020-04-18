/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.move;

import org.cascadebot.cascadebot.utils.move.MovableList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MovableListTest {

    @Test
    public void checkMoveUpOne() {
        TestListsObject testLists = createTestLists(4, 2, 1);
        List<TestMovableItem> movingList = testLists.getOriginalList();
        List<TestMovableItem> resultList = testLists.getMovedList();
        MovableList<TestMovableItem> movableList = new MovableList<>(movingList);
        movableList.moveSelection(2);
        movableList.startMovingItem();
        movableList.moveSelection(-1);
        List<TestMovableItem> movedList = movableList.confirmMove();
        boolean worked = true;
        int i = 0;
        for (TestMovableItem movedItem : movedList) {
            TestMovableItem resultItem = resultList.get(i);
            if (!movedItem.getName().equals(resultItem.getName())) {
                worked = false;
            }
            i++;
        }
        assertTrue(worked);
    }

    @Test
    public void checkMoveDownOne() {
        TestListsObject testLists = createTestLists(4, 1, 2);
        List<TestMovableItem> movingList = testLists.getOriginalList();
        List<TestMovableItem> resultList = testLists.getMovedList();
        MovableList<TestMovableItem> movableList = new MovableList<>(movingList);
        movableList.moveSelection(1);
        movableList.startMovingItem();
        movableList.moveSelection(1);
        List<TestMovableItem> movedList = movableList.confirmMove();
        boolean worked = true;
        int i = 0;
        for (TestMovableItem movedItem : movedList) {
            TestMovableItem resultItem = resultList.get(i);
            if (!movedItem.getName().equals(resultItem.getName())) {
                worked = false;
            }
            i++;
        }
        assertTrue(worked);
    }

    @Test
    public void checkMoveUpMultiple() {
        TestListsObject testLists = createTestLists(6, 4, 2);
        List<TestMovableItem> movingList = testLists.getOriginalList();
        List<TestMovableItem> resultList = testLists.getMovedList();
    }

    @Test
    public void checkMoveDownMultiple() {
        List<TestMovableItem> movingList = new ArrayList<>();
        List<TestMovableItem> resultList = new ArrayList<>();
    }

    @Test
    public void checkMoveUpOneWithUpdate() {
        List<TestMovableItem> movingList = new ArrayList<>();
        List<TestMovableItem> resultList = new ArrayList<>();
    }

    @Test
    public void checkMoveDownOneWithUpdate() {
        List<TestMovableItem> movingList = new ArrayList<>();
        List<TestMovableItem> resultList = new ArrayList<>();
    }

    @Test
    public void checkMoveUpMultipleWithUpdate() {
        List<TestMovableItem> movingList = new ArrayList<>();
        List<TestMovableItem> resultList = new ArrayList<>();
    }

    @Test
    public void checkMoveDownMultipleWithUpdate() {
        List<TestMovableItem> movingList = new ArrayList<>();
        List<TestMovableItem> resultList = new ArrayList<>();
    }

    /**
     * Creates test lists for testing movable lists
     *
     * @param size The size of the list to create
     * @param movedPos The position of the item being moved
     * @param newPos The position to move the item to
     * @return A tests list objects which contains the original list, and the list as it should be after being moved. {@link TestListsObject}
     *
     * @see TestListsObject
     */
    public TestListsObject createTestLists(int size, int movedPos, int newPos) {
        List<TestMovableItem> originalList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            originalList.add(new TestMovableItem(String.valueOf(i)));
        }
        List<TestMovableItem> movedList = new ArrayList<>(originalList);
        TestMovableItem tempItem = movedList.get(movedPos); //TODO properly move item
        movedList.set(movedPos, movedList.get(newPos));
        movedList.set(newPos, tempItem);
        return new TestListsObject(originalList, movedList);
    }

}

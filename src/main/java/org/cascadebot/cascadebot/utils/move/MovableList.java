/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.move;

import lombok.Getter;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class will handle the code for any list that will have movable items.
public class MovableList<T extends MovableItem> {

    List<T> list;
    int selected = 0;
    int itemStart = -1;
    @Getter
    boolean moving;

    public MovableList() {
        list = new ArrayList<>();
    }

    public MovableList(List<T> startingList) {
        list = startingList;
    }

    public void notifyListChange(@NotNull List<T> newList) {
        Map<Integer, Integer> movedAmountMap = new HashMap<>();
        int newPos = 0;
        for (T item: newList) {
            // Loop through all items and find the difference between item positions
            int oldPos = list.indexOf(item);
            if (oldPos != -1) {
                // existing item
                movedAmountMap.put(oldPos, newPos - oldPos);
            } // we ignore new items as the user can never have a new item selected.
            newPos++;
        }
        if (selected != -1) {
            if (movedAmountMap.containsKey(selected)) {
                int newSelected = selected + movedAmountMap.get(selected);
                selected = fitInArray(newSelected);
            } else {
                // Selected item was removed
                selected = 0;
            }
        }
        if (itemStart != -1) {
            if (movedAmountMap.containsKey(itemStart)) {
                int newItem = itemStart + movedAmountMap.get(itemStart);
                itemStart = fitInArray(newItem);
            } else {
                // Item being moved was removed
                itemStart = -1;
                cancelMovingItem();
            }
        }
        list = newList;
    }

    public void moveSelection(int amount) {
        if (moving) {
            moveItem(selected, selected + amount);
        }
        int newSelect = selected + amount;
        selected = fitInArray(newSelect);
    }

    public void startMovingItem() throws UnsupportedOperationException {
        if (moving) {
            throw new UnsupportedOperationException("Cannot start moving if you are already moving");
        }
        moving = true;
    }

    public void cancelMovingItem() throws UnsupportedOperationException {
        if (!moving) {
            throw new UnsupportedOperationException("Cannot cancel moving if you are not moving");
        }
        moving = false;
        if (itemStart == -1) {
            // Cannot revert if item being moved no longer exists.
            return;
        }
        // Revert list
        moveItem(selected, itemStart);
        itemStart = -1;
    }

    public List<T> confirmMove() throws UnsupportedOperationException {
        if (!moving) {
            throw new UnsupportedOperationException("Cannot confirm moving if you are not moving");
        }
        moving = false;
        itemStart = -1;
        return list;
    }

    private void moveItem(int start, int end) {
        T itemToMove = list.get(start);
        if (end >= list.size()) {
            // Moved to end of array
            list.remove(start);
            list.add(itemToMove);
        }

        list.add(start, list.get(end));
        list.add(end, itemToMove);
    }

    private int fitInArray(int i) {
        if (i < 0) {
            return 0;
        }
        if (i >= list.size()) {
            return list.size() - 1;
        }
        return i;
    }

    public String getFrontendText() {
        int currentPage = selected / 10 + 1;
        int start = currentPage * 10 - 10;
        int end = start + 9;

        StringBuilder pageBuilder = new StringBuilder();

        for (int i = start; i <= end; i++) {
            if (i >= list.size()) {
                break;
            }
            T item = list.get(i);
            if (i == selected) {
                pageBuilder.append(UnicodeConstants.SMALL_ORANGE_DIAMOND).append(" ");
            } else {
                pageBuilder.append(UnicodeConstants.WHITE_SMALL_SQUARE).append(" ");
            }
            pageBuilder.append(i + 1).append(": ");
            if (i == selected && moving) {
                pageBuilder.append("**");
            }
            pageBuilder.append(item.getItemText()).append('\n');
            if (i == selected && moving) {
                pageBuilder.append("**");
            }
        }
        return pageBuilder.toString();
    }

}

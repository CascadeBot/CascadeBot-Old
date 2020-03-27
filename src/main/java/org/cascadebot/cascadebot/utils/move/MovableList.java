/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.move;

import com.google.common.primitives.Ints;
import lombok.Getter;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class will handle the code for any list that will have movable items.
public class MovableList<T extends MovableItem> {

    private List<T> list;
    private int selectedPos = 0;
    private int itemStart = -1;
    @Getter
    private boolean moving;

    public MovableList() {
        list = new ArrayList<>();
    }

    public MovableList(List<T> startingList) {
        list = startingList;
    }

    public void notifyListChange(@NotNull List<T> newList) {
        Map<Integer, Integer> movedAmountMap = new HashMap<>();
        int newPos = 0;
        for (T item : newList) {
            // Loop through all items and find the difference between item positions
            int oldPos = list.indexOf(item);
            if (oldPos != -1) {
                // existing item
                movedAmountMap.put(oldPos, newPos - oldPos);
            } // Ignore new items
            newPos++;
        }
        if (selectedPos != -1) {
            if (movedAmountMap.containsKey(selectedPos)) {
                int moveAmount = movedAmountMap.get(selectedPos);
                int newSelected = selectedPos + moveAmount;
                if (moving) {
                    newSelected -= itemStart - selectedPos;
                }
                selectedPos = Ints.constrainToRange(newSelected, 0, list.size() - 1);
            } else {
                // Selected item was removed
                selectedPos = 0;
            }
        }
        if (itemStart != -1) {
            if (movedAmountMap.containsKey(itemStart)) {
                int newItem = itemStart + movedAmountMap.get(itemStart);
                if (moving) {
                    newItem -= selectedPos - itemStart;
                }
                itemStart = Ints.constrainToRange(newItem, 0, list.size() - 1);
            } else {
                // Item being moved was removed
                itemStart = -1;
                cancelMovingItem();
            }
        }
        list = newList;
        if (moving) {
            moveItem(itemStart, selectedPos);
        }
    }

    public void moveSelection(int amount) {
        if (moving) {
            moveItem(selectedPos, selectedPos + amount);
        }
        int newSelect = selectedPos + amount;
        selectedPos = Ints.constrainToRange(newSelect, 0, list.size() - 1);
    }

    public void startMovingItem() throws UnsupportedOperationException {
        if (moving) {
            throw new UnsupportedOperationException("Cannot start moving if you are already moving");
        }
        moving = true;
        itemStart = selectedPos;
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
        moveItem(selectedPos, itemStart);
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

    private void moveItem(int source, int destination) {
        T itemToMove = list.get(source);

        if (source > destination) {
            for (int i = source; i > destination; i--) {
                list.set(i, list.get(i - 1));
            }
            list.set(destination, itemToMove);
        } else {
            for (int i = source; i  < destination; i++) {
                list.set(i, list.get(i + 1));
            }
            list.set(destination, itemToMove);
        }
    }

    public String getFrontendText() {
        int lowestItem = selectedPos - 5;
        int highestItem = selectedPos + 4;
        int lowestItemDisplay = lowestItem;
        int highestItemDisplay = highestItem;

        if (lowestItem < 0) {
            highestItemDisplay += Math.abs(lowestItem);
            lowestItemDisplay = 0;
        }

        if (highestItem >= list.size() - 1) {
            lowestItemDisplay -= highestItem - (list.size() - 2);
            highestItemDisplay = list.size() - 2;
        }

        StringBuilder pageBuilder = new StringBuilder();

        for (int i = lowestItemDisplay; i <= highestItemDisplay + 1; i++) {
            if (i >= list.size()) {
                break;
            }
            T item = list.get(i);
            if (i == selectedPos) {
                pageBuilder.append(UnicodeConstants.SMALL_ORANGE_DIAMOND).append(" ");
            } else {
                pageBuilder.append(UnicodeConstants.WHITE_SMALL_SQUARE).append(" ");
            }
            pageBuilder.append(i + 1).append(": ");
            if (i == selectedPos && moving) {
                pageBuilder.append("**");
            }
            pageBuilder.append(item.getItemText()).append('\n');
            if (i == selectedPos && moving) {
                pageBuilder.append("**");
            }
        }
        return pageBuilder.toString();
    }

}

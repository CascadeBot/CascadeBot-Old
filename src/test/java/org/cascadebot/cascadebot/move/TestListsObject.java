/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.move;

import java.util.List;

public class TestListsObject {

    private List<TestMovableItem> originalList;
    private List<TestMovableItem> movedList;

    public TestListsObject(List<TestMovableItem> originalList, List<TestMovableItem> movedList) {
        this.originalList = originalList;
        this.movedList = movedList;
    }

    public List<TestMovableItem> getOriginalList() {
        return originalList;
    }

    public List<TestMovableItem> getMovedList() {
        return movedList;
    }

}

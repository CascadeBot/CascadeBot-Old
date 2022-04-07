/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.move;

import lombok.Getter;
import org.cascadebot.cascadebot.utils.move.legacy.MovableItem;

public class TestMovableItem implements MovableItem {

    @Getter
    private String name;

    TestMovableItem(String name) {
        this.name = name;
    }

    @Override
    public String getItemText() {
        return name;
    }

}

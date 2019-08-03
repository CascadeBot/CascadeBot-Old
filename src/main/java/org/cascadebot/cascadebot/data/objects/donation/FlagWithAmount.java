/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.donation;

import lombok.ToString;

public class FlagWithAmount extends Flag {
    private int amount;

    private FlagWithAmount() {
        super();
    }

    public FlagWithAmount(String id) {
        super(id);
    }

    public FlagWithAmount(String id, int amount) {
        super(id);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return getId() + ": " + amount;
    }
}

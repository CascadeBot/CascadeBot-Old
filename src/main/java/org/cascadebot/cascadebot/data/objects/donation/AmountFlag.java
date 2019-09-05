/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.donation;

import com.google.gson.JsonObject;

public class AmountFlag extends Flag implements DataFlag {
    private int amount;

    private AmountFlag() {
        super();
    }

    public AmountFlag(String id) {
        super(id);
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

    @Override
    public DataFlag parseFlagData(JsonObject flagDataObject) {
        amount = flagDataObject.get("amount").getAsInt();
        return this;
    }
}

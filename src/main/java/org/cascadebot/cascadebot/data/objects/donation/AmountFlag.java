/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.donation;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

public class AmountFlag extends DataFlag {

    @Getter
    @Setter
    private int amount;

    private AmountFlag() {
        super();
    }

    public AmountFlag(String id) {
        super(id);
    }

    @Override
    public DataFlag parseFlagData(JsonObject flagDataObject) {
        amount = flagDataObject.get("amount").getAsInt();
        return this;
    }

    @Override
    public String toString() {
        return "AmountFlag(id=" + getId() + ", amount=" + amount + ")";
    }

}

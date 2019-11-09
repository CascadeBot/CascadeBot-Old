package org.cascadebot.cascadebot.data.objects.donation;

import com.google.gson.JsonObject;

//TODO move stuff over to interface
public abstract class DataFlag extends Flag {

    public DataFlag(String id) {
        super(id);
    }

    protected DataFlag() {
        super(null);
    }

    abstract DataFlag parseFlagData(JsonObject flagDataObject);

    abstract public String toString();


}

package org.cascadebot.cascadebot.data.objects.donation;

import com.google.gson.JsonObject;

public class TimeFlag extends DataFlag {

    private TimeFlag() {
        super();
    }

    public TimeFlag(String id) {
        super(id);
    }

    long mills;

    @Override
    DataFlag parseFlagData(JsonObject flagDataObject) {
        mills = flagDataObject.get("time").getAsLong();
        return this;
    }

    @Override
    public String toString() {
        return "TimeFlag(id=" + getId() + ", time=" + mills + ")";
    }
}

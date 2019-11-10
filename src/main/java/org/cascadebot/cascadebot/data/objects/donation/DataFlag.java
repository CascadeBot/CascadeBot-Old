package org.cascadebot.cascadebot.data.objects.donation;

import com.google.gson.JsonObject;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

public abstract class DataFlag extends Flag {

    public DataFlag(String id) {
        super(id);
    }

    protected DataFlag() {
        super(null);
    }

    abstract DataFlag parseFlagData(JsonObject flagDataObject);

    abstract public String toString();

    abstract public String getDescription(Locale locale);

}

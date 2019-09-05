package org.cascadebot.cascadebot.data.objects.donation;

import com.google.gson.JsonObject;

import java.io.InvalidClassException;

//TODO move stuff over to interface
public interface DataFlag extends IFlag {

    DataFlag parseFlagData(JsonObject flagDataObject);

}

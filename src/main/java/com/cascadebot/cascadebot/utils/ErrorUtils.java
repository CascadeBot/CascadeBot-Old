/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.data.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorUtils {

    public static String paste(String paste) {
        Request request = new Request.Builder()
                .url(Config.INS.getHasteServer())
                .post(RequestBody.create(MediaType.parse("application/text"), paste))
                .build();

        try {
            Response response = CascadeBot.INS.getHttpClient().newCall(request).execute();
            JsonParser parser = new JsonParser();
            if (response.body() != null) {
                JsonObject object = parser.parse(response.body().string()).getAsJsonObject();
                return Config.INS.getHasteLink() + object.get("key").getAsString();
            }
        } catch (IOException e) {
            CascadeBot.logger.error(MarkerFactory.getMarker("HASTEBIN"), "Error while trying to post!", e);
        }
        return "";
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter writer = new PrintWriter(sw)) {
            throwable.printStackTrace(writer);
            return sw.toString();
        }
    }

}

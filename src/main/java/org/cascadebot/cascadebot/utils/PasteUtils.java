/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@UtilityClass
public class PasteUtils {

    public static String paste(String paste) {
        Request request = new Request.Builder()
                .url(Config.INS.getHasteServer())
                .post(RequestBody.create(MediaType.parse("application/text"), paste))
                .build();

        try {
            Response response = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build().newCall(request).execute();
            JsonParser parser = new JsonParser();
            if (response.body() != null) {
                JsonObject object = parser.parse(response.body().string()).getAsJsonObject();
                return Config.INS.getHasteLink() + object.get("key").getAsString();
            }
        } catch (IOException e) {
            CascadeBot.LOGGER.error(MarkerFactory.getMarker("HASTEBIN"), "Error while trying to post!", e);
            return "Could not post to hastebin :(";
        }
        return "";
    }

    public static void pasteIfLong(String message, int maxLength, Consumer<String> action) {
        if (message.length() > maxLength) {
            action.accept(paste(message));
        } else {
            action.accept(message);
        }
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter writer = new PrintWriter(sw)) {
            throwable.printStackTrace(writer);
            return sw.toString();
        }
    }

}

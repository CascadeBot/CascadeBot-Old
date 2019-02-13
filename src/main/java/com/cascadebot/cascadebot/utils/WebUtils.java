/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.CascadeBot;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class WebUtils {

    private static JsonParser parser = new JsonParser();

    public static JsonElement getJsonFromURL(String url) throws IOException, IllegalArgumentException {
        OkHttpClient client = CascadeBot.INS.getHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        if(response.body() == null) {
            throw new IllegalArgumentException("Response returned no content");
        }
        try {
            return parser.parse(response.body().string());
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Response didn't return json!");
        }
    }
}

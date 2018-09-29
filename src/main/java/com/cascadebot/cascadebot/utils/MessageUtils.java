package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageUtils {

    public static String paste(String paste) {
        Request request = new Request.Builder()
                .url(Config.VALUES.hasteServer)
                .post(RequestBody.create(MediaType.parse("application/text"), paste))
                .build();

        try {
            Response response = CascadeBot.instance().getHttpClient().newCall(request).execute();
            JsonParser parser = new JsonParser();
            if(response.body() != null) {
                JsonObject object = parser.parse(response.body().string()).getAsJsonObject();
                return object.get("key").getAsString();
            }
        } catch (IOException e) {
            e.printStackTrace(); //TODO log this separately so things don't infinite loop.
        }
        return "";
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String trace = sw.toString();
        pw.close();
        return trace;
    }

}

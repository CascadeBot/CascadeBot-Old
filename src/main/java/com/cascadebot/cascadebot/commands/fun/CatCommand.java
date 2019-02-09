/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.fun;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.permissions.Permission;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;


public class CatCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.thecatapi.com/v1/images/search").newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String catUrl = jsonObject.getString("url");
                if (context.getData().getUseEmbedForMessages()) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setImage(catUrl);
                    context.reply(embedBuilder.build());
                } else {
                    context.reply(catUrl);
                }
            }
        } catch (IOException e) {
            context.replyDanger("Our goblins scared away all of the cats!");
        }
    }

    @Override
    public String command() {
        return "cat";
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Permission getPermission() {
        return null; // Cannot be restricted
    }

}

/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.fun;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;


public class DogCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.thedogapi.com/v1/images/search").newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String dogUrl = jsonObject.getString("url");
                if (context.getData().getUseEmbedForMessages()) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setImage(dogUrl);
                    context.reply(embedBuilder.build());
                } else {
                    context.reply(dogUrl);
                }
            }
        } catch (IOException e) {
            context.replyDanger("Our goblins scared away all of the dogs!");
        }
    }

    @Override
    public String command() {
        return "dog";
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
        return Permission.of("Dog command", "dog");
    }

}

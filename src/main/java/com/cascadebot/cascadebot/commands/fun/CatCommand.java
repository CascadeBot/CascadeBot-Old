/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.fun;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.permissions.Permission;
import com.cascadebot.cascadebot.utils.WebUtils;
import com.cascadebot.cascadebot.utils.buttons.Button;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class CatCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
            ButtonGroup catButtons = new ButtonGroup(context.getUser().getIdLong(), context.getGuild().getIdLong());
            catButtons.addButton(new Button.UnicodeButton("\uD83D\uDD01" /* Repeat 🔁 */, (member, channel, message) -> {
                if(member.getUser().getIdLong() != catButtons.getOwner().getUser().getIdLong()) {
                    return;
                }
                try {
                    if (message.getEmbeds().size() > 0) {
                        EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
                        embedBuilder.setImage(getCatUrl());
                        message.editMessage(embedBuilder.build()).queue();
                    } else {
                        message.editMessage(getCatUrl()).queue();
                    }
                } catch (IOException e) {
                    message.editMessage("Error loading cat picture \uD83D\uDE26" /* Frowning 😦*/).queue();
                }
            }));
            try {
                context.replyImage(getCatUrl()).queue(message -> {
                    catButtons.addButtonsToMessage(message);
                    catButtons.setMessage(message.getIdLong());
                    context.getData().addButtonGroup(context.getChannel(), message, catButtons);
                });
            } catch (IOException e) {
                context.replyDanger("Error loading cat picture \uD83D\uDE26" /* Frowning 😦*/);
            }
    }

    private String getCatUrl() throws IOException {
        JsonArray jsonArray = WebUtils.getJsonFromURL("https://api.thecatapi.com/v1/images/search").getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        return jsonObject.get("url").getAsString();
    }

    @Override
    public String command() {
        return "cat";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Permission getPermission() {
        return Permission.of("Cat command", "cat", true);
    }

}

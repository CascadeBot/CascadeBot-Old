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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.entities.Member;

import java.io.IOException;


public class DogCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        JsonArray jsonArray = null;
        try {
            jsonArray = WebUtils.getJsonFromURL("https://api.thedogapi.com/v1/images/search").getAsJsonArray();
        } catch (IOException e) {
            context.replyDanger("Error loading dog picture \uD83D\uDE26");
        }
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        String dogUrl = jsonObject.get("url").getAsString();
        context.replyImage(dogUrl);
    }

    @Override
    public String command() {
        return "dog";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Permission getPermission() {
        return Permission.of("Dog command", "dog", true);
    }

}

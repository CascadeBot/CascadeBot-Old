/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.WebUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.io.IOException;


public class DogCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        ButtonGroup dogButtons = new ButtonGroup(context.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
        dogButtons.addButton(new Button.UnicodeButton("\uD83D\uDD01" /* Repeat 🔁 */, (member, channel, message) -> {
            if(member.getUser().getIdLong() != dogButtons.getOwner().getUser().getIdLong()) {
                return;
            }
            try {
                if (message.getEmbeds().size() > 0) {
                    EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
                    embedBuilder.setImage(getDogUrl());
                    message.editMessage(embedBuilder.build()).queue();
                } else {
                    context.getUIMessaging().replyImage(getDogUrl()).thenAccept(dogMessage -> {
                        dogButtons.addButtonsToMessage(dogMessage);
                        dogButtons.setMessage(dogMessage.getIdLong());
                        context.getData().addButtonGroup(context.getChannel(), dogMessage, dogButtons);
                    });
                    message.delete().queue();
                }
            } catch (IOException e) {
                message.editMessage("Error loading dog picture \uD83D\uDE26" /* Frowning 😦*/).queue();
            }
        }));
        try {
            context.getUIMessaging().replyImage(getDogUrl()).thenAccept(message -> {
                dogButtons.addButtonsToMessage(message);
                dogButtons.setMessage(message.getIdLong());
                context.getData().addButtonGroup(context.getChannel(), message, dogButtons);
            });
        } catch (IOException e) {
            context.getTypedMessaging().replyDanger("Error loading dog picture \uD83D\uDE26" /* Frowning 😦*/);
        }
    }

    private String getDogUrl() throws IOException {
        JsonArray jsonArray = WebUtils.getJsonFromURL("https://api.thedogapi.com/v1/images/search").getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        return jsonObject.get("url").getAsString();
    }

    @Override
    public String command() {
        return "dog";
    }

    @Override
    public Module getModule() {
        return Module.FUN;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Dog command", "dog", true);
    }

    @Override
    public String description() {
        return "Returns a random picture of a dog";
    }

}

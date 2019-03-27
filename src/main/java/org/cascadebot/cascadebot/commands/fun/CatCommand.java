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
                        context.getUIMessaging().replyImage(getCatUrl()).thenAccept(catMessage -> {
                            catButtons.addButtonsToMessage(catMessage);
                            catButtons.setMessage(catMessage.getIdLong());
                            context.getData().addButtonGroup(context.getChannel(), catMessage, catButtons);
                        });
                        message.delete().queue();
                    }
                } catch (IOException e) {
                    message.editMessage("Error loading cat picture \uD83D\uDE26" /* Frowning 😦*/).queue();
                }
            }));
            try {
                context.getUIMessaging().replyImage(getCatUrl()).thenAccept(message -> {
                    catButtons.addButtonsToMessage(message);
                    catButtons.setMessage(message.getIdLong());
                    context.getData().addButtonGroup(context.getChannel(), message, catButtons);
                });
            } catch (IOException e) {
                context.getTypedMessaging().replyDanger("Error loading cat picture \uD83D\uDE26" /* Frowning 😦*/);
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
    public Module getModule() {
        return Module.FUN;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Cat command", "cat", true);
    }

    @Override
    public String description() {
        return "Shows a random picture of a cat";
    }

}

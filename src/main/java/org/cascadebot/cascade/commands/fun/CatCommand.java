/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.UnicodeConstants;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.messaging.MessagingObjects;
import org.cascadebot.cascade.permissions.CascadePermission;
import org.cascadebot.cascade.utils.WebUtils;
import org.cascadebot.cascade.utils.buttons.Button;
import org.cascadebot.cascade.utils.buttons.ButtonGroup;

import java.io.IOException;

public class CatCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        ButtonGroup catButtons = new ButtonGroup(context.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
        catButtons.addButton(new Button.UnicodeButton(UnicodeConstants.REPEAT, (member, channel, message) -> {
            if (member.getUser().getIdLong() != catButtons.getOwner().getUser().getIdLong()) {
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
                message.editMessage("Error loading cat picture " + UnicodeConstants.FROWNING).queue();
            }
        }));
        try {
            context.getUIMessaging().replyImage(getCatUrl()).thenAccept(message -> {
                catButtons.addButtonsToMessage(message);
                catButtons.setMessage(message.getIdLong());
                context.getData().addButtonGroup(context.getChannel(), message, catButtons);
            });
        } catch (IOException e) {
            context.getTypedMessaging().replyDanger("Error loading cat picture " + UnicodeConstants.FROWNING);
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
        return "Returns a random picture of a cat";
    }

}

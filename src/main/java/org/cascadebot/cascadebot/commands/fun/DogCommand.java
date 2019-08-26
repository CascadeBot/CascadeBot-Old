/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.WebUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.io.IOException;


public class DogCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        ButtonGroup dogButtons = new ButtonGroup(context.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
        dogButtons.addButton(new Button.UnicodeButton(UnicodeConstants.REPEAT, (member, channel, message) -> {
            if (member.getIdLong() != dogButtons.getOwner().getIdLong()) {
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
                    message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                }
            } catch (IOException e) {
                message.editMessage(context.i18n("commands.dog.error_loading")).queue();
            }
        }));
        try {
            context.getUIMessaging().replyImage(getDogUrl()).thenAccept(message -> {
                dogButtons.addButtonsToMessage(message);
                dogButtons.setMessage(message.getIdLong());
                context.getData().addButtonGroup(context.getChannel(), message, dogButtons);
            });
        } catch (IOException e) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.dog.error_loading"));
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
        return CascadePermission.of("dog", true);
    }

}

/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.WebUtils;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.CascadeButton;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;

import java.io.IOException;
import java.util.stream.Collectors;

public class CatCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        ComponentContainer container = new ComponentContainer();
        CascadeActionRow actionRow = new CascadeActionRow();
        actionRow.addComponent(new CascadeButton(ButtonStyle.SUCCESS, Emoji.fromUnicode(UnicodeConstants.REPEAT), (runner, channel, message) -> {
            if (runner.getIdLong() != sender.getIdLong()) {
                return;
            }
            try {
                if (message.getMessage().getEmbeds().size() > 0) {
                    EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder(context.getUser(), context.getLocale());
                    embedBuilder.setImage(getCatUrl());
                    message.editMessage(embedBuilder.build()).queue();
                } else {
                    context.getUiMessaging().replyImage(getCatUrl()).thenAccept(catMessage -> {
                        catMessage.editMessageComponents().setActionRows(container.getComponents().stream().map(CascadeActionRow::toDiscordActionRow).collect(Collectors.toList())).queue();
                        context.getData().addComponents(channel, catMessage, container);
                    });
                    message.getMessage().delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                }
            } catch (IOException e) {
                message.editMessage(context.i18n("commands.cat.error_loading")).queue();
            }
        }));
        container.addRow(actionRow);
        try {
            context.getUiMessaging().replyImage(getCatUrl()).thenAccept(message -> {
                message.editMessageComponents().setActionRows(container.getComponents().stream().map(CascadeActionRow::toDiscordActionRow).collect(Collectors.toList())).queue();
                context.getData().addComponents(context.getChannel(), message, container);
            });
        } catch (IOException e) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.cat.error_loading"));
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
    public Module module() {
        return Module.FUN;
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("cat", true);
    }

}

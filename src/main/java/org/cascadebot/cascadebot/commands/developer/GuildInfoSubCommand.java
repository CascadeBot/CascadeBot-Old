/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuildInfoSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        GuildData dataForList = context.getData();
        Guild guildForList = context.getGuild();

        if (context.getArgs().length > 0) {
            guildForList = CascadeBot.INS.getShardManager().getGuildById(context.getArg(0));
            if (guildForList == null) {
                context.getTypedMessaging().replyDanger("We couldn't find that guild!");
                return;
            }
            dataForList = GuildDataManager.getGuildData(guildForList.getIdLong());
        }

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(guildForList.getName());
        builder.setThumbnail(guildForList.getIconUrl());


        GuildData finalDataForList = dataForList;
        String flags = Arrays.stream(Flag.values())
                             .map(flag -> FormatUtils.formatEnum(flag) + " - " +
                                     (finalDataForList.isFlagEnabled(flag) ? UnicodeConstants.TICK : UnicodeConstants.RED_CROSS))
                             .collect(Collectors.joining("\n"));

        String modules = Arrays.stream(Module.values())
                             .map(module -> FormatUtils.formatEnum(module) + " - " +
                                     (finalDataForList.getCoreSettings().isModuleEnabled(module) ? UnicodeConstants.TICK : UnicodeConstants.RED_CROSS))
                             .collect(Collectors.joining("\n"));


        builder.addField("Flags", flags, false);
        builder.addField("Modules", modules, false);
        builder.addField("Join Date", FormatUtils.formatDateTime(context.getSelfMember().getTimeJoined()), false);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "info";
    }

    @Override
    public String parent() {
        return "guild";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

    @Override
    public String description() {
        return "Returns dev information about a guild.";
    }

}

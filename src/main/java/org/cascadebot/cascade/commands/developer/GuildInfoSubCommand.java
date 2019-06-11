/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.developer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.Cascade;
import org.cascadebot.cascade.UnicodeConstants;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.data.managers.GuildDataManager;
import org.cascadebot.cascade.data.objects.Flag;
import org.cascadebot.cascade.data.objects.GuildData;
import org.cascadebot.cascade.messaging.MessagingObjects;
import org.cascadebot.cascade.permissions.CascadePermission;
import org.cascadebot.cascade.utils.FormatUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuildInfoSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        GuildData dataForList = context.getData();
        Guild guildForList = context.getGuild();

        if (context.getArgs().length > 0) {
            guildForList = Cascade.INS.getShardManager().getGuildById(context.getArg(0));
            dataForList = GuildDataManager.getGuildData(guildForList.getIdLong());
        }

        if (dataForList == null || guildForList == null) {
            context.getTypedMessaging().replyDanger("We couldn't find that guild!");
            return;
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
                                     (finalDataForList.getSettings().isModuleEnabled(module) ? UnicodeConstants.TICK : UnicodeConstants.RED_CROSS))
                             .collect(Collectors.joining("\n"));


        builder.addField("Flags", flags, false);
        builder.addField("Modules", modules, false);
        builder.addField("Join Date", FormatUtils.formatDateTime(context.getSelfMember().getJoinDate()), false);

        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "info";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

}

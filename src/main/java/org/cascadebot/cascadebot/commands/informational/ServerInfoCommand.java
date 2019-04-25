/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.informational;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Set;

public class ServerInfoCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Guild guildForInfo = context.getGuild();

        if (context.getArgs().length > 0) {
            guildForInfo = CascadeBot.INS.getShardManager().getGuildById(context.getArg(0));
        }
        if (guildForInfo == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.responses.cannot_find_guild"));
            return;
        }

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(guildForInfo.getName());
        builder.setThumbnail(guildForInfo.getIconUrl());
        builder.addField(context.i18n("commands.serverinfo.guild_creation_date"), FormatUtils.formatDateTime(guildForInfo.getCreationTime()), true);
        builder.addField(context.i18n("commands.serverinfo.guild_name"), guildForInfo.getName(), true);
        builder.addField(context.i18n("commands.serverinfo.guild_owner") + context.globalEmote("server_owner"), guildForInfo.getOwner().getUser().getAsTag(), true);
        builder.addField(context.i18n("commands.serverinfo.guild_region"), guildForInfo.getRegion().toString(), true);
        builder.addField(context.i18n("commands.serverinfo.guild_member_count"), String.valueOf(guildForInfo.getMemberCache().size()), true);
        builder.setFooter(context.i18n("commands.serverinfo.guild_id") + guildForInfo.getId(), guildForInfo.getIconUrl());

        context.getTypedMessaging().replyInfo(builder); // Send the embed
    }

    @Override
    public String command() {
        return "serverinfo";
    }

    @Override
    public Module getModule() {
        return Module.INFORMATIONAL;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Server info command", "serverinfo", true);
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("guildinfo");
    }

}

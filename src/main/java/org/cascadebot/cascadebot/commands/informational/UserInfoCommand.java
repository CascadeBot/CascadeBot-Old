/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.informational;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.language.LanguageUtils;
import org.cascadebot.shared.SecurityLevel;

public class UserInfoCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        User userForInfo = sender.getUser();
        if (context.getArgs().length > 0) {
            userForInfo = DiscordUtils.getUser(context.getGuild(), context.getMessage(0), true);
        }
        if (userForInfo == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.invalid_user"));
            return;
        }
        Member member = context.getGuild().getMember(userForInfo);
        String status = "";
        String statusName = "";
        EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, sender.getUser());


        if (member != null) {
            builder.addField(context.i18n("commands.userinfo.user_join_date"), FormatUtils.formatDateTime(member.getTimeJoined(), context.getLocale()), true);
            statusName = LanguageUtils.getEnumI18n(context.getLocale(), "statuses", member.getOnlineStatus());
            if (member.getActivities().size() > 0 && member.getActivities().stream().anyMatch(activity -> activity.getType() == Activity.ActivityType.STREAMING)) {
                status = context.globalEmote("streaming");
                statusName = context.i18n("statuses.streaming");
            } else if (member.getOnlineStatus() == OnlineStatus.ONLINE) {
                status = context.globalEmote("online");
            } else if (member.getOnlineStatus() == OnlineStatus.OFFLINE) {
                status = context.globalEmote("offline");
            } else if (member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
                status = context.globalEmote("dnd");
            } else if (member.getOnlineStatus() == OnlineStatus.IDLE) {
                status = context.globalEmote("idle");
            }
        }


        builder.setTitle(userForInfo.getAsTag());
        builder.setThumbnail(userForInfo.getAvatarUrl());
        builder.addField(context.i18n("commands.userinfo.user_created"), FormatUtils.formatDateTime(userForInfo.getTimeCreated(), context.getLocale()), true);
        builder.addField(context.i18n("commands.userinfo.user_id"), userForInfo.getId(), true);
        builder.addField(context.i18n("commands.userinfo.user_mutual_servers"), String.valueOf(userForInfo.getMutualGuilds().size()), true);
        long userId = userForInfo.getIdLong();
        SecurityLevel userSecurityLevel = CascadeBot.INS.getPermissionsManager().getUserSecurityLevel(userId);
        if (userSecurityLevel != null) {
            builder.addField(context.i18n("commands.userinfo.user_official_role"), FormatUtils.formatEnum(userSecurityLevel, context.getLocale()), true);
        }

        if (member != null) {
            builder.addField(context.i18n("words.status"), status + statusName, true);

            for (Activity activity : member.getActivities()) {
                String gameStatus;
                String gameType = LanguageUtils.getEnumI18n(context.getLocale(), "game_types", activity.getType());
                if (activity.isRich()) {
                    RichPresence presence = activity.asRichPresence();
                    gameStatus = String.format("%s **%s**", gameType, presence.getName());
                    if (presence.getDetails() != null) gameStatus += "\n*" + presence.getDetails() + "*";
                    if (presence.getState() != null) gameStatus += "\n*" + presence.getState() + "*";
                } else {
                    gameStatus = String.format("%s **%s**", gameType, activity.getName());
                }
                builder.addField(context.i18n("commands.userinfo.activity"), gameStatus, true);
            }

            context.getTypedMessaging().replyInfo(builder);
        }
    }

    @Override
    public String command() {
        return "userinfo";
    }

    @Override
    public Module getModule() {
        return Module.INFORMATIONAL;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("userinfo", true);
    }

}

/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.informational;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.RichPresence;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.Table;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserInfoCommand implements ICommandMain {

    Argument userArg = Argument.of("user", "Gets a specific users info", ArgumentType.OPTIONAL);

    @Override
    public void onCommand(Member sender, CommandContext context) {
        User userForInfo = sender.getUser();
        if (userArg.argExists(context.getArgs(), 0)) {
            userForInfo = DiscordUtils.getUser(context.getGuild(), context.getMessage(0), true);
        }
        if (userForInfo == null) {
            context.getTypedMessaging().replyDanger("Invalid User!");
            return;
        }
        Member member = context.getGuild().getMember(userForInfo);

        String status = "";
        String statusName = "";

        if (member != null) {
            statusName = FormatUtils.formatEnum(member.getOnlineStatus());
            if (member.getGame() != null && member.getGame().getType() == Game.GameType.STREAMING) {
                status = context.globalEmote("streaming");
                statusName = "Streaming";
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

        List<Page> pageList = new ArrayList<>();
        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder();
        builder.setTitle(userForInfo.getAsTag());
        builder.setThumbnail(userForInfo.getAvatarUrl());
        builder.addField("User Created", FormatUtils.formatDateTime(userForInfo.getCreationTime()), true);
        builder.addField("Join Date", FormatUtils.formatDateTime(member.getJoinDate()), true);
        builder.addField("User ID", userForInfo.getId(), true);
        builder.addField("Mutual Servers", String.valueOf(userForInfo.getMutualGuilds().size()), true);


        if (member != null) {
            builder.addField("Status", status + statusName, true);
            Game game = member.getGame();
            if (game != null) {
                String gameStatus;
                String gameType = FormatUtils.formatEnum(game.getType());
                switch (game.getType()) {
                    case LISTENING:
                        gameType += " to";
                        break;
                    case DEFAULT:
                        gameType = "Playing";
                        break;
                }
                if (game.isRich()) {
                    RichPresence presence = game.asRichPresence();
                    gameStatus = String.format("%s **%s**\n*%s*\n*%s*", gameType, presence.getName(), presence.getDetails(), presence.getState());
                } else {
                    gameStatus = String.format("%s **%s**", gameType, game.getName());
                }
                builder.addField("Activity", gameStatus, true);
            }

            Table.TableBuilder tableBuilder = new Table.TableBuilder("Role ID", "Role Name");

            for (Role role : member.getRoles()) {
                tableBuilder.addRow(role.getId(), role.getName());
            }

            pageList.add(new PageObjects.TablePage(tableBuilder.build()));
        }

        pageList.add(0, new PageObjects.EmbedPage(builder));
        context.getUIMessaging().sendPagedMessage(pageList);
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
        return CascadePermission.of("User info command", "userinfo", true);
    }

    @Override
    public String description() {
        return "Returns information regarding the specified user";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(userArg);
    }

}

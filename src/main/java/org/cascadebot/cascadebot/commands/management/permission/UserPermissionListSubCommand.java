/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.ArrayList;
import java.util.List;

public class UserPermissionListSubCommand extends DeprecatedSubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!context.getArg(1).equalsIgnoreCase("permissions") && !context.getArg(1).equalsIgnoreCase("groups")) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (member == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_user_matching", context.getArg(0)));
            return;
        }

        User user = context.getData().getManagement().getPermissions().getPermissionUser(member);

        if (context.getArg(1).equalsIgnoreCase("groups")) {
            StringBuilder groupsBuilder = new StringBuilder();
            if (user.getGroupIds().isEmpty()) {
                context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.list.no_groups", member.getUser().getAsTag()));
                return;
            }

            for (String id : user.getGroupIds()) {
                Group group = context.getData().getManagement().getPermissions().getGroupById(id);
                groupsBuilder.append(group.getName()).append(" (").append(group.getId()).append(")\n");
            }
            List<String> pageContent = PageUtils.splitString(groupsBuilder.toString(), 1000, '\n');
            List<Page> pages = new ArrayList<>();
            for (String content : pageContent) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle(context.i18n("commands.userperms.list.group_title", member.getUser().getAsTag()));
                builder.setDescription("```\n" + content + "```");
                pages.add(new PageObjects.EmbedPage(builder));
            }
            context.getUiMessaging().sendPagedMessage(pages);
        } else if (context.getArg(1).equalsIgnoreCase("permissions")) {
            if (user.getPermissions().isEmpty()) {
                context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.list.no_permissions", member.getUser().getAsTag()));
                return;
            }

            StringBuilder permsBuilder = new StringBuilder();
            for (String perm : user.getPermissions()) {
                permsBuilder.append(perm).append('\n');
            }
            List<String> pageContent = PageUtils.splitString(permsBuilder.toString(), 1000, '\n');
            List<Page> pages = new ArrayList<>();
            for (String content : pageContent) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle(context.i18n("commands.userperms.list.perms_title", member.getUser().getAsTag()));
                builder.setDescription("```\n" + content + "```");
                pages.add(new PageObjects.EmbedPage(builder));
            }
            context.getUiMessaging().sendPagedMessage(pages);
        }
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String parent() {
        return "userperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.user.list", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}

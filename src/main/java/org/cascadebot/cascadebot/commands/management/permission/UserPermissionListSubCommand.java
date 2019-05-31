/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.pagination.Page;
import org.cascadebot.cascadebot.utils.pagination.PageObjects;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

public class UserPermissionListSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage(this, "userperms");
            return;
        }

        if (!context.getArg(1).equalsIgnoreCase("permissions") && !context.getArg(1).equalsIgnoreCase("groups")) {
            context.getUIMessaging().replyUsage(this, "userperms");
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (member == null) {
            context.getTypedMessaging().replyDanger("User `" + context.getArg(0) + "` not found");
            return;
        }

        User user = context.getData().getPermissions().getPermissionUser(member);

        if (context.getArg(1).equalsIgnoreCase("groups")) {
            StringBuilder groupsBuilder = new StringBuilder();
            if (user.getGroupIds().isEmpty()) {
                context.getTypedMessaging().replyWarning("User has no groups!");
                return;
            }

            for (String id : user.getGroupIds()) {
                Group group = context.getData().getPermissions().getGroupById(id);
                groupsBuilder.append(group.getName()).append(" (").append(group.getId()).append(")\n");
            }
            List<String> pageContent = PageUtils.splitString(groupsBuilder.toString(), 1000, '\n');
            List<Page> pages = new ArrayList<>();
            for (String content : pageContent) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle(member.getUser().getAsTag() + "'s groups");
                builder.setDescription("```\n" + content + "```");
                pages.add(new PageObjects.EmbedPage(builder));
            }
            context.getUIMessaging().sendPagedMessage(pages);
        } else if (context.getArg(1).equalsIgnoreCase("permissions")) {
            if (user.getPermissions().isEmpty()) {
                context.getTypedMessaging().replyWarning("User has no permissions!");
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
                builder.setTitle(member.getUser().getAsTag() + "'s permissions");
                builder.setDescription("```\n" + content + "```");
                pages.add(new PageObjects.EmbedPage(builder));
            }
            context.getUIMessaging().sendPagedMessage(pages);
        }
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("User permissions list sub command", "permissions.user.group", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("user", null, ArgumentType.REQUIRED,
                Set.of(Argument.of("groups", "List the groups the user has", ArgumentType.COMMAND),
                        Argument.of("permissions", "List the permissions the user has", ArgumentType.COMMAND))));
    }
}

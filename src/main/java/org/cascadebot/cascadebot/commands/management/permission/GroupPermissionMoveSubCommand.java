/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.GuildPermissions;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

public class GroupPermissionMoveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getPermissions().getMode() == GuildPermissions.PermissionMode.MOST_RESTRICTIVE) {
            context.getTypedMessaging().replyDanger("Cannot move groups in most restrictive mode");
            return;
        }

        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "groupperms");
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if (context.getArgs().length > 1) {
                context.getData().getPermissions().getGroups().remove(group);
                try {
                    context.getData().getPermissions().getGroups().add(context.getArgAsInteger(1), group);
                    context.getTypedMessaging().replySuccess("Moved group " + group.getName() + " to position " + context.getArg(1));
                } catch (IndexOutOfBoundsException e) {
                    context.getTypedMessaging().replyWarning("Couldn't move group " + group.getName() + " to position " + context.getArg(1) + " as that is out of range");
                }
                return;
            }

            AtomicInteger currIndex = new AtomicInteger(context.getData().getPermissions().getGroups().indexOf(group));
            ButtonGroup buttonGroup = new ButtonGroup(sender.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel, message) -> {
                context.getData().getPermissions().moveGroup(context.getData().getPermissions().getGroups().get(currIndex.get()), currIndex.get() - 1);
                currIndex.addAndGet(-1);
                message.editMessage(getGroupsList(group, context.getData().getPermissions().getGroups())).queue();
            }));
            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel, message) -> {
                context.getData().getPermissions().moveGroup(context.getData().getPermissions().getGroups().get(currIndex.get()), currIndex.get() + 1);
                currIndex.addAndGet(1);
                message.editMessage(getGroupsList(group, context.getData().getPermissions().getGroups())).queue();
            }));

            context.getUIMessaging().sendButtonedMessage(getGroupsList(group, context.getData().getPermissions().getGroups()), buttonGroup);
        }, sender.getUser().getIdLong());
    }

    public String getGroupsList(Group targetGroup, List<Group> groups) {
        StringBuilder stringBuilder = new StringBuilder();

        int index = groups.indexOf(targetGroup);
        int min = index - 5;
        int max = index + 5;

        if (min < 0) {
            min = 0;
        }

        if (max >= groups.size() - 1) {
            max = groups.size() - 1;
        }

        for (int i = 0; i <= (max - min); i++) {
            int num = i + min;
            stringBuilder.append(num).append(": ");
            Group group = groups.get(num);
            if (group.getId().equals(targetGroup.getId())) {
                stringBuilder.append(UnicodeConstants.WHITE_HALLOW_SQUARE).append(" ");
            } else {
                stringBuilder.append(UnicodeConstants.WHILE_SQUARE).append(" ");
            }

            stringBuilder.append(group.getName()).append(" (").append(group.getId()).append(")\n");
        }

        return stringBuilder.toString();
    }

    @Override
    public String command() {
        return "move";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Group permissions move sub command", "permissions.group.move", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}

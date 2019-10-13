/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.GuildPermissions;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.Group;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

public class GroupPermissionMoveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getData().getPermissions().getMode() == GuildPermissions.PermissionMode.MOST_RESTRICTIVE) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.groupperms.move.wrong_mode")); //TODO provide docs link
            return;
        }

        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if (context.getArgs().length > 1 && context.isArgInteger(1)) {
                context.getData().getPermissions().moveGroup(group, context.getArgAsInteger(1));
                context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.move.moved", group.getName(), context.getArg(1)));
                return;
            }

            AtomicInteger currIndex = new AtomicInteger(context.getData().getPermissions().getGroups().indexOf(group));
            ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel, message) -> {
                if (buttonGroup.getOwner().getIdLong() != runner.getIdLong()) {
                    return;
                }
                context.getData().getPermissions().moveGroup(context.getData().getPermissions().getGroups().get(currIndex.get()), currIndex.get() - 1);
                currIndex.addAndGet(-1);
                message.editMessage(getGroupsList(group, context.getData().getPermissions().getGroups())).queue();
            }));
            buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel, message) -> {
                if (buttonGroup.getOwner().getIdLong() != runner.getIdLong()) {
                    return;
                }
                context.getData().getPermissions().moveGroup(context.getData().getPermissions().getGroups().get(currIndex.get()), currIndex.get() + 1);
                currIndex.addAndGet(1);
                message.editMessage(getGroupsList(group, context.getData().getPermissions().getGroups())).queue();
            }));

            context.getUIMessaging().sendButtonedMessage(getGroupsList(group, context.getData().getPermissions().getGroups()), buttonGroup);
        }, sender.getIdLong());
    }

    public String getGroupsList(Group targetGroup, List<Group> groups) {
        StringBuilder stringBuilder = new StringBuilder();

        int index = groups.indexOf(targetGroup);
        int min = Math.max(index - 5, 0);
        int max = Math.min(index + 5, groups.size() - 1);

        for (int i = 0; i <= (max - min); i++) {
            int num = i + min;

            Group group = groups.get(num);

            if (group.getId().equals(targetGroup.getId())) {
                stringBuilder.append(UnicodeConstants.WHITE_HALLOW_SQUARE).append(" ");
            } else {
                stringBuilder.append(UnicodeConstants.WHITE_SQUARE).append(" ");
            }
            stringBuilder.append(num).append(": ");
            stringBuilder.append(group.getName()).append(" (").append(group.getId()).append(")\n");
        }

        return stringBuilder.toString();
    }

    @Override
    public String command() {
        return "move";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.group.move", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}

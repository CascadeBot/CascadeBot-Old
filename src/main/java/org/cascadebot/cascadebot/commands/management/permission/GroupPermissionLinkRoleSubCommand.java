/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.ModlogEventData;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

import java.util.List;

public class GroupPermissionLinkRoleSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Role role = DiscordUtils.getRole(context.getArg(1), context.getGuild());
        if (role == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_role_matching", context.getArg(1)));
            return;
        }

        context.getData().write(guildData -> {
            PermissionCommandUtils.tryGetGroupFromString(context, guildData, context.getArg(0), group -> {
                if (group.linkRole(role.getIdLong())) {
                    context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.link.success", group.getName(), role.getName()));
                    ModlogEvent event = ModlogEvent.CASCADE_PERMISSIONS_GROUP_LINK;
                    ModlogEventData eventStore = new ModlogEventData(event, sender.getUser(), group, List.of());
                    eventStore.setExtraDescriptionInfo(List.of(role.getAsMention()));
                    context.getData().getModeration().sendModlogEvent(context.getGuild().getIdLong(), eventStore);
                } else {
                    context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.link.fail", group.getName(), role.getName()));
                }
            }, sender.getIdLong());
        });
    }

    @Override
    public String command() {
        return "link";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.link", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}

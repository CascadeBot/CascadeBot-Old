/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.ModlogEventStore;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.permissions.objects.User;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.LanguageEmbedField;

import java.util.ArrayList;
import java.util.List;

public class UserPermissionAddSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Member member = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        if (member == null) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.cannot_find_user_matching", context.getArg(0)));
            return;
        }

        User user = context.getData().getManagement().getPermissions().getPermissionUser(member);

        if (!context.getData().getPermissionsManager().isValidPermission(context.getArg(1))) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.permission_not_exist", context.getArg(1)));
            return;
        }

        if (user.addPermission(context.getArg(1))) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.userperms.add.success", context.getArg(1), member.getUser().getAsTag()));
            ModlogEvent event = ModlogEvent.CASCADE_PERMISSIONS_USER_PERMISSION_ADD;
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            embedFieldList.add(new LanguageEmbedField(true, "modlog.cascade_permissions.permission_added", "modlog.general.variable", context.getArg(1)));
            ModlogEventStore eventStore = new ModlogEventStore(event, sender.getUser(), member.getUser(), embedFieldList);
            context.getData().getModeration().sendModlogEvent(context.getGuild().getIdLong(), eventStore);
        } else {
            context.getTypedMessaging().replyWarning(context.i18n("commands.userperms.add.fail", context.getArg(1), member.getUser().getAsTag()));
        }
    }

    @Override
    public String command() {
        return "add";
    }

    @Override
    public String parent() {
        return "userperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.user.add", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}

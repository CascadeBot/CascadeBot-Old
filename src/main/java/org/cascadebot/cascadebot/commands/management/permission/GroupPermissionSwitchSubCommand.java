/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.PermissionMode;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;

public class GroupPermissionSwitchSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        PermissionMode mode = context.getData().getPermissionSettings().getMode();
        if (context.getArgs().length > 1) {
            mode = EnumUtils.getEnumIgnoreCase(PermissionMode.class, context.getArg(0));
            if (mode == null) {
                context.getTypedMessaging().replyDanger("commands.groupperms.switch.fail", context.getArg(0));
                return;
            }
        } else {
            switch (mode) {
                case HIERARCHICAL:
                    mode = PermissionMode.MOST_RESTRICTIVE;
                    break;
                case MOST_RESTRICTIVE:
                    mode = PermissionMode.HIERARCHICAL;
                    break;
            }
        }

        context.getData().getPermissionSettings().setMode(mode);
        context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.switch.success", FormatUtils.formatEnum(mode, context.getLocale())));
    }

    @Override
    public String command() {
        return "switch";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("permissions.group.switch", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return "Switches the permissions mode.";
    }

}

/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagDeleteSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        if (context.getCoreSettings().removeTag(context.getArg(0))) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.tag.delete.successfully_deleted_tag"));
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("commands.tag.delete.tag_doesnt_exist", context.getArg(0)));
        }
    }

    @Override
    public String command() {
        return "delete";
    }

    @Override
    public String parent() {
        return "tag";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("tag.delete", false);
    }

}

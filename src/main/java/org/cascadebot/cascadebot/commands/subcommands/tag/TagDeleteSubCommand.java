/*

  * Copyright (c) 2019 CascadeBot. All rights reserved.
  * Licensed under the MIT license.

 */

package org.cascadebot.cascadebot.commands.subcommands.tag;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagDeleteSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getData().removeTag(context.getMessage(0));
        context.getTypedMessaging().replySuccess("Successfully deleted tag!");
    }

    @Override
    public String command() {
        return null;
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

}

/*

 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.

 */

package org.cascadebot.cascadebot.commands.subcommands.tag;

import java.util.Set;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagDeleteSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUIMessaging().replyUsage(this, "tags");
            return;
        }

        if (context.getSettings().removeTag(context.getArg(0))) {
            context.getTypedMessaging().replySuccess("Successfully deleted tag!");
        } else {
            context.getTypedMessaging().replyDanger("Tag `" + context.getArg(0) + "` doesn't exist!");
        }
    }

    @Override
    public String command() {
        return "delete";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag delete sub command", "tag.delete", false);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("name", "Deletes tag with the giving name", ArgumentType.REQUIRED));
    }

}

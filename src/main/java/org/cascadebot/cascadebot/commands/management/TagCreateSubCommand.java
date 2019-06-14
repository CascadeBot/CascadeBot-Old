/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management;

import java.util.Set;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.data.objects.Tag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class TagCreateSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUIMessaging().replyUsage();
            return;
        }
        context.getSettings().addTag(context.getArg(0), new Tag(context.getMessage(1), "tag"));
        context.getTypedMessaging().replySuccess(context.i18n("commands.tag.create.successfully_created_tag" ,context.getArg(0)));
    }

    @Override
    public String command() {
        return "create";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("tag.create", false);
    }

}

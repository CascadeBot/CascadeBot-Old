/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.entities.Member;

import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ColorUtilsKt;
import org.cascadebot.cascadebot.utils.RandomUtils;

public class RandomColorSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply(ColorUtilsKt.getColorEmbed(RandomUtils.randomColor(), context));
    }

    @Override
    public String command() { return "color"; }

    @Override
    public String parent() { return "random"; }

    @Override
    public CascadePermission getPermission() { return null; }

}

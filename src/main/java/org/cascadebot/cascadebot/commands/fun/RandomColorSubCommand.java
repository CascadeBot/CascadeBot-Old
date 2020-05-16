/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ColorUtils;
import org.cascadebot.cascadebot.utils.RandomUtils;

public class RandomColorSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply(ColorUtils.getColorEmbed(RandomUtils.randomColor(), context));
    }

    @Override
    public String command() { return "color"; }

    @Override
    public String parent() { return "random"; }

    @Override
    public CascadePermission permission() { return null; }

}

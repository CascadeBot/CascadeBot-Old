/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ColorUtils;
import org.cascadebot.cascadebot.utils.RandomUtils;

import java.awt.*;
import java.util.Set;

public class RandomColorSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.reply(ColorUtils.getColor(RandomUtils.randomColor(), context));
    }

    @Override
    public String command() { return "color"; }

    @Override
    public String parent() { return "random"; }

    @Override
    public CascadePermission getPermission() { return null; }

}

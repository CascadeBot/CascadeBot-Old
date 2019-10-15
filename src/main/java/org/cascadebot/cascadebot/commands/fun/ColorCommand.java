/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ColorUtils;

import java.awt.Color;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length != 1) {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        context.reply(ColorUtils.getColor(context.getMessage(0), context));

    }

    @Override
    public Module getModule() {
        return Module.FUN;
    }


    @Override
    public String command() {
        return "color";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("color", true);
    }

}

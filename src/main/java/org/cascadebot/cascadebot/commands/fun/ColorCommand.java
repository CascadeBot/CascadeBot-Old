/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ColorException;
import org.cascadebot.cascadebot.utils.ColorUtilsKt;

import java.awt.Color;

public class ColorCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length != 1) {
            context.getUIMessaging().replyUsage();
            return;
        }

        try {
            Color color = ColorUtilsKt.getColor(context.getArg(0), context);
            context.reply(ColorUtilsKt.getColorEmbed(color, context));
        } catch (ColorException e) {
            context.getTypedMessaging().replyDanger(e.getI18nMessage(context.getLocale()));
        }
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

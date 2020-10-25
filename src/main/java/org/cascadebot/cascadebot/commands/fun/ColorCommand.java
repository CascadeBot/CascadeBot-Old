/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ColorUtils;

import java.awt.Color;

public class ColorCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUiMessaging().replyUsage();
            return;
        }

        try {
            Color color = ColorUtils.getColor(context.getMessage(0), context);
            context.reply(ColorUtils.getColorEmbed(color, context));
        } catch (ColorUtils.ColorException e) {
            context.getTypedMessaging().replyDanger(e.getI18nMessage(context.getLocale()));
        }

    }

    @Override
    public Module module() {
        return Module.FUN;
    }


    @Override
    public String command() {
        return "color";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("color", true);
    }

}

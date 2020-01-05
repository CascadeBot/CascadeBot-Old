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
import org.cascadebot.cascadebot.utils.RandomUtils;

import java.io.IOException;

public class JokeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        try {
            context.getTypedMessaging().replyInfo(RandomUtils.randomJoke());
        } catch (IOException | IllegalArgumentException e) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.joke.error_loading"));
        }
    }

    @Override
    public String command() {
        return "joke";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("joke", true);
    }

    @Override
    public Module getModule() {
        return Module.FUN;
    }

}
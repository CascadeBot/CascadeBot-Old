/*
 *
 *  * Copyright (c) 2019 CascadeBot. All rights reserved.
 *  * Licensed under the MIT license.
 *
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class PauseCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().getPlayer().isPaused()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.pause.already_paused", context.getCoreSettings().getPrefix()));
        } else {
            context.getMusicPlayer().getPlayer().setPaused(true);
            context.getTypedMessaging().replySuccess(context.i18n("commands.pause.successfully_paused", context.getCoreSettings().getPrefix()));
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "pause";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("pause", true);
    }

}

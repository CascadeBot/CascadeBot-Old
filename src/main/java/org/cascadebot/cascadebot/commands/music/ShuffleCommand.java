/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class ShuffleCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().toggleShuffleOnRepeat()) {
            context.getMusicPlayer().shuffle();
            context.getTypedMessaging().replySuccess(context.i18n("commands.shuffle.enabled"));
        } else {
            context.getTypedMessaging().replySuccess(context.i18n("commands.shuffle.disabled"));
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "shuffle";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("shuffle", true);
    }

}

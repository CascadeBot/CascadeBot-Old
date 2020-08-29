/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class ShuffleCommand extends MainCommand {

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
    public Module module() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "shuffle";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("shuffle", true);
    }

}

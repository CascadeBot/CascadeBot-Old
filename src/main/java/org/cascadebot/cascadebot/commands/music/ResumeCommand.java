/*
 *
 *  * Copyright (c) 2019 CascadeBot. All rights reserved.
 *  * Licensed under the MIT license.
 *
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class ResumeCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().isPaused()) {
            context.getMusicPlayer().setPaused(false);
            context.getTypedMessaging().replySuccess(context.i18n("commands.resume.successfully_resumed"));
        } else {
            context.getTypedMessaging().replyDanger(context.i18n("commands.resume.already_resumed", context.getCoreSettings().getPrefix()));
        }
    }

    @Override
    public Module module() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "resume";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("resume", true);
    }

}

/*

 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.

 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LoopCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            // This gets the next loop mode in the enum or returns to 0 if there are no more
            CascadePlayer.LoopMode loopMode = CascadePlayer.LoopMode.values()[(context.getMusicPlayer().getLoopMode().ordinal() + 1) % 3];
            context.getMusicPlayer().loopMode(loopMode);
            if (loopMode == CascadePlayer.LoopMode.DISABLED) {
                context.getTypedMessaging().replySuccess(context.i18n("commands.loop.looping_disabled"));
            } else {
                context.getTypedMessaging().replySuccess(context.i18n("commands.loop.looping_set", loopMode.name().toLowerCase()));
            }
        } else {
            if (EnumUtils.isValidEnum(CascadePlayer.LoopMode.class, context.getArg(0).toUpperCase())) {
                CascadePlayer.LoopMode loopMode = CascadePlayer.LoopMode.valueOf(context.getArg(0).toUpperCase());
                context.getMusicPlayer().loopMode(loopMode);
                if (loopMode == CascadePlayer.LoopMode.DISABLED) {
                    context.getTypedMessaging().replySuccess(context.i18n("commands.loop.looping_disabled"));
                } else {
                    context.getTypedMessaging().replySuccess(context.i18n("commands.loop.looping_set", loopMode.name().toLowerCase()));
                }
            } else {
                context.getTypedMessaging().replyDanger(
                        context.i18n("commands.loop.not_valid_mode", context.getArg(0),
                        Arrays.stream(CascadePlayer.LoopMode.values())
                                .map(loopMode -> "`" + loopMode + "`")
                                .collect(Collectors.joining(", ")))
                );
            }
        }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "loop";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("loop", true);
    }

}

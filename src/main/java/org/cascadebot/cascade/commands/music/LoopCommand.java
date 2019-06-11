/*

 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.

 */

package org.cascadebot.cascade.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.music.CascadePlayer;
import org.cascadebot.cascade.permissions.CascadePermission;

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
                context.getTypedMessaging().replySuccess("Looping has been disabled!");
            } else {
                context.getTypedMessaging().replySuccess("Set to loop the current **%s**", loopMode.name().toLowerCase());
            }
        } else {
            if (EnumUtils.isValidEnum(CascadePlayer.LoopMode.class, context.getArg(0).toUpperCase())) {
                CascadePlayer.LoopMode loopMode = CascadePlayer.LoopMode.valueOf(context.getArg(0).toUpperCase());
                context.getMusicPlayer().loopMode(loopMode);
                if (loopMode == CascadePlayer.LoopMode.DISABLED) {
                    context.getTypedMessaging().replySuccess("Looping has been disabled!");
                } else {
                    context.getTypedMessaging().replySuccess("Set to loop the current **%s**", loopMode.name().toLowerCase());
                }
            } else {
                context.getTypedMessaging().replyDanger("`%s` is not a valid loop mode! Valid modes are: %s",
                        context.getArg(0),
                        Arrays.stream(CascadePlayer.LoopMode.values())
                                .map(loopMode -> "`" + loopMode + "`")
                                .collect(Collectors.joining(", "))
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
        return CascadePermission.of("Loop command", "loop", true);
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("mode", "Sets the looping mode to the specified mode", ArgumentType.OPTIONAL));
    }

    @Override
    public String description() {
        return "Changes between loop modes";
    }

}

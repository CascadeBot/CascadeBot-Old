/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commandmeta;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.data.objects.Flag;
import org.cascadebot.cascade.permissions.CascadePermission;

import java.util.HashSet;
import java.util.Set;

public interface ICommandExecutable {

    void onCommand(Member sender, CommandContext context);

    String command();

    CascadePermission getPermission();

    String description();

    default boolean deleteMessages() {
        return true;
    }

    default Set<Flag> getFlags() {
        return Set.of();
    }

    /**
     * Not when overriding this. the system automatically handles sub command arguments so you only need to define arguments here that are not defined their.
     *
     * @return A set of arguments not being defined else where
     */
    default Set<Argument> getUndefinedArguments() {
        return Set.of();
    }

    default Set<Argument> getArguments() {
        Set<Argument> arguments = new HashSet<>(this.getUndefinedArguments());
        if (this instanceof ICommandMain) {
            for (ICommandExecutable subCommand : ((ICommandMain) this).getSubCommands()) {
                arguments.add(Argument.of(subCommand.command(), subCommand.description(), subCommand.getUndefinedArguments()));
            }
        }
        return Set.copyOf(arguments);
    }

}

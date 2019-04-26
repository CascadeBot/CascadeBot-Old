/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.HashSet;
import java.util.Set;

public interface ICommandExecutable {

    void onCommand(Member sender, CommandContext context);

    String command();

    CascadePermission getPermission();

    default String getDescriptionPath() {
        if (this instanceof ICommandMain) {
            ICommandMain commandMain = (ICommandMain) this;
            if (commandMain.getSubCommands().size() > 0) {
                return "command_descriptions." + command() + ".main_command";
            }
        }
        return "command_descriptions." + command();
    }

    default String getDescription(Locale locale) {
        return CascadeBot.INS.getLanguage().get(locale, getDescriptionPath());
    }


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
                // TODO: find a way to get the guild's locale in here
                arguments.add(Argument.of(subCommand.command(), subCommand.getDescription(Locale.getDefaultLocale()), subCommand.getUndefinedArguments()));
            }
        }
        return Set.copyOf(arguments);
    }

}

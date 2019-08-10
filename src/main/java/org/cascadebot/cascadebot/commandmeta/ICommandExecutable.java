/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public interface ICommandExecutable {

    void onCommand(Member sender, CommandContext context);

    default String getCommandPath() {
        return "commands." + getAbsoluteCommand() + ".command";
    }

    String command();

    default String command(Locale locale) {
        if (Language.hasLanguageEntry(locale, getCommandPath())) {
            return Language.i18n(locale, getCommandPath());
        } else {
            return command();
        }
    }

    default String getAbsoluteCommand() {
        if (this instanceof ISubCommand) {
            return ((ISubCommand) this).parent() + "." + command();
        }
        return command();
    }

    CascadePermission getPermission();

    default String getDescriptionPath() {
        return "commands." + getAbsoluteCommand() + ".description";
    }

    default String description() {
        return null;
    }

    default String description(Locale locale) {
        if (Language.hasLanguageEntry(locale, getDescriptionPath()) || description() == null) {
            return Language.i18n(locale, getDescriptionPath());
        } else {
            return description();
        }
    }


    default boolean deleteMessages() {
        return true;
    }

    default Set<Flag> getFlags() {
        return Set.of();
    }

}

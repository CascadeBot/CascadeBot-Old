/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface ICommandMain extends ICommandExecutable {

    Module getModule();

    default boolean forceDefault() {
        return false;
    }

    default Set<String> getGlobalAliases(Locale locale) {
        Optional<JsonElement> element = Language.getLanguage(locale).getElement("commands." + getAbsoluteCommand() + ".aliases");
        if (element.isEmpty() || !element.get().isJsonArray()) return Set.of();
        JsonArray array = element.get().getAsJsonArray();
        Set<String> aliases = new HashSet<>();
        array.forEach(arrayElement -> {
            if (arrayElement.isJsonPrimitive()) {
                aliases.add(arrayElement.getAsString());
            }
        });
        return Set.copyOf(aliases);
    }

    default Set<ISubCommand> getSubCommands() { return Set.of(); }

}

/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import com.google.gson.JsonArray;
import io.github.binaryoverload.JSONConfig;
import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ArgumentManager {

    @Getter
    private JSONConfig argumentsFile;

    public void initArguments() {
        try {
            argumentsFile = new JSONConfig(Objects.requireNonNull(CascadeBot.class.getClassLoader()
                    .getResourceAsStream("arguments.json")));
        } catch (Exception e) {
            CascadeBot.LOGGER.error("Cannot load arguments!", e);
            ShutdownHandler.exitWithError();
        }
    }

    public Set<Argument> getCommandArguments(ICommandExecutable command) {
        return getArguments(command.getAbsoluteCommand());
    }

    public Set<Argument> getArguments(String parent) {
        Optional<JSONConfig> argumentsConfig = argumentsFile.getSubConfig(parent);
        if (argumentsConfig.isEmpty()) return Set.of();
        Set<Argument> arguments = new HashSet<>();
        for (String key : argumentsConfig.get().getKeys(false)) {
            /*
                Checks if the last path node contains a "_". If it does, it means this path refers to a meta key
                and not an actual argument
                Example:
                    tag.raw - "raw" doesn't start with "_" so the loop for this key will not be skipped
                    tag.raw._type - "_type" starts with a "_" so the loop for this key will be skipped
            */
            if (key.charAt(key.lastIndexOf(".") + 1) == '_') continue;

            String id = parent + "." + key;

            String typeRaw = argumentsConfig.get().getString("_type").orElse("command");
            ArgumentType type = EnumUtils.isValidEnumIgnoreCase(ArgumentType.class, typeRaw) ? EnumUtils.getEnumIgnoreCase(ArgumentType.class, typeRaw) : ArgumentType.COMMAND;

            JsonArray aliasesRaw = argumentsConfig.get().getArray("_aliases").orElse(new JsonArray());
            Set<String> aliases = new HashSet<>();
            aliasesRaw.iterator().forEachRemaining(element -> aliases.add(element.getAsString()));

            Set<Argument> subArgs = getArguments(id);

            arguments.add(new Argument(id, type, subArgs, aliases));
        }
        return arguments;
    }

}

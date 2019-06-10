/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import io.github.binaryoverload.JSONConfig;
import lombok.Getter;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;

import java.util.HashSet;
import java.util.Objects;
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
        if (argumentsFile.getSubConfig(command.getAbsoluteCommand()).isEmpty()) return Set.of();
        Set<Argument> arguments = new HashSet<>();
        for (String key : argumentsFile.getSubConfig(command.getAbsoluteCommand()).get().getKeys(true)) {
            /*
                Checks if the last path node contains a "_". If it does, it means this path refers to a meta key
                and not an actual argument
                Example:
                    tag.raw - "raw" doesn't start with "_" so the loop for this key will not be skipped
                    tag.raw._type - "_type" starts with a "_" so the loop for this key will be skipped
            */
            if (key.charAt(key.lastIndexOf(".") + 1) != '_') continue;


        }
        // TODO: Don't do this
        return arguments;
    }

}

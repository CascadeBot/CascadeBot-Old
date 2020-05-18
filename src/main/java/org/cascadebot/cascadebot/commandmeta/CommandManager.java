/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import lombok.Getter;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    @Getter
    private List<MainCommand> commands;

    public CommandManager() {
        long start = System.currentTimeMillis();
        try {
            List<MainCommand> commands = new ArrayList<>();
            for (Class<?> c : ReflectionUtils.getClasses("org.cascadebot.cascadebot.commands")) {
                if (MainCommand.class.isAssignableFrom(c)) {
                    MainCommand command = (MainCommand) ConstructorUtils.invokeConstructor(c);
                    commands.add(command);
                }
            }
            LOGGER.info("Loaded {} commands in {}ms.", commands.size(), (System.currentTimeMillis() - start));
            this.commands = List.copyOf(commands);
        } catch (Exception e) {
            LOGGER.error("Could not load commands!", e);
            ShutdownHandler.exitWithError();
        }
    }

    public MainCommand getCommand(String command) {
        for (MainCommand cmd : commands) {
            if (cmd.command().equals(command)) return cmd;
        }
        return null;
    }

    public MainCommand getCommand(String command, GuildData data) {
        for (MainCommand cmd : commands) {
            if (data.getCore().getCommandAliases(cmd).contains(command)) {
                return cmd;
            }
        }

        // Fallback to default if cannot find command
        for (MainCommand cmd : commands) {
            if (cmd.command(data.getLocale()).equals(command)) {
                return cmd;
            } else if (cmd.globalAliases(data.getLocale()).contains(command)) {
                return cmd;
            }
        }
        // Fallback to default if cannot find command
        return getCommand(command);
    }

    public List<MainCommand> getCommandsByModule(Module type) {
        return commands.stream().filter(command -> command.module() == type).collect(Collectors.toList());
    }

    public MainCommand getCommandByDefault(String defaultCommand) {
        return commands.stream().filter(command -> command.command().equalsIgnoreCase(defaultCommand)).findFirst().orElse(null);
    }

}

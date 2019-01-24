/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.ShutdownHandler;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.cascadebot.cascadebot.utils.ReflectionUtils;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CommandManager {

    private static CommandManager instance = null;

    private final List<IMainCommand> commands = new CopyOnWriteArrayList<>();
    private final Logger logger = LoggerFactory.getLogger("Command Manager");

    public CommandManager() {
        instance = this;

        long start = System.currentTimeMillis();
        try {
            for (Class<?> c : ReflectionUtils.getClasses("com.cascadebot.cascadebot.commands")) {
                if (ICommandExecutable.class.isAssignableFrom(c))
                    commands.add((IMainCommand) ConstructorUtils.invokeConstructor(c));
            }
            logger.info("Loaded {} commands in {}ms.", commands.size(), (System.currentTimeMillis() - start));
        } catch (Exception e) {
            logger.error("Could not load commands!", e);
            ShutdownHandler.exitWithError();
        }
    }

    public IMainCommand getCommand(String command, User user, GuildData data) {
        for (IMainCommand cmd : getCommands()) {
            if (data.getCommandName(cmd).equalsIgnoreCase(command)) {
                return cmd;
            } else if (data.getCommandArgs(cmd).contains(command)) {
                return cmd;
            }
        }
        return null;
    }

    public List<IMainCommand> getCommands() {
        return commands;
    }

    public List<IMainCommand> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    public ICommandExecutable getCommandByDefault(String defaultCommand) {
        return commands.stream().filter(command -> command.command().equalsIgnoreCase(defaultCommand)).findFirst().orElse(null);
    }

    public static CommandManager instance() {
        return instance;
    }

}

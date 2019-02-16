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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static CommandManager instance = null;

    private final List<ICommandMain> commands = Collections.synchronizedList(new ArrayList<>());

    public CommandManager() {
        instance = this;

        long start = System.currentTimeMillis();
        try {
            for (Class<?> c : ReflectionUtils.getClasses("com.cascadebot.cascadebot.commands")) {
                if (ICommandMain.class.isAssignableFrom(c))
                    commands.add((ICommandMain) ConstructorUtils.invokeConstructor(c));
            }
            LOGGER.info("Loaded {} commands in {}ms.", commands.size(), (System.currentTimeMillis() - start));
        } catch (Exception e) {
            LOGGER.error("Could not load commands!", e);
            ShutdownHandler.exitWithError();
        }
    }

    public ICommandMain getCommand(String command, User user, GuildData data) {
        synchronized (commands) {
            for (ICommandMain cmd : commands) {
                if (data.getCommandName(cmd).equalsIgnoreCase(command)) {
                    return cmd;
                } else if (data.getCommandArgs(cmd).contains(command)) {
                    return cmd;
                }
            }
        }
        return null;
    }

    public List<ICommandMain> getCommands() {
        return commands;
    }

    public List<ICommandMain> getCommandsByModule(Module type) {
        synchronized (commands) {
            return commands.stream().filter(command -> command.getModule() == type).collect(Collectors.toList());
        }
    }

    public ICommandExecutable getCommandByDefault(String defaultCommand) {
        synchronized (commands) {
            return commands.stream().filter(command -> command.command().equalsIgnoreCase(defaultCommand)).findFirst().orElse(null);
        }
    }

    public static CommandManager instance() {
        return instance;
    }

}

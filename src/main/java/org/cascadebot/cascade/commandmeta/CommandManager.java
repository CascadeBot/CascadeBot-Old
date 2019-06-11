/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commandmeta;

import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.cascadebot.cascade.ShutdownHandler;
import org.cascadebot.cascade.data.objects.GuildData;
import org.cascadebot.cascade.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    @Getter
    private List<ICommandMain> commands;

    public CommandManager() {
        long start = System.currentTimeMillis();
        try {
            List<ICommandMain> commands = new ArrayList<>();
            for (Class<?> c : ReflectionUtils.getClasses("org.cascadebot.cascade.commands")) {
                if (ICommandMain.class.isAssignableFrom(c)) {
                    ICommandMain command = (ICommandMain) ConstructorUtils.invokeConstructor(c);
                    if (command.getModule() == null) {
                        throw new IllegalStateException(String.format("Command %s could not be loaded as its module was null!", command.getClass().getSimpleName()));
                    }
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

    public ICommandMain getCommand(String command, User user, GuildData data) {
        for (ICommandMain cmd : commands) {
            if (data.getCommandName(cmd).equalsIgnoreCase(command)) {
                return cmd;
            } else if (data.getCommandAliases(cmd).contains(command)) {
                return cmd;
            }
        }
        return null;
    }

    public List<ICommandMain> getCommandsByModule(Module type) {
        return commands.stream().filter(command -> command.getModule() == type).collect(Collectors.toList());
    }

    public ICommandMain getCommandByDefault(String defaultCommand) {
        return commands.stream().filter(command -> command.command().equalsIgnoreCase(defaultCommand)).findFirst().orElse(null);
    }

}

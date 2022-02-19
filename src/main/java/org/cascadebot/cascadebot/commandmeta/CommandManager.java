/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import lombok.Getter;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.ShutdownHandler;
import org.cascadebot.cascadebot.data.entities.GuildSettingsCoreEntity;
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
            if (cmd.command().equals(command)) {
                return cmd;
            }
        }
        return null;
    }

    public MainCommand getCommand(String command, long guildId) {
        /*for (MainCommand cmd : commands) { TODO aliases? We might just want to expand the language capabilities of this in general
            if (data.getCore().getCommandAliases(cmd).equals(command)) {
                return cmd;
            }
        }*/

        // Fallback to default if cannot find command

        return CascadeBot.INS.getPostgresManager().transaction(session -> {
            GuildSettingsCoreEntity coreEntity = session.get(GuildSettingsCoreEntity.class, guildId);
            if (coreEntity == null) {
                return null;
            }
            for (MainCommand cmd : commands) {
                if (cmd.command(coreEntity.getLocale()).equals(command)) {
                    return cmd;
                } else if (cmd.globalAliases(coreEntity.getLocale()).contains(command)) {
                    return cmd;
                }
            }
            return null;
        });
    }

    public List<MainCommand> getCommandsByModule(Module type) {
        return commands.stream().filter(command -> command.module() == type).collect(Collectors.toList());
    }

    public MainCommand getCommandByDefault(String defaultCommand) {
        return commands.stream().filter(command -> command.command().equalsIgnoreCase(defaultCommand)).findFirst().orElse(null);
    }

}

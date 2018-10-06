package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.ExitCodes;
import com.cascadebot.cascadebot.objects.GuildData;
import com.cascadebot.cascadebot.utils.ReflectionUtils;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CommandManager {

    private static CommandManager instance = null;

    private final List<ICommand> commands = new CopyOnWriteArrayList<>();
    private final Logger logger = LoggerFactory.getLogger("ICommand Manager");

    public CommandManager() {
        instance = this;

        long start = System.currentTimeMillis();
        try {
            for (Class<?> c : ReflectionUtils.getClasses("com.cascadebot.cascadebot.commands")) {
                if (ICommand.class.isAssignableFrom(c))
                    commands.add((ICommand) ConstructorUtils.invokeConstructor(c));
            }
            logger.info("Loaded {} commands in {}ms.", commands.size(), (System.currentTimeMillis() - start));
        } catch (ClassNotFoundException | IOException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("Could not load commands!", e);
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
        }
    }

    public ICommand getCommand(String command, User user, GuildData data) {
        for (ICommand cmd : getCommands()) {
            if (data.getCommandName(cmd).equalsIgnoreCase(command)) {
                return cmd;
            } else if (ArrayUtils.contains(data.getCommandArgs(cmd), command)) {
                return cmd;
            }
        }
        return null;
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    public List<ICommand> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    public static CommandManager instance() {
        return instance;
    }

}

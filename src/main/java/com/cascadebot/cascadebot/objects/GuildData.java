package com.cascadebot.cascadebot.objects;

import com.cascadebot.cascadebot.commands.Command;
import com.cascadebot.cascadebot.commands.CommandManager;
import com.cascadebot.cascadebot.commands.CommandType;

import java.util.concurrent.ConcurrentHashMap;

public class GuildData {

    private long guildID;
    private ConcurrentHashMap<Class<? extends Command>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();

    public GuildData(long guildID) {
        this.guildID = guildID;
    }

    public void enableCommand(Command command) {
        if (!command.getType().isAvailableModule()) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setDisabled(false);
        }
    }

    public void enableCommandByType(CommandType commandType) {
        for (Command command : CommandManager.instance().getCommandsByType(commandType)) {
            enableCommand(command);
        }
    }

    public void disableCommand(Command command) {
        if (!command.getType().isAvailableModule()) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setDisabled(false);
        }
    }

    public void disableCommandByType(CommandType commandType) {
        if (!commandType.isAvailableModule()) return;
        for (Command command : CommandManager.instance().getCommandsByType(commandType)) {
            disableCommand(command);
        }
    }

    public boolean isCommandEnabled(Command command) {
        if (commandInfo.contains(command.getClass())) {
            return !commandInfo.get(command.getClass()).isDisabled();
        }
        return false;
    }

    public boolean isTypeEnabled(CommandType type) {
        boolean enabled = true;
        for (Command command : CommandManager.instance().getCommandsByType(type)) {
            enabled &= !commandInfo.get(command.getClass()).isDisabled();
        }
        return enabled;
    }

    public String getCommandName(Command command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getCommand();
        }
        return command.defaultCommand();
    }

    public String[] getCommandArgs(Command command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getAliases();
        }
        return command.getGlobalAliases();
    }

    public long getGuildID() {
        return guildID;
    }
}

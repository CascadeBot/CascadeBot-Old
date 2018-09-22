package com.cascadebot.cascadebot.objects;

import com.cascadebot.cascadebot.commands.Command;
import com.cascadebot.cascadebot.commands.CommandManager;
import com.cascadebot.cascadebot.commands.CommandType;

import java.util.HashSet;
import java.util.Set;

public class GuildData {

    private long guildID;
    private Set<Command> enabledCommands = new HashSet<>(CommandManager.instance().getCommands());

    public GuildData(long guildID) {
        this.guildID = guildID;
    }

    public void enableCommand(Command command) {
        if (!command.getType().isAvailableModule()) return;
        enabledCommands.add(command);
    }

    public void enableCommandByType(CommandType commandType) {
        if (!commandType.isAvailableModule()) return;
        enabledCommands.addAll(CommandManager.instance().getCommandsByType(commandType));
    }

    public void disableCommand(Command command) {
        if (!command.getType().isAvailableModule()) return;
        enabledCommands.remove(command);
    }

    public void disableCommandByType(CommandType commandType) {
        if (!commandType.isAvailableModule()) return;
        enabledCommands.removeAll(CommandManager.instance().getCommandsByType(commandType));
    }

}

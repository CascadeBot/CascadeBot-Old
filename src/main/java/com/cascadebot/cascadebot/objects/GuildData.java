/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.objects;

import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuildData {

    private static Map<Long, GuildData> guildDataMap = new ConcurrentHashMap<>();

    private long guildID;
    private ConcurrentHashMap<Class<? extends ICommand>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();

    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix

    private ButtonsCache buttonsCache = new ButtonsCache(5);


    private GuildData(long guildID) {
        this.guildID = guildID;
    }

    public void enableCommand(ICommand command) {
        if (!command.getType().isAvailableModule()) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setDisabled(false);
        }
    }

    public void enableCommandByType(CommandType commandType) {
        for (ICommand command : CommandManager.instance().getCommandsByType(commandType)) {
            enableCommand(command);
        }
    }

    public void disableCommand(ICommand command) {
        if (!command.getType().isAvailableModule()) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setDisabled(false);
        }
    }

    public void disableCommandByType(CommandType commandType) {
        if (!commandType.isAvailableModule()) return;
        for (ICommand command : CommandManager.instance().getCommandsByType(commandType)) {
            disableCommand(command);
        }
    }

    public boolean isCommandEnabled(ICommand command) {
        if (commandInfo.contains(command.getClass())) {
            return !commandInfo.get(command.getClass()).isDisabled();
        }
        return false;
    }

    public boolean isTypeEnabled(CommandType type) {
        boolean enabled = true;
        for (ICommand command : CommandManager.instance().getCommandsByType(type)) {
            enabled &= !commandInfo.get(command.getClass()).isDisabled();
        }
        return enabled;
    }

    public String getCommandName(ICommand command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getCommand();
        }
        return command.defaultCommand();
    }

    public String[] getCommandArgs(ICommand command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getAliases();
        }
        return command.getGlobalAliases();
    }

    public long getGuildID() {
        return guildID;
    }

    public boolean isMentionPrefix() {
        return mentionPrefix;
    }

    public void setMentionPrefix(boolean mentionPrefix) {
        this.mentionPrefix = mentionPrefix;
    }

    public void addButtonGroup(TextChannel channel, Message message, ButtonGroup group) {
        group.setMessage(message.getIdLong());
        buttonsCache.put(channel.getIdLong(), message.getIdLong(), group);
    }

    public ButtonsCache getButtonsCache() {
        return buttonsCache;
    }

    public static GuildData getGuildData(Long id) {
        return guildDataMap.computeIfAbsent(id, GuildData::new);
    }

}

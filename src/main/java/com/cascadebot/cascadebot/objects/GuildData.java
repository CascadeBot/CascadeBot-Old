/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.objects;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.utils.buttons.Button;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuildData {

    private long guildID;
    private ConcurrentHashMap<Class<? extends ICommand>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();

    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix

    private Map<Long, LinkedHashMap<Long, ButtonGroup>> buttonGroups = new HashMap<>(); //Long 1 is channel id. land 2 is message id.

    int maxSize = 5;


    public GuildData(long guildID) {
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

    //Binary i hope you have a better way of handling this.
    public void addButtonGroup(TextChannel channel, Message message, ButtonGroup group) {
        group.setMessage(message.getIdLong());
        if(!buttonGroups.containsKey(channel.getIdLong())) {
            LinkedHashMap<Long, ButtonGroup> subMap = new LinkedHashMap<>() {
                @Override
                protected boolean removeEldestEntry(final Map.Entry<Long, ButtonGroup> eldest) {
                    if(size() > maxSize) {
                        channel.getMessageById(eldest.getKey()).queue(buttonedMessage -> buttonedMessage.clearReactions().queue());
                        return true;
                    }
                    return false;
                }
            };
            subMap.put(message.getIdLong(), group);
            buttonGroups.put(channel.getIdLong(), subMap);
        } else {
            LinkedHashMap<Long, ButtonGroup> subMap = buttonGroups.get(channel.getIdLong());
            subMap.put(message.getIdLong(), group);
            buttonGroups.put(channel.getIdLong(), subMap);
        }
    }

}

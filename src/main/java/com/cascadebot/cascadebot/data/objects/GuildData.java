/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.objects;

import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import com.cascadebot.cascadebot.utils.pagination.PageCache;
import com.cascadebot.shared.Version;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@BsonDiscriminator
public class GuildData {

    private static Map<Long, GuildData> guildDataMap = new ConcurrentHashMap<>();

    @BsonProperty("guild_id")
    private long guildID;

    private Version configVersion = Constants.CONFIG_VERSION;

    @BsonProperty("commands")
    private ConcurrentHashMap<Class<? extends ICommand>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();

    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix
    private boolean useEmbedForMessages = true;

    @BsonIgnore
    private ButtonsCache buttonsCache = new ButtonsCache(5);

    @BsonIgnore
    private PageCache pageCache = new PageCache();

    protected GuildData() {} // for morphia

    public GuildData(long guildID) {
        this.guildID = guildID;
    }

    public void enableCommand(ICommand command) {
        if (!command.getType().isAvailableModule()) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setEnabled(true);
            updateCommand(command);
        }
    }

    public void enableCommandByType(CommandType commandType) {
        for (ICommand command : CommandManager.instance().getCommandsByType(commandType)) {
            enableCommand(command);
        }
    }

    public void disableCommand(ICommand command) {
        if (!command.getType().isAvailableModule()) return;
        commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command)).setEnabled(false);
        updateCommand(command);
    }

    public void disableCommandByType(CommandType commandType) {
        if (!commandType.isAvailableModule()) return;
        for (ICommand command : CommandManager.instance().getCommandsByType(commandType)) {
            disableCommand(command);
        }
    }

    private void updateCommand(ICommand command) {
        GuildDataMapper.update(guildID, Updates.combine(
                Updates.set(
                        "config.commands." + command.defaultCommand(),
                        GuildDataMapper.processCommandInfo(commandInfo.get(command.getClass()))
                )
        ));
    }

    public boolean isCommandEnabled(ICommand command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).isEnabled();
        }
        return true;
    }

    public boolean isTypeEnabled(CommandType type) {
        boolean enabled = true;
        for (ICommand command : CommandManager.instance().getCommandsByType(type)) {
            enabled &= commandInfo.get(command.getClass()).isEnabled();
        }
        return enabled;
    }

    public String getCommandName(ICommand command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getCommand();
        }
        return command.defaultCommand();
    }

    public Set<String> getCommandArgs(ICommand command) {
        if (commandInfo.contains(command.getClass())) {
            return getGuildCommandInfo(command).getAliases();
        }
        return command.getGlobalAliases();
    }

    public void setCommandName(ICommand command, String commandName) {
        getGuildCommandInfo(command).setCommand(commandName);
        updateCommand(command);
    }

    public boolean addAlias(ICommand command, String alias) {
        boolean success = getGuildCommandInfo(command).addAlias(alias);
        updateCommand(command);
        return success;
    }

    public boolean removeAlias(ICommand command, String alias) {
        boolean success = getGuildCommandInfo(command).removeAlias(alias);
        updateCommand(command);
        return success;
    }

    private GuildCommandInfo getGuildCommandInfo(ICommand command) {
        return commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command));
    }

    public long getGuildID() {
        return guildID;
    }

    public boolean isMentionPrefix() {
        return mentionPrefix;
    }

    public void setMentionPrefix(boolean mentionPrefix) {
        this.mentionPrefix = mentionPrefix;
        GuildDataMapper.update(guildID, Updates.combine(
                Updates.set("config.mention_prefix", mentionPrefix)
        ));
    }

    public boolean getUseEmbedForMessages() {
        return useEmbedForMessages;
    }

    public void setUseEmbedForMessages(boolean useEmbedForMessages) {
        this.useEmbedForMessages = useEmbedForMessages;
        GuildDataMapper.update(guildID, Updates.combine(
                Updates.set("useEmbedForMessages", useEmbedForMessages)
        ));
    }

    public void addButtonGroup(TextChannel channel, Message message, ButtonGroup group) {
        group.setMessage(message.getIdLong());
        buttonsCache.put(channel.getIdLong(), message.getIdLong(), group);
    }

    public ButtonsCache getButtonsCache() {
        return buttonsCache;
    }

    public Collection<GuildCommandInfo> getGuildCommandInfos() {
        return commandInfo.values();
    }

    public Version getConfigVersion() {
        return configVersion;
    }

    public PageCache getPageCache() {
        return pageCache;
    }

}

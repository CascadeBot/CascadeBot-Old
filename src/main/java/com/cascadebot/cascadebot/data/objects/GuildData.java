/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.objects;

import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.IMainCommand;
import com.cascadebot.cascadebot.commandmeta.IMainCommand;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import com.cascadebot.cascadebot.utils.pagination.PageCache;
import com.cascadebot.shared.Version;
import de.bild.codec.annotations.Id;
import de.bild.codec.annotations.Transient;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@BsonDiscriminator
public class GuildData {

    @Id
    private long guildID;

    private Date creationDate = new Date();

    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix

    private Version configVersion = Constants.CONFIG_VERSION;

    private ConcurrentHashMap<Class<? extends IMainCommand>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();

    private boolean useEmbedForMessages = true;

    @Transient
    private ButtonsCache buttonsCache = new ButtonsCache(5);

    @Transient
    private PageCache pageCache = new PageCache();

    private GuildData() {} // This is for mongodb object serialisation

    public GuildData(long guildID) {
        this.guildID = guildID;
    }

    public void enableCommand(IMainCommand command) {
        if (!command.getType().isAvailableModule()) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setEnabled(true);
        }
    }

    public void enableCommandByType(CommandType commandType) {
        for (IMainCommand command : CommandManager.instance().getCommandsByType(commandType)) {
            enableCommand(command);
        }
    }

    public void disableCommand(IMainCommand command) {
        if (!command.getType().isAvailableModule()) return;
        commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command)).setEnabled(false);
    }

    public void disableCommandByType(CommandType commandType) {
        if (!commandType.isAvailableModule()) return;
        for (IMainCommand command : CommandManager.instance().getCommandsByType(commandType)) {
            disableCommand(command);
        }
    }

    public boolean isCommandEnabled(IMainCommand command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).isEnabled();
        }
        return true;
    }

    public boolean isTypeEnabled(CommandType type) {
        boolean enabled = true;
        for (IMainCommand command : CommandManager.instance().getCommandsByType(type)) {
            enabled &= commandInfo.get(command.getClass()).isEnabled();
        }
        return enabled;
    }

    public String getCommandName(IMainCommand command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getCommand();
        }
        return command.command();
    }

    public Set<String> getCommandArgs(IMainCommand command) {
        if (commandInfo.contains(command.getClass())) {
            return getGuildCommandInfo(command).getAliases();
        }
        return command.getGlobalAliases();
    }

    public void setCommandName(IMainCommand command, String commandName) {
        getGuildCommandInfo(command).setCommand(commandName);
    }

    public boolean addAlias(IMainCommand command, String alias) {
        boolean success = getGuildCommandInfo(command).addAlias(alias);
        return success;
    }

    public boolean removeAlias(IMainCommand command, String alias) {
        boolean success = getGuildCommandInfo(command).removeAlias(alias);
        return success;
    }

    @BsonIgnore
    private GuildCommandInfo getGuildCommandInfo(IMainCommand command) {
        return commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command));
    }

    public ConcurrentHashMap<Class<? extends IMainCommand>, GuildCommandInfo> getCommandInfo() {
        return commandInfo;
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

    public boolean getUseEmbedForMessages() {
        return useEmbedForMessages;
    }

    public void setUseEmbedForMessages(boolean useEmbedForMessages) {
        this.useEmbedForMessages = useEmbedForMessages;
    }

    public void addButtonGroup(MessageChannel channel, Message message, ButtonGroup group) {
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

    public Date getCreationDate() {
        return creationDate;
    }

}

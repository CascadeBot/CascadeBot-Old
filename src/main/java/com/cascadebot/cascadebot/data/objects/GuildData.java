/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.objects;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.data.Config;
import com.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import com.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import com.cascadebot.cascadebot.utils.pagination.PageCache;
import com.cascadebot.shared.Version;
import com.google.common.collect.Sets;
import de.bild.codec.annotations.Id;
import de.bild.codec.annotations.PreSave;
import de.bild.codec.annotations.Transient;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@BsonDiscriminator
public class GuildData {

    @Id
    private long guildID;

    //region Meta information
    private UUID stateLock = UUID.randomUUID(); // This is for checking state between the wrapper, bot and panel
    private Date creationDate = new Date();
    private Version configVersion = Constants.CONFIG_VERSION;
    //endregion

    private ConcurrentHashMap<Class<? extends ICommandMain>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();
    private Set<Module> enabledModules = Sets.newConcurrentHashSet(
            Sets.newHashSet(
                    Module.CORE,
                    Module.MANAGEMENT,
                    Module.INFORMATIONAL
            )
    );
    private String commandPrefix = Config.INS.getDefaultPrefix();

    //region Boolean flags
    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix
    private boolean deleteCommandMessages = true;
    private boolean useEmbedForMessages = true;
    private boolean displayPermissionErrors = true; // Whether commands will silently fail on no permissions
    private boolean displayModuleErrors = false;
    //endregion

    //region Transient fields
    @Transient
    private ButtonsCache buttonsCache = new ButtonsCache(5);

    @Transient
    private PageCache pageCache = new PageCache();
    //endregion

    private GuildData() {} // This is for mongodb object serialisation

    @PreSave
    public void preSave() {
        this.stateLock = UUID.randomUUID();
    }

    public GuildData(long guildID) {
        this.guildID = guildID;
    }

    //region Commands
    public void enableCommand(ICommandMain command) {
        if (!command.getModule().isPublicModule()) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setEnabled(true);
        }
    }

    public void enableCommandByType(Module module) {
        for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommandsByModule(module)) {
            enableCommand(command);
        }
    }

    public void disableCommand(ICommandMain command) {
        if (!command.getModule().isPublicModule()) return;
        commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command)).setEnabled(false);
    }

    public void disableCommandByType(Module module) {
        if (!module.isPublicModule()) return;
        for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommandsByModule(module)) {
            disableCommand(command);
        }
    }

    public boolean isCommandEnabled(ICommandMain command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).isEnabled();
        }
        return true;
    }

    public boolean isTypeEnabled(Module type) {
        boolean enabled = true;
        for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommandsByModule(type)) {
            enabled &= commandInfo.get(command.getClass()).isEnabled();
        }
        return enabled;
    }

    public String getCommandName(ICommandMain command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getCommand();
        }
        return command.command();
    }

    public void setCommandName(ICommandMain command, String commandName) {
        getGuildCommandInfo(command).setCommand(commandName);
    }

    public Set<String> getCommandAliases(ICommandMain command) {
        if (commandInfo.contains(command.getClass())) {
            return getGuildCommandInfo(command).getAliases();
        }
        return command.getGlobalAliases();
    }

    public boolean addAlias(ICommandMain command, String alias) {
        boolean success = getGuildCommandInfo(command).addAlias(alias);
        return success;
    }

    public boolean removeAlias(ICommandMain command, String alias) {
        boolean success = getGuildCommandInfo(command).removeAlias(alias);
        return success;
    }

    @BsonIgnore
    private GuildCommandInfo getGuildCommandInfo(ICommandMain command) {
        return commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command));
    }

    public ConcurrentHashMap<Class<? extends ICommandMain>, GuildCommandInfo> getCommandInfo() {
        return commandInfo;
    }
    //endregion

    //region Modules
    public void enableModule(Module module) {
        if (!module.isPublicModule()) {
            throw new IllegalArgumentException("This module is not available to be enabled!");
        }
        this.enabledModules.add(module);
    }

    public void disableModule(Module module) {
        if (!module.isPublicModule()) {
            throw new IllegalArgumentException("This module is not available to be disabled!");
        } else if (Module.CORE_MODULES.contains(module)) {
            throw new IllegalArgumentException(String.format("Cannot disable the %s module!", module.toString().toLowerCase()));
        }
        this.enabledModules.remove(module);
    }

    public boolean isModuleEnabled(Module module) {
        return this.enabledModules.contains(module);
    }
    //endregion

    public void addButtonGroup(MessageChannel channel, Message message, ButtonGroup group) {
        group.setMessage(message.getIdLong());
        buttonsCache.put(channel.getIdLong(), message.getIdLong(), group);
    }

    //region Getters and setters
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

    public ButtonsCache getButtonsCache() {
        return buttonsCache;
    }

    public Collection<GuildCommandInfo> getGuildCommandInfos() {
        return Collections.unmodifiableCollection(commandInfo.values());
    }

    public Set<Module> getEnabledModules() {
        return Collections.unmodifiableSet(enabledModules);
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

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public boolean willDisplayPermissionErrors() {
        return displayPermissionErrors;
    }

    public void setDisplayPermissionErrors(boolean displayPermissionErrors) {
        this.displayPermissionErrors = displayPermissionErrors;
    }

    public boolean willDisplayModuleErrors() {
        return displayModuleErrors;
    }

    public void setDisplayModuleErrors(boolean displayModuleErrors) {
        this.displayModuleErrors = displayModuleErrors;
    }

    public boolean willDeleteCommandMessages() {
        return deleteCommandMessages;
    }

    public void setDeleteCommandMessages(boolean deleteCommandMessages) {
        this.deleteCommandMessages = deleteCommandMessages;
    }

    //endregion

}

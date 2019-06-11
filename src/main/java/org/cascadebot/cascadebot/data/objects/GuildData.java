/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import com.google.common.collect.Sets;
import de.bild.codec.annotations.Id;
import de.bild.codec.annotations.PreSave;
import de.bild.codec.annotations.Transient;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.cascadebot.cascadebot.Cascade;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.ModuleFlag;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import org.cascadebot.cascadebot.utils.pagination.PageCache;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@BsonDiscriminator
public class GuildData {

    @Id
    private long guildID;

    //region Meta information
    private UUID stateLock = UUID.randomUUID(); // This is for checking state between the wrapper, bot and panel
    private Date creationDate = new Date();
    //endregion

    private ConcurrentHashMap<Class<? extends ICommandMain>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();
    private Set<Flag> enabledFlags = Sets.newConcurrentHashSet();

    @Setter
    private String prefix = Config.INS.getDefaultPrefix();

    private ConcurrentHashMap<String, Tag> tags = new ConcurrentHashMap<>();

    //region Guild data containers

    private GuildSettingsCore guildSettings = new GuildSettingsCore();
    private GuildPermissions guildPermissions = new GuildPermissions();
    /*
        Eventually these will be used but they're commented out for now

        private GuildModeration guildModeration = new GuildModeration();

    */

    //endregion

    //region Transient fields
    @Transient
    private ButtonsCache buttonsCache = new ButtonsCache(5);

    @Transient
    private PageCache pageCache = new PageCache();

    //endregion

    @PreSave
    public void preSave() {
        this.stateLock = UUID.randomUUID();
    }

    public GuildData(long guildID) {
        this.guildID = guildID;
    }

    //region Commands
    public void enableCommand(ICommandMain command) {
        if (command.getModule().isFlagEnabled(ModuleFlag.PRIVATE)) return;
        if (commandInfo.contains(command.getClass())) {
            commandInfo.get(command.getClass()).setEnabled(true);
        }
    }

    public void enableCommandByModule(Module module) {
        if (module.isFlagEnabled(ModuleFlag.PRIVATE)) return;
        for (ICommandMain command : Cascade.INS.getCommandManager().getCommandsByModule(module)) {
            enableCommand(command);
        }
    }

    public void disableCommand(ICommandMain command) {
        if (command.getModule().isFlagEnabled(ModuleFlag.PRIVATE)) return;
        commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command)).setEnabled(false);
    }

    public void disableCommandByModule(Module module) {
        if (module.isFlagEnabled(ModuleFlag.PRIVATE)) return;
        for (ICommandMain command : Cascade.INS.getCommandManager().getCommandsByModule(module)) {
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
        for (ICommandMain command : Cascade.INS.getCommandManager().getCommandsByModule(type)) {
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

    public Map<Class<? extends ICommandMain>, GuildCommandInfo> getCommandInfo() {
        return Collections.unmodifiableMap(commandInfo);
    }

    //endregion

    public boolean enableFlag(Flag flag) {
        return this.enabledFlags.add(flag);
    }

    public boolean disableFlag(Flag flag) {
        return this.enabledFlags.remove(flag);
    }

    public boolean isFlagEnabled(Flag flag) {
        return this.enabledFlags.contains(flag);
    }

    public void addButtonGroup(MessageChannel channel, Message message, ButtonGroup group) {
        group.setMessage(message.getIdLong());
        buttonsCache.put(channel.getIdLong(), message.getIdLong(), group);
    }

    public GuildSettingsCore getSettings() {
        return guildSettings;
    }

    public GuildPermissions getPermissions() {
        return guildPermissions;
    }

    public Collection<GuildCommandInfo> getGuildCommandInfos() {
        return Collections.unmodifiableCollection(commandInfo.values());
    }

    //endregion

}

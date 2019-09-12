/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import com.google.common.collect.Sets;
import de.bild.codec.annotations.Id;
import de.bild.codec.annotations.PreSave;
import de.bild.codec.annotations.Transient;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache;
import org.cascadebot.cascadebot.utils.pagination.PageCache;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@BsonDiscriminator
public class GuildData {

    @Id
    private long guildId;

    //region Meta information
    @GraphQLNonNull
    private UUID stateLock = UUID.randomUUID(); // This is for checking state between the wrapper, bot and panel

    @GraphQLNonNull
    private OffsetDateTime dbCreationTime = OffsetDateTime.now();
    //endregion

    @GraphQLIgnore
    private ConcurrentHashMap<Class<? extends ICommandMain>, GuildCommandInfo> commandInfo = new ConcurrentHashMap<>();

    @GraphQLNonNull
    private Set<Flag> enabledFlags = Sets.newConcurrentHashSet();

    @GraphQLNonNull
    @Setter
    private Locale locale = Locale.getDefaultLocale();

    //region Guild data containers

    @GraphQLNonNull
    @Setter
    private GuildSettingsCore coreSettings = new GuildSettingsCore(guildId);

    @GraphQLQuery(name = "permissions")
    @GraphQLNonNull
    @Setter
    private GuildPermissions guildPermissions = new GuildPermissions();
    /*
        Eventually these will be used but they're commented out for now

        private GuildModeration guildModeration = new GuildModeration();

    */

    //endregion

    //region Transient fields
    @Transient
    @GraphQLIgnore
    private ButtonsCache buttonsCache = new ButtonsCache(5);

    @Transient
    @GraphQLIgnore
    private PageCache pageCache = new PageCache();

    //endregion

    @PreSave
    public void preSave() {
        this.stateLock = UUID.randomUUID();
    }

    public GuildData(long guildId) {
        this.guildId = guildId;
    }

    //region Commands
    public void enableCommand(ICommandMain command) {
        if (command.getModule().isPrivate()) return;
        if (commandInfo.contains(command.getClass()) || !command.getModule().isDefault()) {
            getGuildCommandInfo(command).setEnabled(true);
        }
    }

    public void enableCommandByModule(Module module) {
        if (module.isPrivate()) return;
        for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommandsByModule(module)) {
            enableCommand(command);
        }
    }

    public void disableCommand(ICommandMain command) {
        if (command.getModule().isPrivate()) return;
        getGuildCommandInfo(command).setEnabled(false);
    }

    public void disableCommandByModule(Module module) {
        if (module.isPrivate()) return;
        for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommandsByModule(module)) {
            disableCommand(command);
        }
    }

    @GraphQLIgnore
    public boolean isCommandEnabled(ICommandMain command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).isEnabled();
        }
        return command.getModule().isDefault();
    }

    @GraphQLIgnore
    public boolean isModuleEnabled(Module module) {
        boolean enabled = module.isDefault();
        for (ICommandMain command : CascadeBot.INS.getCommandManager().getCommandsByModule(module)) {
            enabled &= commandInfo.get(command.getClass()).isEnabled();
        }
        return enabled;
    }

    @GraphQLIgnore
    public String getCommandName(ICommandMain command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getCommand();
        }
        return command.command(locale);
    }

    public void setCommandName(ICommandMain command, String commandName) {
        getGuildCommandInfo(command).setCommand(commandName);
    }

    @GraphQLIgnore
    public Set<String> getCommandAliases(ICommandMain command) {
        if (commandInfo.contains(command.getClass())) {
            return commandInfo.get(command.getClass()).getAliases();
        }
        return command.getGlobalAliases(locale);
    }

    @GraphQLIgnore
    public boolean addAlias(ICommandMain command, String alias) {
        return getGuildCommandInfo(command).addAlias(alias);
    }

    @GraphQLIgnore
    public boolean removeAlias(ICommandMain command, String alias) {
        return getGuildCommandInfo(command).removeAlias(alias);
    }

    @GraphQLIgnore
    @BsonIgnore
    private GuildCommandInfo getGuildCommandInfo(ICommandMain command) {
        return commandInfo.computeIfAbsent(command.getClass(), aClass -> new GuildCommandInfo(command, locale));
    }

    @GraphQLIgnore
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

    @GraphQLIgnore
    public boolean isFlagEnabled(Flag flag) {
        return this.enabledFlags.contains(flag);
    }

    public void addButtonGroup(MessageChannel channel, Message message, ButtonGroup group) {
        group.setMessage(message.getIdLong());
        buttonsCache.put(channel.getIdLong(), message.getIdLong(), group);
    }

    @GraphQLIgnore
    public GuildPermissions getPermissions() {
        return guildPermissions;
    }

    public Collection<GuildCommandInfo> getGuildCommandInfos() {
        return Collections.unmodifiableCollection(commandInfo.values());
    }

    //endregion

}

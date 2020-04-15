/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import com.google.common.collect.Sets;
import jdk.jfr.Name;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.ModuleFlag;
import org.cascadebot.cascadebot.data.Config;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SettingsContainer(module = Module.CORE)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuildSettingsCore {

    public static Map<String, Field> VALUES = new HashMap<>();

    private long guildId;

    static {
        for (Field field : GuildSettingsCore.class.getDeclaredFields()) {
            if (field.getName().equals("VALUES")) continue;
            if (field.getAnnotation(Setting.class) == null || !field.getAnnotation(Setting.class).directlyEditable()) continue;
            field.setAccessible(true);
            VALUES.put(field.getName().toLowerCase(), field);
        }
    }

    //region Boolean flags
    @Setting
    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix

    @Setting
    private boolean deleteCommand = true;

    @Setting
    private boolean useEmbedForMessages = true;

    @Setting
    private boolean showPermErrors = true; // Whether commands will silently fail on no permissions

    @Setting
    private boolean showModuleErrors = true;

    @Setting
    private boolean adminsHaveAllPerms = true;

    @Setting
    private boolean allowTagCommands = true; // Whether tag commands will be executed by ;<tagname>

    @Setting
    private boolean helpHideCommandsNoPermission = true;

    @Setting
    private boolean helpShowAllModules = false;

    //endregion

    @Setting(directlyEditable = false)
    private Set<Module> enabledModules = Sets.newConcurrentHashSet(Module.getModules(ModuleFlag.DEFAULT));

    @Setting(directlyEditable = false)
    private Set<CommandFilter> commandFilters = Sets.newConcurrentHashSet();

    @Setting(directlyEditable = false)
    private String prefix = Config.INS.getDefaultPrefix();

    @Setting(directlyEditable = false)
    private ConcurrentHashMap<String, Tag> tags = new ConcurrentHashMap<>();

    public GuildSettingsCore(long guildId) {
        this.guildId = guildId;
    }

    //region Modules
    public boolean enableModule(Module module) {
        if (module.isPrivate()) {
            throw new IllegalArgumentException("This module is not available to be enabled!");
        }
        return this.enabledModules.add(module);
    }

    public boolean disableModule(Module module) {
        if (module.isPrivate()) {
            throw new IllegalArgumentException("This module is not available to be disabled!");
        } else if (module.isRequired()) {
            throw new IllegalArgumentException(String.format("Cannot disable the %s module!", module.toString().toLowerCase()));
        }
        return this.enabledModules.remove(module);
    }

    public boolean isModuleEnabled(Module module) {
        boolean isEnabled = this.enabledModules.contains(module);
        if (!isEnabled && module.isRequired()) {
            this.enabledModules.add(module);
            return true;
        }
        return isEnabled;
    }

    //endregion

    public Map<String, Tag> getTags() {
        return Collections.unmodifiableMap(tags);
    }

    public Tag getTag(String key) {
        return tags.get(key);
    }

    public boolean hasTag(String key) {
        return tags.containsKey(key);
    }

    public void addTag(String key, Tag tag) {
        tags.put(key, tag);
    }

    public boolean removeTag(String key) {
        return tags.remove(key) != null;
    }

    public Set<CommandFilter> getCommandFilters() {
        return Collections.unmodifiableSet(commandFilters);
    }

    public boolean hasCommandFilter(String name) {
        return getCommandFilter(name) != null;
    }

    public CommandFilter getCommandFilter(String name) {
        return commandFilters.stream()
                .filter(filter -> filter.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean addCommandFilter(CommandFilter filter) {
        return commandFilters.add(filter);
    }

    public boolean removeCommandFilter(String name) {
        return commandFilters.removeIf(filter -> filter.getName().equals(name));
    }

}

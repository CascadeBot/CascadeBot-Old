/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import com.google.common.collect.Sets;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
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
public class GuildSettingsCore {

    public static Map<String, Field> VALUES = new HashMap<>();

    static {
        for (Field field : GuildSettingsCore.class.getDeclaredFields()) {
            if (field.getName().equals("VALUES")) continue;
            if (field.getAnnotation(Setting.class) != null && !field.getAnnotation(Setting.class).directlyEditable()) continue;
            field.setAccessible(true);
            VALUES.put(field.getName().toLowerCase(), field);
        }
    }

    //region Boolean flags
    @Setting(niceName = "Mention Prefix")
    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix

    @Setting(niceName = "Delete command after execution")
    private boolean deleteCommand = true;

    @Setting(niceName = "Use embeds for messages")
    private boolean useEmbedForMessages = true;

    @Setting(niceName = "Show permission errors")
    private boolean showPermErrors = true; // Whether commands will silently fail on no permissions

    @Setting(niceName = "Show module errors")
    private boolean showModuleErrors = false;

    @Setting(niceName = "Admins have all permissions")
    private boolean adminsHaveAllPerms = true;

    @Setting(niceName = "Allow tags to be used as commands")
    private boolean allowTagCommands = true; // Whether tag commands will be executed by ;<tagname>
    //endregion

    @Setting(niceName = "Enabled modules", directlyEditable = false)
    private Set<Module> enabledModules = Sets.newConcurrentHashSet(
            Sets.newHashSet(
                    Module.CORE,
                    Module.MANAGEMENT,
                    Module.INFORMATIONAL
            )
    );

    @Setting(niceName = "Prefix", directlyEditable = false)
    private String prefix = Config.INS.getDefaultPrefix();

    @Setting(niceName = "Tags", directlyEditable = false)
    private ConcurrentHashMap<String, Tag> tags = new ConcurrentHashMap<>();

    public boolean isMentionPrefix() {
        return mentionPrefix;
    }

    public void setMentionPrefix(boolean mentionPrefix) {
        this.mentionPrefix = mentionPrefix;
    }

    public boolean willDeleteCommand() {
        return deleteCommand;
    }

    public void setDeleteCommand(boolean deleteCommand) {
        this.deleteCommand = deleteCommand;
    }

    public boolean useEmbedForMessages() {
        return useEmbedForMessages;
    }

    public void setUseEmbedForMessages(boolean useEmbedForMessages) {
        this.useEmbedForMessages = useEmbedForMessages;
    }

    public boolean willShowPermErrors() {
        return showPermErrors;
    }

    public void setShowPermErrors(boolean showPermErrors) {
        this.showPermErrors = showPermErrors;
    }

    public boolean willDisplayModuleErrors() {
        return showModuleErrors;
    }

    public void setShowModuleErrors(boolean showModuleErrors) {
        this.showModuleErrors = showModuleErrors;
    }

    public boolean doAdminsHaveAllPerms() {
        return adminsHaveAllPerms;
    }

    public void setAdminsHaveAllPerms(boolean adminsHaveAllPerms) {
        this.adminsHaveAllPerms = adminsHaveAllPerms;
    }

    public boolean willAllowTagCommands() { return allowTagCommands; }

    public void setAllowTagCommands(boolean allowTagCommands) { this.allowTagCommands = allowTagCommands; }

    //region Modules
    public boolean enableModule(Module module) {
        if (module.isFlagEnabled(ModuleFlag.PRIVATE)) {
            throw new IllegalArgumentException("This module is not available to be enabled!");
        }
        return this.enabledModules.add(module);
    }

    public boolean disableModule(Module module) {
        if (module.isFlagEnabled(ModuleFlag.PRIVATE)) {
            throw new IllegalArgumentException("This module is not available to be disabled!");
        } else if (module.isFlagEnabled(ModuleFlag.REQUIRED)) {
            throw new IllegalArgumentException(String.format("Cannot disable the %s module!", module.toString().toLowerCase()));
        }
        return this.enabledModules.remove(module);
    }

    public boolean isModuleEnabled(Module module) {
        boolean isEnabled = this.enabledModules.contains(module);
        if (!isEnabled && module.isFlagEnabled(ModuleFlag.REQUIRED)) {
            this.enabledModules.add(module);
            return true;
        }
        return isEnabled;
    }

    public Set<Module> getEnabledModules() {
        return Collections.unmodifiableSet(enabledModules);
    }
    //endregion

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Map<String, Tag> getTagInfo() { return Collections.unmodifiableMap(tags); }

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

}

/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.objects;

import com.cascadebot.cascadebot.commandmeta.ICommand;

import java.util.Set;

public class GuildCommandInfo {

    private boolean enabled;
    private boolean forceDefault;
    private String command;
    private String defaultCommand;
    private Set<String> aliases;

    public GuildCommandInfo(ICommand command) {
        this.command = command.defaultCommand();
        this.defaultCommand = command.defaultCommand();
        this.forceDefault = command.forceDefault();
        this.aliases = command.getGlobalAliases();
        this.enabled = true;
    }

    public GuildCommandInfo(String command, String defaultCommand, Set<String> aliases, boolean enabled, boolean forceDefault) {
        this.command = command;
        this.defaultCommand = defaultCommand;
        this.aliases = aliases;
        this.enabled = enabled;
        this.forceDefault = forceDefault;
    }

    public String getCommand() {
        return command;
    }

    public GuildCommandInfo setCommand(String command) {
        if (this.forceDefault)
            throw new UnsupportedOperationException("This command's main command cannot be changed!");
        this.command = command;
        return this;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public boolean addAlias(String alias) {
        return this.aliases.add(alias);
    }

    public boolean removeAlias(String alias) {
        return this.aliases.remove(alias);
    }

    public GuildCommandInfo setAliases(Set<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public GuildCommandInfo setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getDefaultCommand() {
        return defaultCommand;
    }

}

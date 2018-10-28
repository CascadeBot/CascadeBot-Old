/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.objects;

import com.cascadebot.cascadebot.commandmeta.ICommand;
import org.apache.commons.lang3.ArrayUtils;

public class GuildCommandInfo {

    private boolean enabled;
    private boolean forceDefault;
    private String command;
    private String defaultCommand;
    private String[] aliases;

    public GuildCommandInfo(ICommand command) {
        this.command = command.defaultCommand();
        this.defaultCommand = command.defaultCommand();
        this.forceDefault = command.forceDefault();
        this.aliases = command.getGlobalAliases();
        this.enabled = true;
    }

    public GuildCommandInfo(String command, String defaultCommand, String[] aliases, boolean enabled, boolean forceDefault) {
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
        if (this.forceDefault) throw new UnsupportedOperationException("This command's main command cannot be changed!");
        this.command = command;
        return this;
    }

    public String[] getAliases() {
        return aliases;
    }

    public GuildCommandInfo addAlias(String alias) {
        ArrayUtils.add(this.aliases, alias);
        return this;
    }

    public GuildCommandInfo setAliases(String[] aliases) {
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

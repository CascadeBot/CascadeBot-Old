/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GuildCommandInfo {

    private boolean enabled;
    private boolean forceDefault;
    private String command;
    private String defaultCommand;
    private Set<String> aliases;

    public GuildCommandInfo(ICommandMain command) {
        this.command = command.command();
        this.defaultCommand = command.command();
        this.forceDefault = command.forceDefault();
        this.aliases = command.getGlobalAliases();
        this.enabled = true;
    }

    public GuildCommandInfo setCommand(String command) {
        if (this.forceDefault)
            throw new UnsupportedOperationException("This command's main command cannot be changed!");
        this.command = command;
        return this;
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

    public GuildCommandInfo setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

}

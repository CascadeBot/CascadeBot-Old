package com.cascadebot.cascadebot.objects;

import com.cascadebot.cascadebot.commands.Command;
import org.apache.commons.lang3.ArrayUtils;

public class GuildCommandInfo {

    private boolean disabled;
    private String command;
    private String[] aliases;

    public GuildCommandInfo(Command command) {
        this.command = command.defaultCommand();
        this.aliases = command.getGlobalAliases();
        this.disabled = false;
    }

    public GuildCommandInfo(String command, String[] aliases, boolean disabled) {
        this.command = command;
        this.aliases = aliases;
        this.disabled = disabled;
    }

    public String getCommand() {
        return command;
    }

    public GuildCommandInfo setCommand(String command) {
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

    public boolean isDisabled() {
        return disabled;
    }

    public GuildCommandInfo setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

}

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.permissions.Permission;

import java.util.Set;

public interface IMainCommand extends ICommandExecutable {

    public CommandType getType();

    public Permission getPermission();

    default boolean forceDefault() {
        return false;
    }

    default Set<String> getGlobalAliases() {
        return Set.of();
    }

    default Set<ICommandExecutable> getSubCommands() { return Set.of(); }

}

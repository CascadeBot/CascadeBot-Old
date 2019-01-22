/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public interface ICommand {

    public void onCommand(Member sender, CommandContext context);

    public String defaultCommand();

    public CommandType getType();

    public Permission getPermission();

    default boolean forceDefault() {
        return false;
    }

    default Set<String> getGlobalAliases() {
        return Set.of();
    }

}

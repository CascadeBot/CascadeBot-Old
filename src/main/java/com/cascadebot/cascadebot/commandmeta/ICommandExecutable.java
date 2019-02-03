/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import com.cascadebot.cascadebot.permissions.Permission;
import net.dv8tion.jda.core.entities.Member;

public interface ICommandExecutable {

    void onCommand(Member sender, CommandContext context);

    String command();

    Permission getPermission();

}

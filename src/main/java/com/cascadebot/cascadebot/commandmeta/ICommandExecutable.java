/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import net.dv8tion.jda.core.entities.Member;

public interface ICommandExecutable {

    public void onCommand(Member sender, CommandContext context);

    public String command();

}

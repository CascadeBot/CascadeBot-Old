/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import net.dv8tion.jda.core.entities.Guild;

public class CommandException extends RuntimeException {

    public CommandException(Exception e, Guild guild, String trigger) {
        super("Error while processing command!\nGuild ID: " + guild.getId() + (trigger.isBlank() ? "" : " Trigger: " + trigger), e);
    }

}

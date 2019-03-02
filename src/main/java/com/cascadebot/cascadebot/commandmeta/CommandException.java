/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import net.dv8tion.jda.core.entities.Guild;

import java.io.PrintStream;
import java.io.PrintWriter;

public class CommandException extends RuntimeException {

    public CommandException(Throwable cause, Guild guild, String trigger) {
        super("Error while processing command!\nGuild ID: " + guild.getId() + (trigger.isBlank() ? "" : " Trigger: " + trigger), cause);
    }

    /*

        We don't want the main exception to be printed as it is just a wrapper.
        That is why these exist :P

    */

    @Override
    public void printStackTrace(PrintWriter s) {
        s.println(getMessage());
        getCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.println(getMessage());
        getCause().printStackTrace(s);
    }

}

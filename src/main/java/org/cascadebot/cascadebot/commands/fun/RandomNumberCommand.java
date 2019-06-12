/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
 
package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import java.io.IOException;mport java.util.Random;


public class RandCommand extends ICommandMain
{
    @Override
    public void onCommand(Member sender, CommandContext context)
        Message message = event.getMessage();
        String content = message.getContentRaw(); 
        Random random_obj = new Random();
        int random_number = random_obj.nextInt(999);
        MessageChannel channel = event.getChannel();
        channel.sendMessage(random_number).queue();
    @Override
    public String command() {
        return "rand";
    }
 
    @Override
    public Module getModule() {
        return Module.FUN;
    }
 
    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Rand command", "rand", true);
    }
 
    @Override
    public String description() {
        return "Returns a random number";
    }
 
}

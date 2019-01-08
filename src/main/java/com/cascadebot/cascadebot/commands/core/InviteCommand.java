/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.CommandType;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class InviteCommand implements ICommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        public static String getInvite() {
            // return String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s",
                    CascadeBot.INS.getSelfUser().getId(), Permission.ALL_GUILD_PERMISSIONS);
        }
    }

    @Override
    public String defaultCommand() {
        return "invite";
    }

    @Override
    public boolean forceDefault() {
        return true;
    }

    @Override
    public CommandType getType() {
        return CommandType.CORE;
    }

}

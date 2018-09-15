package com.cascadebot.cascadebot.commands.core;

import com.cascadebot.cascadebot.commands.Command;
import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class HelpCommand implements Command {
    @Override
    public void onCommand(Member sender, GuildData data, TextChannel channel, String[] args) {

    }

    @Override
    public String defaultCommand() {
        return "help";
    }

}
